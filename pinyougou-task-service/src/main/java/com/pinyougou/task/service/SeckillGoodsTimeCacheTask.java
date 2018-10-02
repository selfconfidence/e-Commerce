package com.pinyougou.task.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

@Service
public class SeckillGoodsTimeCacheTask {
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	@Scheduled(cron="5/10 * * * * ?")
	public void timeDateSack(){
		//完成定时操作
		//从缓存中获取所有秒杀商品的key 因为key就是使用它们的ID作为存储的
		List seckillGoodsId= new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
		//也就可能第一次查的key是一个null或者是一个[]  那么所有的查询逻辑就会出错所以需要判断
		System.out.println(seckillGoodsId);
		TbSeckillGoodsExample example = new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");// 设置条件,秒杀商品的状态必须是审核专状态
		criteria.andStockCountGreaterThan(0);// 设置库存数量必须是大于等于1的;
		criteria.andStartTimeLessThanOrEqualTo(new Date());// 秒杀时间必须是小于等于当前时间的
		criteria.andEndTimeGreaterThan(new Date());// 秒杀结束时间大于等于当前时间
        //设置当前数据库秒杀商品的Id不包含这些Id,也就是说每次调度更新操作的时候如果数据库新添加秒杀商品了,那么就只会更新新添加的到redis 
		//而且最重要的每次都会判断商品的状态,时间,以及开始时间,结束时间,始终控制秒杀商品的时间范围
		if(seckillGoodsId!=null && seckillGoodsId.size()>0){
			criteria.andIdNotIn(seckillGoodsId);
		}
		
		List<TbSeckillGoods> selectByExample = seckillGoodsMapper.selectByExample(example);
		System.err.println(selectByExample);
		for (TbSeckillGoods tbSeckillGoods : selectByExample) {
			redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(),tbSeckillGoods);
		  
		}
						
	}
	
	@Scheduled(cron="1/5 * * * * ?")
	public void updateSeckill(){
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
			if(tbSeckillGoods.getEndTime().getTime()<new Date().getTime()){
				redisTemplate.boundHashOps("seckillGoods").delete(tbSeckillGoods.getId());
				seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);
				System.out.println("时间到期"+tbSeckillGoods.getId());
			}
		}
		System.out.println("----");
		
	}
	

}
