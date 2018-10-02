package com.pinyougou.shop.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.support.SecurityWebApplicationContextUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entitygroup.Goods;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.CRUDResult;
import entity.PageResult;

/**
 * controller
 * @author Administrator
 *
 */
  
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Reference
	private SeckillGoodsService seckillGoodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public CRUDResult add(@RequestBody Goods goods){
		//需要获取当前登陆商家的名称用来和商品做对应关系
		String merChantName = SecurityContextHolder.getContext().getAuthentication().getName();
		
		goods.getGoods().setSellerId(merChantName);
		try {
			goodsService.add(goods);
			return new CRUDResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public CRUDResult update(@RequestBody Goods goods){
		try {
			goods.getGoods().setAuditStatus("0");
			 //安全验证更新操作 是否商品来自同一个商家 如果不是就非法操作
			 if( !goods.getGoods().getSellerId().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                  return new CRUDResult(false,"非法操作!");				 
			 }
			goodsService.update(goods);
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
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public CRUDResult delete(Long [] ids){
		Goods goods = goodsService.findOne(ids[0]);
		 if( !goods.getGoods().getSellerId().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
             return new CRUDResult(false,"非法操作!");				 
		 }
		try {
			goodsService.delete(ids);
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
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		   goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return goodsService.findPage(goods, page, rows);		
	}
	@RequestMapping("/seckillSearch")
	public PageResult seckillSearch(@RequestBody TbSeckillGoods seckillgoods, int page, int rows  ){
		seckillgoods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return seckillGoodsService.findPage(seckillgoods, page, rows);		
	}
	@RequestMapping("/putaway")
	public CRUDResult putaway(Long[] ids,String status){
		try{
			goodsService.putaway(ids, status);
			return new CRUDResult(true, "操作完成!");
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false, "操作有误!");
		}
	}
	
}
