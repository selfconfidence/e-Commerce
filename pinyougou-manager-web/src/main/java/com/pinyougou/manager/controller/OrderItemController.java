package com.pinyougou.manager.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.OrderItemService;

import entity.PageResult;
import entity.CRUDResult;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/orderItem")
public class OrderItemController {

	@Reference
	private OrderItemService orderItemService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrderItem> findAll(){			
		return orderItemService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderItemService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param orderItem
	 * @return
	 */
	@RequestMapping("/add")
	public CRUDResult add(@RequestBody TbOrderItem orderItem){
		try {
			orderItemService.add(orderItem);
			return new CRUDResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param orderItem
	 * @return
	 */
	@RequestMapping("/update")
	public CRUDResult update(@RequestBody TbOrderItem orderItem){
		try {
			orderItemService.update(orderItem);
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
	public TbOrderItem findOne(Long id){
		return orderItemService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public CRUDResult delete(Long [] ids){
		try {
			orderItemService.delete(ids);
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
	public PageResult search(@RequestBody TbOrderItem orderItem, int page, int rows  ){
		return orderItemService.findPage(orderItem, page, rows);		
	}
	
}
