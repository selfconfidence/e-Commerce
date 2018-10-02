package com.pinyougou.seach.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.seach.service.SeachItemService;

@RestController
@RequestMapping("/seach")
public class SeachItemController {
	
	@Reference
	private SeachItemService seachItemService;
	
	
	@RequestMapping("/itemSeach")
   public Map seachItem(@RequestBody Map condition){
		return seachItemService.seachItem(condition);
	}
}
