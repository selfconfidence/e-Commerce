package com.pinyougou.pay.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WexinPayService;
import com.pinyougou.pojo.TbPayLog;

import util.HttpClient;

@Service // 微信实际支付流程
public class WeixinPayServiceImpl implements WexinPayService {
	// 具体流程 根据指定的传输传递,请求微信的支付地址微信支付就会返回给我们一个支付的二维码地址

	// 读取配置文件中的paramenter的参数信息;
	@Value("${appid}")
	String appid;// 公众账号ID
	@Value("${partner}") // 商户号
	String partner;
	@Value("${partnerkey}") // 密钥
	String partnerkey;
	@Value("${notifyurl}") // 回调地址
	String notifyurl;

	@Override
	public Map<String, String> weixinNativePay(String out_trade_no, String total_fee) throws Exception {
		Map<String, String> payparmenter = new HashMap<String, String>();
		payparmenter.put("appid", appid);
		payparmenter.put("mch_id", partner); // WxPayUtil属于微信支付提供的工具类
		payparmenter.put("nonce_str", WXPayUtil.generateNonceStr());// 随机字符串
		payparmenter.put("body", "品优购");
		payparmenter.put("out_trade_no", out_trade_no);
		payparmenter.put("total_fee", total_fee);
		payparmenter.put("spbill_create_ip", "127.0.0.1");// 记录请求ID本机ID
		payparmenter.put("notify_url", "https://www.itcast.cn");
		payparmenter.put("trade_type", "NATIVE");// 扫码地址支付
		System.err.println(partnerkey);
		// 工具类构造中填写请求微信后台的路径
		String mapToXml = WXPayUtil.generateSignedXml(payparmenter, partnerkey);// 转换为xml格式内容//传入参数以及密钥
		System.out.println("请求参数" + mapToXml);
		HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
		httpClient.setHttps(true);// 指定为https的协议
		httpClient.setXmlParam(mapToXml);
		httpClient.post();
		// 完成调用
		// 获取调用过后的参数
		String content = httpClient.getContent();
		// 这时候就会有回调参数的产生
		Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
		System.out.println("回调函数类型" + xmlToMap);
		Map payParamenter = new HashMap<>();
		payParamenter.put("code_url", xmlToMap.get("code_url"));
		payParamenter.put("out_trade_no", out_trade_no);
		payParamenter.put("total_fee", total_fee);

		return payParamenter;
	}

	@Override
	public Map<String, String> findPayDetails(String goodsId) throws Exception {
		// 查询支付状态             //
		try{
			Map<String, String> map = new HashMap<>();
			map.put("appid", appid);
			map.put("mch_id", partner);
			map.put("out_trade_no", goodsId);
			map.put("nonce_str", WXPayUtil.generateNonceStr());
			String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
			System.out.println(httpClient);
			httpClient.setHttps(true);
			httpClient.setXmlParam(signedXml);
			httpClient.post();
			// 接收请求参数 判断是否付款成功!
			String content = httpClient.getContent();
			Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
			System.out.println(xmlToMap);
			return xmlToMap;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public TbPayLog getPayLog(String userName) {
		
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userName);
	}

	@Override
	public Map<String, String> orderCancel(String goodsId) {
		// 取消支付状态            //
		try{
			Map<String, String> map = new HashMap<>();
			map.put("appid", appid);
			map.put("mch_id", partner);
			map.put("out_trade_no", goodsId);
			map.put("nonce_str", WXPayUtil.generateNonceStr());
			String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
			HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
			System.out.println(httpClient);
			httpClient.setHttps(true);
			httpClient.setXmlParam(signedXml);
			httpClient.post();
			// 接收请求参数 判断是否付款成功!
			String content = httpClient.getContent();
			Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
			System.out.println(xmlToMap);
			return xmlToMap;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	
	}
   //用户中心支付操作
	@Override
	public TbPayLog getOrderUser(String userName) {
		
		return (TbPayLog) redisTemplate.boundHashOps("payOrderUser").get(userName);
	}

}
