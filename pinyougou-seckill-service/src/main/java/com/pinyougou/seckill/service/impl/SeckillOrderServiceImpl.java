package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public  class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public boolean saveSeckillOrder(Long seckillGoodsId, String userName) {
			  //立即抢购应该从redis中去读取数据 将这些数据生成订单在进行存储redis操作 ,很少存在数据库操作
			TbSeckillGoods tbSeckillGoods= (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillGoodsId);
			if(tbSeckillGoods==null){  //如果当前秒杀商品是null 就没有此商品信息
				return false;
			}
			if(tbSeckillGoods.getStockCount()<=0){
				return false;         //如果当前秒杀商品的库存数小于等于0,代表没有库存.
			}
			//秒杀商品只限购买一次,所以不涉及数量操作只是每次执行操作的时候库存数据减少一,等到某个商品库存数没有的话,在进行更新数据库操作即可
			tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
			System.out.println("数量以减");
			redisTemplate.boundHashOps("seckillGoods").put(seckillGoodsId, tbSeckillGoods);//再将此商品覆盖到redis中只是跟更新了库存数量而已
			if(tbSeckillGoods.getStockCount()<=0){
				tbSeckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);//更新数据库操作
				redisTemplate.boundHashOps("seckillGoods").delete(seckillGoodsId);//删除redis缓存
				//注意虽然库存没有了,但是当前线程是不能停的.因为还有一件商品线程未结束
			}
		
			//添加订单表到redis
			long nextId = idWorker.nextId();
			TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
			tbSeckillOrder.setId(nextId);
			tbSeckillOrder.setSeckillId(seckillGoodsId);
			tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());
			tbSeckillOrder.setUserId(userName);
			tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId());
			tbSeckillOrder.setCreateTime(new Date());
			tbSeckillOrder.setStatus("0");
			redisTemplate.boundHashOps("seckillOrder").put(userName, tbSeckillOrder);
			//将此订单数据存储到redis中
			System.out.println("保存订单OK");
			return true;
		}
         //查询redis秒杀订单商品的数据
		@Override
		public TbSeckillOrder findRedisSeckillOrder(String userNmae) {
			return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userNmae);
		}

		@Override //保存到数据库操作
		public boolean findRedisSavetoDb(String orderId, String transaction_id,String userNmae) {
			TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userNmae);
			if(tbSeckillOrder==null){
				new RuntimeException("值是null");
			}
			if(!tbSeckillOrder.getId().equals(orderId)){
				new RuntimeException("Id不相等");
			}
			
			TbSeckillOrder seckillOrder = findRedisSeckillOrder(userNmae);
			seckillOrder.setStatus("1");
			seckillOrder.setPayTime(new Date());
			seckillOrder.setTransactionId(transaction_id);
			//添加添加数据库操作
			seckillOrderMapper.insert(seckillOrder);
			//删除redis中的订单操作 
			redisTemplate.boundHashOps("seckillOrder").delete(userNmae);
			return true;
			
		}

		@Override
		public void updateSeckillOrderAndGoods(String seckillGoodsId, String userName) {
			//取消此订单,并恢复成以前的数量
			TbSeckillOrder tbSeckillOrder=(TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userName);
			redisTemplate.boundHashOps("seckillOrder").delete(userName);
			//当前很有可能商品是被删除的,因为秒杀所以判空
			TbSeckillGoods tbSeckillGoods = (TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(tbSeckillOrder.getSeckillId());
			if(tbSeckillGoods==null){
				TbSeckillGoods seckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
				 //判断日期在当前日期之前 重新更新数据库并放入缓存
				if(seckillGoods.getStartTime().before(new Date())&&seckillGoods.getEndTime().after(new Date())){
					seckillGoods.setStockCount(1);
					tbSeckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
					redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(),tbSeckillGoods);
				}
				
			}else{
				//如果还有那么更改数量再放入缓存
		           tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);
		           redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(),tbSeckillGoods);
			}
		}

		@Override
		public void updateSeckillOrder(Long seckillOrderId, TbAddress tbAddress) {
               //根据当前登陆人找到指定的订单信息 并将收货人更新到秒杀表中
			TbSeckillOrder tbSeckillOrder = seckillOrderMapper.selectByPrimaryKey(seckillOrderId);
			if(tbSeckillOrder.getReceiver()!=null||tbSeckillOrder.getReceiverAddress()!=null||tbSeckillOrder.getReceiverMobile()!=null){
				new RuntimeException("不要重复更新收货人哦,联系商家更新!");
			}
			tbSeckillOrder.setReceiverAddress(tbAddress.getAddress());
			tbSeckillOrder.setReceiverMobile(tbAddress.getMobile());
			tbSeckillOrder.setReceiver(tbAddress.getContact());
			//重新保存订单即可
			seckillOrderMapper.updateByPrimaryKeySelective(tbSeckillOrder);
			System.out.println("更新");
			
		}
	
}
