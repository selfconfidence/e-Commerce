package com.pinyougou.cat.controller;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pinyougou.cat.service.ItemCatService;
import com.pinyougou.entitygroup.ItemCat;

import entity.CRUDResult;
import util.CookieUtil;

@RestController
@RequestMapping("/cat")
public class ItemCatController {
	@Reference
	private ItemCatService itemCatService;

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	@RequestMapping("/lookcat")

	// 查找购物车数据 的方法也能查看cookie中的购物车信息
	public List<ItemCat> lookCat() {
		// 获取存储在cookie中的购物车列表信息
		String cookieValue = CookieUtil.getCookieValue(request, "itemCat", "UTF-8");
		// 判断字符串有没有数据
		String loginUserName = SecurityContextHolder.getContext().getAuthentication().getName();

		if (cookieValue == null || cookieValue.equals("")) {
			cookieValue = "[]"; // 如果是没有数据的那么转换JSON也不会报错,只会转换成一个JSON的空集合操作.
		}
		List<ItemCat> parseArray = JSON.parseArray(cookieValue, ItemCat.class);
		// 判断当前登陆着信息是匿名登陆还是用户登陆
		if (loginUserName.equals("anonymousUser")) {
			// 匿名登陆使用cookie获取

			return parseArray;
		} else {
			// 登陆之后用redis获取数据
			List<ItemCat> findRedisItemCart = itemCatService.findRedisItemCart(loginUserName);

			if (findRedisItemCart == null) {
				// 第一次操作获取的肯定是null 为了避免空指针,如果为null,那么就new一个具体类型
				findRedisItemCart = new ArrayList();
			}
			// 登陆之后就需要合并操作 ,再合并之前首先判断当前cookie是否有数据,否则如果没有走登陆的时候就会直接合并操作,会消耗资源
			if (parseArray.size() > 0) {
				findRedisItemCart = itemCatService.mergeCartList(parseArray, findRedisItemCart);
				// 存储到redis,并清除cookie数据,使合并操作尽量减少
				CookieUtil.deleteCookie(request, response, "itemCat");
				itemCatService.addRedisItemCart(findRedisItemCart, loginUserName);
			}

			return findRedisItemCart;
		}

	}
	
	// 前端控制层:
    
	@RequestMapping("/catinsert")
	  //SpringMvc封装过的跨域请求                                                             //Springmvc这里是分装好的 缺省就是true
	public CRUDResult addCat(Long itemId, Integer num) { // 根据sku的id来对购物车列表进行具体的操作:
		// 存储cookie数据 操作 不仅需要存储还需要从cookie中去获取数据

        //也可以是一个*  通配,,但是不能使用请求段cookie
response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//设置以下标准,设置请求段cookie域服务端的交互                                                            //同意,
response.setHeader("Access-Control-Allow-Credentials", "true");
		try {

			String loginUserName = SecurityContextHolder.getContext().getAuthentication().getName();
			// 从cookie中获取购物车列表信息 这个参数就是cookie中获取的购物车信息 或者有或者没有
			List<ItemCat> addItemCat = itemCatService.addItemCat(lookCat(), itemId, num);// SKUid
																							// 以及数量

			if (loginUserName.equals("anonymousUser")) {
				// 转换成字符串后再存到cookie中
				// 加上这个之后就会避免垃圾数据的产生

				String jsonString = JSON.toJSONString(addItemCat, SerializerFeature.DisableCircularReferenceDetect);
				CookieUtil.setCookie(request, response, "itemCat", jsonString, 3600, "UTF-8");
			} else {
				// 存储redis

				itemCatService.addRedisItemCart(addItemCat, loginUserName);
			}

			return new CRUDResult(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(true, "保存失败");
		}

	}

	@RequestMapping("/deleteItemCart")
	public CRUDResult deleteItemCart(Long itemId) {
		try {
			String loginUserName = SecurityContextHolder.getContext().getAuthentication().getName();
			// 调用此方法获取所有购物车列表对象
			List<ItemCat> lookCat = lookCat();
			List<ItemCat> deleteItemCart = null;

			if (lookCat.size() != 1) {
				// 如果购物车列表不等于1 说明不止1个数据那就走这个逻辑
				deleteItemCart = itemCatService.deleteItemCart(lookCat, itemId);
			} else {
				// 相反购物车列表为1,那么就是就有一个购物车对象,就走这个逻辑
				deleteItemCart = itemCatService.deleteItemCartOne(lookCat, itemId);
			}

			// 以下逻辑都是根据条件存储到cookie或者redis中的;

			if (loginUserName.equals("anonymousUser")) {
				// 转换成字符串后再存到cookie中
				// 加上这个之后就会避免垃圾数据的产生

				String jsonString = JSON.toJSONString(deleteItemCart, SerializerFeature.DisableCircularReferenceDetect);
				CookieUtil.setCookie(request, response, "itemCat", jsonString, 3600, "UTF-8");
			} else {
				// 存储redis

				itemCatService.addRedisItemCart(deleteItemCart, loginUserName);
			}

			return new CRUDResult(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new CRUDResult(false, "删除失败");
		}

	}

}
