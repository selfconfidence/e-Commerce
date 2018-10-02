package com.pinyougou.cat.controller;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WexinPayService;
import com.pinyougou.pojo.TbPayLog;

import entity.CRUDResult;
import util.IdWorker;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Reference
	private WexinPayService wexinPayService;
	@Reference
	 private  OrderService orderService;
	
	@RequestMapping("/wxPay")
	public Map weixinNativePay(){
		try{
			//从redis中去获取订单日志数据  并生成相对应的二维码
			TbPayLog payLog = wexinPayService.getPayLog(SecurityContextHolder.getContext().getAuthentication().getName());
			Map<String, String> weixinNativePay = wexinPayService.weixinNativePay(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
		 return weixinNativePay;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	@RequestMapping("/wxPayStatus")
	public CRUDResult payStatus(String goodsId){
		try{
			
			int timer=0;
			//这是一个实时监控支付状态的后台逻辑
			while(true){ //无限循环的方式来监视
				Map<String, String> findPayDetails = wexinPayService.findPayDetails(goodsId);
				if(findPayDetails == null){
					 //在service中如果有异常那么就会返回null  所以在controller中去判断是否有异常显示
					return new CRUDResult(false, "网络异常,请稍后重试.");
				}
				if(findPayDetails.get("trade_state").equals("SUCCESS")){
					                           //如果状态码对应上之后那就说明支付成功了
					//应传递的参数,, 流水号,以及日志ID
					orderService.updateOrderLog(goodsId,findPayDetails.get("transaction_id"));
					//支付成功之后应该修改数据库的内容
					return new CRUDResult(true,"支付成功!");
				
				}
				//无限循环会产生堆内存压力过大 vm没有多余空间,就会系统瘫痪,所以需要让这个线程休息一会
				Thread.sleep(3000);//3秒循环一次
				           if(timer++ ==100){ //但是需要考虑到.如果多久的时间没有按照约定时间支付,就需要停止这个过程,造成二维码作废的逻辑
				        	   return new CRUDResult(false, "refresh");
				           }     
				
			}
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false, "支付异常请重试!");
		}
		
		
	}

}
