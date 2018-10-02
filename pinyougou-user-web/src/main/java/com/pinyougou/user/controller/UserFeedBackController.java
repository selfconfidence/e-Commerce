package com.pinyougou.user.controller;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.mongodb.FeedBackEntity;
import com.pinyougou.mongodb.service.UserFeedBackService;

import entity.CRUDResult;
import util.IdWorker;

/** 
* @author misterWei
* @ 创建时间：2018年9月12日 上午11:53:32 
* 说明等 
*/
@RestController
@RequestMapping("/feedBack")
public class UserFeedBackController {
	
	@Reference(timeout=5000)
	private UserFeedBackService userFeedBackService;
	
	@RequestMapping("/save")
	public CRUDResult saveData(@RequestBody FeedBackEntity feedBack){
		try{
			feedBack.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
			feedBack.setCreateTime(new Date());
			feedBack.setUpdateTime(new Date());
			userFeedBackService.saveCountext(feedBack);
		return new CRUDResult(true, "保存成功");
		}catch(Exception e){
		 e.printStackTrace();
		 return new CRUDResult(false,"保存失败");
		}
		
	}
	
	@RequestMapping("/delete")
	public CRUDResult deleteData( String self_motionId){
		
		try{
			userFeedBackService.deleteCountext(self_motionId);
			return new CRUDResult(true, "保存成功");
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false,"保存失败");
		}
		
	}
	@RequestMapping("/findOne")
	public FeedBackEntity findOne( String self_motionId){
		
		try{
			FeedBackEntity findOneCountext = userFeedBackService.findOneCountext(self_motionId);
			return findOneCountext;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	@RequestMapping("/update")
	public CRUDResult updateData(@RequestBody FeedBackEntity feedBack){
		
		try{
			userFeedBackService.updateCountext(feedBack);
			return new CRUDResult(true, "保存成功");
		}catch(Exception e){
			e.printStackTrace();
			return new CRUDResult(false,"保存失败");
		}
		
	}
	
	@RequestMapping("/initName.do")
	public String initName(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	@RequestMapping("/findAll")
	public List<FeedBackEntity> findAll(){
		return userFeedBackService.findCountext(SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	
	
	
}
