package com.pinyougou.userlogin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.SimpleAliasRegistry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	//在这里我们使用到了service要进行认证数据库连接操作,但是在不同项目也不是controller  
	  //具体引用的实现步骤如下:
	 private SellerService sellerService;
	 //在springsecurity.xml中去配置引入必须需要set的   Spring的ioc注入原理
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}
	

	@Override  //实现安全认证就需要去使用SpringSecurity所提供的接口实现类去完成安全校验功能操作
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("加密认证中!");
		//查询当前用户名称  登录名就是它的ID在表中是被指定好的了
		TbSeller findOne = sellerService.findOne(username);
		      if(findOne!=null){
		    	if(findOne.getStatus().equalsIgnoreCase("1")){
		    		//return null 相当于被拦截   
			  		//需要的返回值是一个接口 使用它的实现类
			  		List<GrantedAuthority> list = new ArrayList<>();
			  		   //指定角色
			  		list.add(new SimpleGrantedAuthority("ROLE_SELLERLOGIN"));
			  		
			  		return new User(username,findOne.getPassword(),list);//有三个参数  一就是当前username  二就是当前password,三指定角色
		    	}else{
		    		return null;
		    	}
		      }else{
		    	  return null;
		      }                      //参数usernaem就是当前用户登陆的名称
		
		
	}

}
