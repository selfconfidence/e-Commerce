package com.pinyougou.user.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;

import entity.CRUDResult;
import entity.PageResult;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import util.PhoneFormatCheckUtils;

/**
 * controller
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	@Reference
	private OrderService orderService;
	static BASE64Encoder encoder = new BASE64Encoder();        
	static BASE64Decoder decoder = new BASE64Decoder();
	 
	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll() {
		return userService.findAll();
	}

	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows) {
		return userService.findPage(page, rows);
	}

	@RequestMapping("/gainUserName")
	public Map findPage() {
		Map map = new HashMap();
		map.put("userName", SecurityContextHolder.getContext().getAuthentication().getName());
		return map;
	}

	/**
	 * 增加
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public CRUDResult add(@RequestBody TbUser user, HttpServletRequest request, HttpSession session) {
		// 调用方法,判断验证码是否一致
		boolean checkPhone = userService.checkPhone(user.getPhone(), request.getParameter("phoneCode"));
		if (!checkPhone) {
			return new CRUDResult(false, "请检查短信验证码是否正确....");
		}
		// 添加用户注册,判断用户是哪个登陆端访问的
		String header = request.getHeader("user-agent");
		System.out.println(header);
		if (header.contains("iPhone") || header.contains("iPad")) {
			user.setSourceType("4");
		} else if (header.contains("Android")) {
			user.setSourceType("5");
		} else {
			user.setSourceType("1");
		}

		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		user.setCreated(new Date());
		user.setLastLoginTime(new Date());
		user.setUpdated(new Date());
		try {
			user.setStatus("Y");
			// 一系列判断之后如果OK那就添加即可 上面的手动添加的内容是属于机制性的
			userService.add(user);
			return new CRUDResult(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "增加失败");
		}
	}

	// 单独的发送短信的前台逻辑
	@RequestMapping("/phoneNumber")
	public CRUDResult phoneFlag(String phone, HttpSession session, HttpServletResponse response,
			HttpServletRequest request) {

		try {
			if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
				return new CRUDResult(false, "手机格式验证失败,请输入正确手机号码!");
			}
			Cookie[] cookies = request.getCookies();
			for (Cookie phoneSms : cookies) {
				if (phoneSms.getName().equals("phone")) {
					if (phoneSms.getValue().length() <= 3) {
						return new CRUDResult(false, "由于发送过多,请您稍后重试!");
					} else {

						userService.smsSend(phone);
						phoneSms.setValue(phoneSms.getValue() + phoneSms.getValue());
						response.addCookie(phoneSms);
					}
				} else {
					Cookie cookie = new Cookie("phone", "1");
					cookie.setMaxAge(100000);
					response.addCookie(cookie);
					userService.smsSend(phone);
				}
			}

			return new CRUDResult(true, "短信发送成功,请及时填写验证码!");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "短信验证失败,请检查网络");
		}

	}

	@RequestMapping("/findOrder")
	public PageResult findOrder(int pageStart,int pageSize) {
		try {
			PageResult order = orderService
					.findOrder(SecurityContextHolder.getContext().getAuthentication().getName(),pageStart,pageSize);
			return order;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping("/skipPay")
	public CRUDResult skipPay(String orderId) {
		try {

			String skipPay = orderService.skipPay(orderId);
			if(skipPay==null){
				return new CRUDResult(false,"订单异常");
			}
			return new CRUDResult(true, "跳转吧风火轮!");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "网络异常");
		}

	}

	@RequestMapping("/initId")
	
	public List initId() {
		 
		List<TbOrder> list = orderService.initId(SecurityContextHolder.getContext().getAuthentication().getName());
		 
		return list;
	}
	
	@RequestMapping("/saveUserAddress")
	public CRUDResult saveUserAddress(String logId,TbAddress tbAddress){
		
		try{
			
          return new CRUDResult(true, "成功");			
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false, "失败,请重试");
		}
	}
	
	@RequestMapping("/findStatus")
	public  List findStatus(String status){
		
		return userService.findStatusList(status,SecurityContextHolder.getContext().getAuthentication().getName());
	}
	@RequestMapping("/upload")
	public CRUDResult savePic(MultipartFile file){
		
		try{
			byte[] bytes = file.getBytes();
			String picString =encoder.encodeBuffer(bytes).trim();
			TbUser pic = userService.findPic(SecurityContextHolder.getContext().getAuthentication().getName());
			pic.setHeadPic(picString);
			userService.update(pic);
			return new CRUDResult(true, picString);
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false, "头像文件过大,或格式不正确!");
		}
		
	}
	@RequestMapping("/updateUser")
	public TbUser updateUser(@RequestBody Map<String,String> map){
		try{
			System.out.println(map.get("birthday"));
			TbUser tbUser2 = userService.findPic(SecurityContextHolder.getContext().getAuthentication().getName());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
			tbUser2.setBirthday(simpleDateFormat.parse((String)map.get("birthday")));
			tbUser2.setNickName(map.get("nickName"));
			tbUser2.setSex(map.get("sex"));
			userService.update(tbUser2);
			return userService.findPic(SecurityContextHolder.getContext().getAuthentication().getName());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
