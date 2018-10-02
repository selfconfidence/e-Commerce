package com.pinyougou.mongodb.service;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.mongodb.FeedBackEntity;


/** 
* @author misterWei
* @ 创建时间：2018年9月11日 下午10:33:17 
*用户反馈接口
* 说明等 
*/

public interface UserFeedBackService {
	
  public void saveCountext(Serializable feedBackEntity);
  
  public void deleteCountext(Serializable id);
  
  public void updateCountext(Serializable feedBackEntity);
  
  public FeedBackEntity findOneCountext(Serializable feedBackEntity);
  public List<FeedBackEntity> findCountext(Serializable userId);
  
  public List<FeedBackEntity> orderFeedBack(Serializable userId);
  public  List<FeedBackEntity> sortFeedBack(Serializable userId);
  
	
}
