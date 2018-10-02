package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entitygroup.Goods;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());
		
		goods.getDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getDesc());
		
		update(goods);
			
		}
	   private void updateItems(Goods goods){
		   if("1".equals(goods.getGoods().getIsEnableSpec())){
				List<TbItem> list = goods.getItemList();
			for (TbItem tbItem : list) {
				  String title=goods.getGoods().getGoodsName();
				  JSONObject specMap = JSON.parseObject(tbItem.getSpec());
				  Map<String,String> javaObject = specMap.toJavaObject(Map.class);
					for(String string : javaObject.keySet()){
						title +=" "+javaObject.get(string);
					}
					tbItem.setTitle(title);
					numList(tbItem, goods);
					itemMapper.insertSelective(tbItem);
			}
				}else{
					
					TbItem item = new TbItem();
					item.setTitle(goods.getGoods().getGoodsName());
					item.setStatus("1");
					item.setIsDefault("1");
					item.setNum(9999);
					item.setSpec("{}");
					
					numList(item, goods);
					itemMapper.insertSelective(item);
				}
			
		   
	   }
	private void numList(TbItem tbItem,Goods goods){
		
		
		tbItem.setCategoryid(goods.getGoods().getCategory3Id());
		
		tbItem.setCreateTime(new Date());
		tbItem.setUpdateTime(new Date());
		tbItem.setGoodsId(goods.getGoods().getId());
		tbItem.setSellerId(goods.getGoods().getSellerId());
		TbItemCat selectByPrimaryKey = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		tbItem.setCategory(selectByPrimaryKey.getName());
		TbSeller selectByPrimaryKey2 = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		tbItem.setSeller(selectByPrimaryKey2.getNickName());
		 List<Map> parseArray = JSON.parseArray(goods.getDesc().getItemImages(),Map.class);
		tbItem.setImage((String)parseArray.get(0).get("url"));
		
	}
		
	

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getDesc());
		  //只需要将goods所有关联的SKU全部删除   然后在进行insert操作即可
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		
		//删除只后重新添加即可 分装的这个方法就和昨天的添加操作是一样的;
		updateItems(goods);
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		//查询商品的主信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		//查询商品的附加信息
		goods.setGoods(tbGoods);
		TbGoodsDesc selectByPrimaryKey = goodsDescMapper.selectByPrimaryKey(id);
		  goods.setDesc(selectByPrimaryKey);
		//查询商品的SPU信息
		
      TbItemExample example = new TbItemExample();
      com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
	 criteria.andGoodsIdEqualTo(id);
      List<TbItem> selectByExample = itemMapper.selectByExample(example);		  
		goods.setItemList(selectByExample);
      return goods; //;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		TbGoods selectByPrimaryKey = goodsMapper.selectByPrimaryKey(id);
		selectByPrimaryKey.setIsDelete("1");
		goodsMapper.updateByPrimaryKey(selectByPrimaryKey);
		}		//如果spu要进行删除那么对应的sku也要进行逻辑删除操作
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		List<TbItem> selectByExample = itemMapper.selectByExample(example );
		for (TbItem tbItem : selectByExample) {
			tbItem.setStatus("0");
			itemMapper.updateByPrimaryKey(tbItem);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void audit(Long[] ids, String status) {
			for (Long id : ids) {
				TbGoods selectByPrimaryKey = goodsMapper.selectByPrimaryKey(id);
			       selectByPrimaryKey.setAuditStatus(status);
			       goodsMapper.updateByPrimaryKey(selectByPrimaryKey);
			}
			TbItemExample example  = new TbItemExample();
			com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdIn(Arrays.asList(ids));
			List<TbItem> selectByExample = itemMapper.selectByExample(example  );
			if(selectByExample.size()>0){

				for (TbItem tbItem : selectByExample) {
					tbItem.setStatus(status);
					itemMapper.updateByPrimaryKeySelective(tbItem);
				}
				//更新索引库的操作
				
			}
			
			
		}

		@Override
		public void putaway(Long[] ids, String status) {
			
            if(status==null||status==""){
            for(Long id:ids){
            	TbGoods key = goodsMapper.selectByPrimaryKey(id);
            	key.setIsMarketable(null);
            	goodsMapper.updateByPrimaryKey(key);
            }
            }else{
            	 for(Long id:ids){
                 	TbGoods key = goodsMapper.selectByPrimaryKey(id);
                 	key.setIsMarketable(status);
                 	goodsMapper.updateByPrimaryKey(key);
                 }
            }			
		}

		@Override //更新索引库操作 根据SPUID查找对应的SKU  在运营商的goods商品管理后台
		public List<TbItem> updateSolr(Long[] ids, String status) {
			  TbItemExample example = new TbItemExample();
			  com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			  criteria.andGoodsIdIn(Arrays.asList(ids));
			  criteria.andStatusEqualTo(status);
			   List<TbItem> selectByExample = itemMapper.selectByExample(example );
			return selectByExample;
		}
	
}
