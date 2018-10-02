package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entitygroup.ItemCat;
import com.pinyougou.entitygroup.OrderIndent;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderIdToString;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbPayLogExample;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private IdWorker idWorker;
	@Autowired
	private TbPayLogMapper tbPayLogMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		// 根据当前ID获取redis的购物车数据
		List<ItemCat> tbCart = (List<ItemCat>) redisTemplate.boundHashOps("itemCartList").get(order.getUserId());
		List<String> order_list = new ArrayList<>();
		double toto_money = 0d;
		for (ItemCat itemCat : tbCart) {
			// 循环购物车列表,原因,一个订单应该对应一个商家
			TbOrder tbOrder = new TbOrder();
			tbOrder.setOrderId(idWorker.nextId());// 使用雪花算法的工具类对ID进行赋值操作
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setPostFee(order.getPostFee());
			tbOrder.setStatus(order.getStatus());
			tbOrder.setUpdateTime(new Date());
			tbOrder.setCreateTime(new Date());
			tbOrder.setUserId(order.getUserId());
			tbOrder.setBuyerNick(order.getUserId());
			tbOrder.setReceiver(order.getReceiver());
			tbOrder.setReceiverMobile(order.getReceiverMobile());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());
			tbOrder.setInvoiceType(order.getInvoiceType());
			tbOrder.setSourceType(order.getSourceType());
			tbOrder.setSellerId(itemCat.getSellerId());
			double totofee = 0d;// 最后对订单的总金额做累加操作
			for (TbOrderItem tbOrderItem : itemCat.getOrderItem()) {
				// 添加订单详情表数据 它的ID也是雪花算法的表达式
				tbOrderItem.setId(idWorker.nextId());
				tbOrderItem.setOrderId(tbOrder.getOrderId());
				totofee += tbOrderItem.getTotalFee().doubleValue();
				tbOrderItemMapper.insert(tbOrderItem);
			}
			toto_money += totofee;
			tbOrder.setPayment(new BigDecimal(totofee));// 实付金额
			// 注意这是两层循环 第一层循环订单 第二层循环订单详情
			orderMapper.insert(tbOrder);
			// 添加订单id
			order_list.add(tbOrder.getOrderId() + "");
		}
		// 添加日志表并存储到redis中
		// 一个日志表对应多个订单 OK?
		TbPayLog tbPayLog = new TbPayLog();
		tbPayLog.setUserId(order.getUserId());// 获取当前用户名称
		tbPayLog.setOutTradeNo(idWorker.nextId() + "");// 订单日志单号
		tbPayLog.setCreateTime(new Date());
		Double moneyfee = toto_money * 100;
		tbPayLog.setTotalFee(moneyfee.longValue());// 精确到分
		tbPayLog.setTradeState("0");
		// 如果list不进行处理,直接toString,格式就会是[1,2] 我们需要的是1,2 所以需要进行处理
		tbPayLog.setOrderList(order_list.toString().replace("[", "").replace("]", ""));
		// 添加数据库
		tbPayLogMapper.insert(tbPayLog);
		// 存储redis //以当前用户作为小key
		redisTemplate.boundHashOps("payLog").put(order.getUserId(), tbPayLog);

		// 最后在清除redis的数据即可.
		redisTemplate.boundHashOps("itemCartList").delete(order.getUserId());

	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order) {
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id) {
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			orderMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();

		if (order != null) {
			if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
				criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
			}
			if (order.getPostFee() != null && order.getPostFee().length() > 0) {
				criteria.andPostFeeLike("%" + order.getPostFee() + "%");
			}
			if (order.getStatus() != null && order.getStatus().length() > 0) {
				criteria.andStatusLike("%" + order.getStatus() + "%");
			}
			if (order.getShippingName() != null && order.getShippingName().length() > 0) {
				criteria.andShippingNameLike("%" + order.getShippingName() + "%");
			}
			if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
				criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
			}
			if (order.getUserId() != null && order.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + order.getUserId() + "%");
			}
			if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
				criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
			}
			if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
				criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
			}
			if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
				criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
			}
			if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
				criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
			}
			if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
			}
			if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
				criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
			}
			if (order.getReceiver() != null && order.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + order.getReceiver() + "%");
			}
			if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
				criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
			}
			if (order.getSourceType() != null && order.getSourceType().length() > 0) {
				criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
			}
			if (order.getSellerId() != null && order.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + order.getSellerId() + "%");
			}

		}

		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateOrderLog(String goodsId, String transaction_id) {
		// 更改订单状态数据
		TbPayLog tbpatlog = tbPayLogMapper.selectByPrimaryKey(goodsId);
		tbpatlog.setPayTime(new Date());
		tbpatlog.setTransactionId(transaction_id);
		tbpatlog.setTradeState("1");
		tbpatlog.setPayType("1");
		tbPayLogMapper.updateByPrimaryKeySelective(tbpatlog);
		String orderList = tbpatlog.getOrderList();
		String[] orderId = orderList.split(",");
		// 订单id 修改状态
		for (String string : orderId) {
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(string));
			order.setStatus("2");
			order.setUpdateTime(new Date());
			order.setPaymentTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
		}
		// 完事之后删除redis
		redisTemplate.boundHashOps("payLog").delete(tbpatlog.getUserId());

	}

	@Override
	public PageResult findOrder(String userName, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<OrderIndent> itemCatList = new ArrayList();
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userName);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		for (TbOrder tbOrder : page.getResult()) {
			OrderIndent orderIndent = new OrderIndent();
			TbOrderItemExample itemExample = new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria criteria2 = itemExample.createCriteria();
			criteria2.andOrderIdEqualTo(tbOrder.getOrderId());
			List<TbOrderItem> list = tbOrderItemMapper.selectByExample(itemExample);
			TbOrderIdToString addTbOrderIdToString = addTbOrderIdToString(tbOrder);
			orderIndent.setTbOrder(addTbOrderIdToString);
			orderIndent.setTbOrderItem(list);
			itemCatList.add(orderIndent);

		}
		return new PageResult((long)page.getPages(), itemCatList);
	}
	
	//没办法转化pojo操作
    private TbOrderIdToString addTbOrderIdToString( TbOrder tbOrder){
    	TbOrderIdToString tbOrderIdToString = new TbOrderIdToString();
		tbOrderIdToString.setOrderId(Long.toString(tbOrder.getOrderId()));
		tbOrderIdToString.setBuyerMessage(tbOrder.getBuyerMessage());
		tbOrderIdToString.setBuyerNick(tbOrder.getBuyerNick());
		tbOrderIdToString.setBuyerRate(tbOrder.getBuyerRate());
		tbOrderIdToString.setCloseTime(tbOrder.getCloseTime());
		tbOrderIdToString.setConsignTime(tbOrder.getConsignTime());
		tbOrderIdToString.setCreateTime(tbOrder.getCreateTime());
		tbOrderIdToString.setEndTime(tbOrder.getEndTime());
		tbOrderIdToString.setExpire(tbOrder.getExpire());
		tbOrderIdToString.setInvoiceType(tbOrder.getInvoiceType());
		tbOrderIdToString.setPayment(tbOrder.getPayment());
		tbOrderIdToString.setPaymentTime(tbOrder.getPaymentTime());
		tbOrderIdToString.setPaymentType(tbOrder.getPaymentType());
		tbOrderIdToString.setPostFee(tbOrder.getPostFee());
		tbOrderIdToString.setReceiver(tbOrder.getReceiver());
		tbOrderIdToString.setReceiverAreaName(tbOrder.getReceiverAreaName());
		tbOrderIdToString.setReceiverMobile(tbOrder.getReceiverMobile());
		tbOrderIdToString.setReceiverZipCode(tbOrder.getReceiverZipCode());
		tbOrderIdToString.setSellerId(tbOrder.getSellerId());
		tbOrderIdToString.setShippingCode(tbOrder.getShippingCode());
		tbOrderIdToString.setShippingName(tbOrder.getShippingName());
		tbOrderIdToString.setSourceType(tbOrder.getSourceType());
		tbOrderIdToString.setStatus(tbOrder.getStatus());
		tbOrderIdToString.setUpdateTime(tbOrder.getUpdateTime());
		tbOrderIdToString.setUserId(tbOrder.getUserId());
		return tbOrderIdToString;
    }
	@Override
	public String skipPay(String orderId) {
		TbPayLogExample example = new TbPayLogExample();
		com.pinyougou.pojo.TbPayLogExample.Criteria criteria = example.createCriteria();
		criteria.andTradeStateEqualTo("0");
		List<TbPayLog> byExample = tbPayLogMapper.selectByExample(example );
		for (TbPayLog tbPayLog : byExample) {
			String orderList = tbPayLog.getOrderList();
			String[] strings = orderList.split(",");
			for (String string : strings) {
				System.out.println(string);
				if (string.trim().equals(orderId.trim())) {
					redisTemplate.boundHashOps("payOrderUser").put(tbPayLog.getUserId(), tbPayLog);
					return "";
				}
			}
		}
		return null;

	}

	@Override
	public List<TbOrder> initId(String name) {
		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(name);
		List<TbOrder> byExample = orderMapper.selectByExample(example);
		for (TbOrder tbOrder : byExample) {
			System.err.println(tbOrder.getOrderId());
		}
		return byExample;
	}

	@Override
	public String updateUserPay(String name ,String transactionId) {
		TbPayLog tbPayLog =(TbPayLog) redisTemplate.boundHashOps("payOrderUser").get(name);
		tbPayLog.setTransactionId(transactionId);
		tbPayLog.setPayTime(new Date());
		tbPayLog.setPayType("1");
		tbPayLog.setTradeState("1");
		String list = tbPayLog.getOrderList();
		String[] split = list.split(",");
		for (String string : split) {
			TbOrder primaryKey = orderMapper.selectByPrimaryKey(Long.valueOf(string));
			primaryKey.setStatus("2");
			primaryKey.setPaymentTime(new Date());
			primaryKey.setPaymentType("1");
			orderMapper.updateByPrimaryKeySelective(primaryKey);
		}
		tbPayLogMapper.updateByPrimaryKeySelective(tbPayLog);
		redisTemplate.boundHashOps("payOrderUser").delete(name);
		return tbPayLog.getOutTradeNo();
		
	}

	@Override
	public void updateUserAddress(String logId, TbAddress tbAddress) {
		TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(logId);
		 String[] split = tbPayLog.getOrderList().split(",");
		 for (String string : split) {
			TbOrder primaryKey = orderMapper.selectByPrimaryKey(Long.valueOf(string));
			primaryKey.setReceiver(tbAddress.getContact());
			primaryKey.setReceiverMobile(tbAddress.getMobile());
			primaryKey.setReceiverAreaName(tbAddress.getAddress());
			primaryKey.setInvoiceType(tbAddress.getAlias());
			orderMapper.updateByPrimaryKeySelective(primaryKey);
		}
	}

}
