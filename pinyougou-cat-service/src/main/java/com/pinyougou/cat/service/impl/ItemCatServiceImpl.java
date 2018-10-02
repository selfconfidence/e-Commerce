package com.pinyougou.cat.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cat.service.ItemCatService;
import com.pinyougou.entitygroup.ItemCat;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;

/**
 * @author asus
 *
 */
/**
 * @author asus
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Override // 参数就是一个购物车列表, 还有具体的skuid 以及购买的数量
	public List<ItemCat> addItemCat(List<ItemCat> catList, Long itemId, Integer num) {
		// 1.根据商品SKU ID查询SKU商品信息
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		// 认证SKU是否符合标准
		if (tbItem == null) {
			throw new RuntimeException("没有此SKU信息");
		}
		if (!tbItem.getStatus().equals("1")) {
			throw new RuntimeException("SKU暂无审核");
		}
		

		// 2.获取商家ID
		String sellerId = tbItem.getSellerId();
		// 3.根据商家ID判断购物车列表中是否存在该商家的购物车
		ItemCat itemCat = topCat(catList, sellerId);
		if (itemCat == null) {

			// 4.如果购物车列表中不存在该商家的购物车
			// 4.1 新建购物车对象
			itemCat = new ItemCat();
			itemCat.setSellerId(tbItem.getSellerId());
			itemCat.setSellerName(tbItem.getSeller());
			List<TbOrderItem> list = new ArrayList();
			// 抽取的方法 如果不存在商家信息那么就新建一个购物车对象
			list.add(addItemCatObject(tbItem, num));
			// 4.2 将新建的购物车对象添加到购物车列表

			itemCat.setOrderItem(list);
			// 这里是一个细节问题 如果改购物车列表没有对应商家的信息,那么就需要为这个商家重新创建一个新的购物车列表对象
			catList.add(itemCat);
		} else {
			// 查询购物车明细列表中是否存在该商品
			TbOrderItem existItem = existItem(itemCat.getOrderItem(), itemId);
			if (existItem == null) {

				// 5.1. 如果没有，新增购物车明细
				existItem = addItemCatObject(tbItem, num);
				// 新添加到购物车明细中
				itemCat.getOrderItem().add(existItem);
			} else {
				// 5.2. 如果有，在原购物车明细上添加数量，更改金额
				existItem.setNum(existItem.getNum() + num);
				existItem.setTotalFee(new BigDecimal(existItem.getPrice().doubleValue() * existItem.getNum()));
				if (existItem.getNum() <= 0) {
					itemCat.getOrderItem().remove(existItem);
				}
				if (itemCat.getOrderItem().size() == 0) {
					catList.remove(itemCat);
				}
			}
		}
		return catList;

	}

	private ItemCat topCat(List<ItemCat> catList, String sellerId) {

		for (ItemCat itemCat : catList) {
			// 判断购物车列表中的购物者对象是否有这个商家的信息
			if (itemCat.getSellerId().equals(sellerId)) {
				return itemCat;
			}
		}

		return null;

	}

	private TbOrderItem addItemCatObject(TbItem tbItem, Integer num) {
		// 新添加Cat数据
		TbOrderItem tb = new TbOrderItem();
		tb.setGoodsId(tbItem.getGoodsId());
		tb.setItemId(tbItem.getId());
		tb.setNum(num);
		tb.setPicPath(tbItem.getImage());
		tb.setPrice(tbItem.getPrice());
		tb.setTitle(tbItem.getTitle());
		tb.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue() * num));
		tb.setSellerId(tbItem.getSellerId());
		return tb;

	}

	// 判断当前Cat是否有对应的ItemId信息
	private TbOrderItem existItem(List<TbOrderItem> orderItem, Long itemId) {
		for (TbOrderItem tbOrderItem : orderItem) {
			if (tbOrderItem.getItemId().equals(itemId)) {
				return tbOrderItem;
			}
		}
		return null;
	}

	@Override
	public List<ItemCat> findRedisItemCart(String userName) {

		return (List<ItemCat>) redisTemplate.boundHashOps("itemCartList").get(userName);
	}

	@Override
	public void addRedisItemCart(List<ItemCat> catList, String userName) {
		redisTemplate.boundHashOps("itemCartList").put(userName, catList);

	}

                                                  
	// 合并购物车业务逻辑操作
	@Override
	public List<ItemCat> mergeCartList(List<ItemCat> catList1, List<ItemCat> catList2) {
		// 合并购物车逻辑 不能使用all的方法如果使用了那么就会追加造成非常多的重复数据 我们可以利用添加购物车方法使其合并即可
		for (ItemCat itemCat : catList1) {// 循环购物车列表
			for (TbOrderItem tbOrderItem : itemCat.getOrderItem()) {// 循环购物车明细
				// 重复使用了 因为添加购物车的方法也是合并的操作 根据sku判断当前商家id,和商品数量进行合并
				catList2 = addItemCat(catList2, tbOrderItem.getItemId(), tbOrderItem.getNum());

			}
		}
		return catList2;
	} 
	
	
	// 删除购物车明细操作
	@Override
	public List<ItemCat> deleteItemCart(List<ItemCat> itemCart, Long itemId) {
		ItemCat removeItemCat = null;

		for (ItemCat itemCat : itemCart) {
			List<TbOrderItem> order = itemCat.getOrderItem();
			// 第一层循环遍历是迭代器形式的
			for (int i = 1; i <= order.size(); i++) {
				// 第二次循环遍历由于需要操作本身集合数据所以不能使用迭代器的方式
				// 为什么要 i-1;
				// 在List集合中删除数据的时候删除一个就会停留在当前下标,如果在根据这这样的下标操作那么就会indexOfException
				// 所以使用 i-1 的操作来防止这种事情的发生
				if (order.get(i - 1).getItemId().equals(itemId)) {
					// 如果当前购物车详情item中包含指定的商品id那么就要进行删除操作
					TbOrderItem tbOrderItem = order.get(i - 1);
					order.remove(tbOrderItem);

					if (itemCat.getOrderItem().size() == 0) {
						// 判断删除过后当前购物车对象的购物详情还有没有数据,如果没有就要进行删除购物车对象
						// 但是购物车列表是迭代的所以不能直接删除,因此定义一个购物车对象,如果有的那么就赋值
						removeItemCat = itemCat;
					}

				}

			}

		}
		// 最后判断当前定义的购物车对象,查看是否被赋值,如果赋值就不是null就直接删除即可
		if (removeItemCat != null) {
			itemCart.remove(removeItemCat);
		}
		// 最后将这个修改过数据的购物车列表返回
		return itemCart;

	}

	@Override
	public List<ItemCat> deleteItemCartOne(List<ItemCat> itemCart, Long itemId) {
		// 这个操作用来删除购物车列表中还有一个购物车对象
		ItemCat itemCat = itemCart.get(0);
		// 由于在前端已经判断好了 我们直接get(0),拿数据即可
		for (int i = 0; i < itemCat.getOrderItem().size(); i++) {
			// 遍历购物车详细列表 查看是否有对应的目标数据
			if (itemCat.getOrderItem().get(i).getItemId().equals(itemId)) {
				// 如果有直接删除
				itemCat.getOrderItem().remove(itemCat.getOrderItem().get(i));
				// 最后判断这个购物车对象的购物详情是否为0 如果为0直接清空购物车列表
				if (itemCat.getOrderItem().size() == 0) {
					itemCart.clear();
				}
			}
		}
		// 返回购物车列表
		return itemCart;
	}

}
