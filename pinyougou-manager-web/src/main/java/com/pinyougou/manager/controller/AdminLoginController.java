package com.pinyougou.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AdminLoginController {
	
@RequestMapping("/admin")
public Map loginAdmin(){
	Map  map= new HashMap<>();
 
	map.put("adminName", SecurityContextHolder.getContext().getAuthentication().getName());
	 return  map ;
}

}
