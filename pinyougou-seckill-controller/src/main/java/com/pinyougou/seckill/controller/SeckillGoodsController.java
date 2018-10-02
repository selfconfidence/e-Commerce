package com.pinyougou.seckill.controller;
import java.util.List;

import org.opensaml.xml.security.SecurityHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.CRUDResult;
import entity.PageResult;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference
	private SeckillGoodsService seckillGoodsService;
	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll(){			
		return seckillGoodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return seckillGoodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/add")
	public CRUDResult add(@RequestBody TbSeckillGoods seckillGoods){
		try {
			seckillGoodsService.add(seckillGoods);
			return new CRUDResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/update")
	public CRUDResult update(@RequestBody TbSeckillGoods seckillGoods){
		try {
			seckillGoodsService.update(seckillGoods);
			return new CRUDResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public CRUDResult delete(Long [] ids){
		try {
			seckillGoodsService.delete(ids);
			return new CRUDResult(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeckillGoods seckillGoods, int page, int rows  ){
		return seckillGoodsService.findPage(seckillGoods, page, rows);		
	}
	@RequestMapping("/findList")
	public List<TbSeckillGoods> findList(){
		return seckillGoodsService.findList();
		
	}
	@RequestMapping("/findOneGoods")
	public TbSeckillGoods findOneGoods(Long goodsId){
		return seckillGoodsService.findOneGoods(goodsId);
	}
	@RequestMapping("/saveSeckillOrder")
	public CRUDResult saveSeckillOrder(Long seckillGoodsId){
		try{
			String userName = SecurityContextHolder.getContext().getAuthentication().getName();		
			   if(userName==null){
				   return new CRUDResult(false, "当前未登陆!");
			   }
			if(userName.equals("anonymousUser")){
				   return new CRUDResult(false, "当前未登陆!");
			   }
			   boolean flag = seckillOrderService.saveSeckillOrder(seckillGoodsId, userName);
			   if(!flag){
				   return new CRUDResult(false, "该商品结束活动");
			   }
			   return new CRUDResult(true, "OK");
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false, "网络延迟.");
		}
		
	}
	
	
}
