package com.pinyougou.seckill.controller;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WexinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.CRUDResult;

@RestController
@RequestMapping("/pay")
public class PayController {

	@Reference(timeout = 1000)
	private WexinPayService wexinPayService;
	@Reference
	private SeckillOrderService seckillOrderService;

	@RequestMapping("/wxPay")
	public Map weixinNativePay() {
		try {
			// 从redis中去获取订单日志数据 并生成相对应的二维码
			TbSeckillOrder seckillOrder = seckillOrderService
					.findRedisSeckillOrder((SecurityContextHolder.getContext().getAuthentication().getName()));
			if(seckillOrder==null){
				return null;
			}
			Map<String, String> weixinNativePay = wexinPayService.weixinNativePay(seckillOrder.getId().toString(),
					(long) (seckillOrder.getMoney().doubleValue() * 100) + "");
			return weixinNativePay;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@RequestMapping("/wxPayStatus")
	public CRUDResult payStatus(String orderId) {
		try {

			int timer = 0;
			// 这是一个实时监控支付状态的后台逻辑
			while (true) { // 无限循环的方式来监视
				Map<String, String> findPayDetails = wexinPayService.findPayDetails(orderId);
				if (findPayDetails == null) {
					// 在service中如果有异常那么就会返回null 所以在controller中去判断是否有异常显示
					return new CRUDResult(false, "网络异常,请稍后重试.");
				}
				if (findPayDetails.get("trade_state").equals("SUCCESS")) {
					// 如果状态码对应上之后那就说明支付成功了
					// 应传递的参数,, 流水号,以及日志ID
					// orderService.updateOrderLog(goodsId,findPayDetails.get("transaction_id"));
					seckillOrderService.findRedisSavetoDb(orderId, findPayDetails.get("transaction_id"),
							SecurityContextHolder.getContext().getAuthentication().getName());
					// 支付成功之后应该修改数据库的内容
					return new CRUDResult(true, "支付成功!");
				}
				// 无限循环会产生堆内存压力过大 vm没有多余空间,就会系统瘫痪,所以需要让这个线程休息一会
				Thread.sleep(3000);// 3秒循环一次
				if (timer++ == 100) { // 但是需要考虑到.如果多久的时间没有按照约定时间支付,就需要停止这个过程,造成二维码作废的逻辑
					// 将订单恢复原来的模样,并取消订单
					Map<String, String> cancel = wexinPayService.orderCancel(orderId);
					if (!"SUCCESS".equals(cancel.get("result_code"))) {
						// 如果没有成功关闭 就要判断是否有订单再关闭时提交,如果有的话,就要判断当前订单是否是已支付不能关闭的状态
						if ("ORDERPAID".equals(cancel.get("err_code"))) {
							// 一旦符合那么就直接支付成功,更新数据库操作
							seckillOrderService.findRedisSavetoDb(orderId, findPayDetails.get("transaction_id"),
									SecurityContextHolder.getContext().getAuthentication().getName());
							return new CRUDResult(true, "保存成功!");
						}
					}
					seckillOrderService.updateSeckillOrderAndGoods(orderId,
							SecurityContextHolder.getContext().getAuthentication().getName());

					return new CRUDResult(false, "refresh");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "支付异常请重试!");
		}

	}

}
