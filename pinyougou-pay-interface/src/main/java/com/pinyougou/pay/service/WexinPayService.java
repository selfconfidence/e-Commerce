package com.pinyougou.pay.service;

import java.util.Map;

import com.pinyougou.pojo.TbPayLog;

//微信支付接口
public interface WexinPayService {
public Map<String,String>weixinNativePay(String out_trade_no,String total_fee) throws Exception;
public Map<String,String>findPayDetails(String goodsId) throws Exception;
public TbPayLog getPayLog(String userName);
public Map<String,String>orderCancel(String goodsId);
public TbPayLog getOrderUser(String userName);

}
