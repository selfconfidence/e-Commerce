package com.pinyougou.seckill.service;
import java.util.List;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum,int pageSize);
	//立即抢购,保存订单到redis和数据库不搭嘎
	public boolean saveSeckillOrder(Long seckillGoodsId,String userName);
	
	public TbSeckillOrder findRedisSeckillOrder(String userNmae);
	public boolean findRedisSavetoDb(String seckillGoodsId,String transaction_id,String userNmae);
	public void updateSeckillOrderAndGoods(String seckillGoodsId,String userName);


	public void updateSeckillOrder(Long  seckillOrderId, TbAddress tbAddress);
}
