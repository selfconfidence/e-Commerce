package com.pinyougou.manager.controller;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pinyougou.entitygroup.Goods;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
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
	
	@Autowired
	private Destination topicCreateDestination;
	@Autowired
	private Destination topicDeleteDestination;
	
	//单元测试  是否能正常生成页面
	@RequestMapping("/gethtml")
	private void createHtml(final Long goodsId){
		jmsTemplate.send(topicCreateDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(goodsId.toString());
			}
		});
	}
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
	public CRUDResult delete(final Long [] ids){
		 
		
		try {
			goodsService.delete(ids);
			//根据运营商传递的id调用solr的具体业务层数据
			jmsTemplate.send(queueSolrDelete,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
			goodsService.delete(ids);
			//根据运营商传递的id调用solr的具体业务层数据
			jmsTemplate.send(topicDeleteDestination,new MessageCreator() {
				
				@Override
				public Message createMessage(Session session) throws JMSException {
					// TODO Auto-generated method stub
					return session.createObjectMessage(ids);
				}
			});
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
		return goodsService.findPage(goods, page, rows);		
	}
	@Autowired //注入整合模板类
	private JmsTemplate jmsTemplate;
	@Autowired//注入点对点的文本信息
	private Destination queueSolrUpdate;
	@Autowired
	private Destination queueSolrDelete;
	@RequestMapping("/checkAudit")
	public CRUDResult audit(Long[] ids ,String status){
		
		
		try {
			 //安全验证更新操作 是否商品来自同一个商家 如果不是就非法操作
			
			goodsService.audit(ids, status);
			//将批量更新的id 与状态传入后获取一个SKU的集合 更新到索引库即可
			if("1".equals(status)){
			List<TbItem> solr = goodsService.updateSolr(ids, status);
			final String jsonString = JSON.toJSONString(solr,SerializerFeature.DisableCircularReferenceDetect);
			jmsTemplate.send(queueSolrUpdate,new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					  //产生消息    由于只能发送String  Map 以及序列化对象 所以采用JSON的方式
                       //首先转换为String文本然后在转成List即可
					return session.createTextMessage(jsonString);
				}
			});
				for(final Long id:ids){
					jmsTemplate.send(topicCreateDestination,new MessageCreator() {
						
						@Override
						public Message createMessage(Session arg0) throws JMSException {
							
							return arg0.createTextMessage(id.toString());
						}
					});
					
					}
				}
		
			
			return new CRUDResult(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "修改失败");
		}
	}
	
	
	//关于秒杀运营商的处理逻辑
	
	@RequestMapping("/seckillSearch")
	public PageResult seckillSearch(@RequestBody TbSeckillGoods seckillgoods, int page, int rows  ){
		return seckillGoodsService.findPage(seckillgoods, page, rows);		
	}
	@RequestMapping("/checkStatus")
	public CRUDResult checkStatus(Long[] ids,String status){
		try{
			seckillGoodsService.checkSeckillStatus(ids, status);
			return new CRUDResult(true,"OK!");
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false,"秒杀商品不可更改.");
		}
		
	}
	
}
