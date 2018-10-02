package com.pinyougou.mongodb.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.WriteResult;
import com.pinyougou.mongodb.FeedBackEntity;
import com.pinyougou.mongodb.service.UserFeedBackService;

import util.IdWorker;

/** 
* @author misterWei
* @ 创建时间：2018年9月11日 下午11:08:22 
* 说明等 
*/
@Service
public class UserFeedBackServiceImpl implements UserFeedBackService{
	
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private IdWorker idWorker;

	@Override
	public void saveCountext(Serializable feedBackEntity) {
		  FeedBackEntity feedBackEntityInsert = (FeedBackEntity)feedBackEntity;
		  
		mongoTemplate.insert(feedBackEntityInsert);
	}

	@Override
	public void deleteCountext(Serializable id) {//删除唯一ID
		mongoTemplate.remove(new Query().addCriteria(Criteria.where("_id").is(id)), FeedBackEntity.class);
		/** 
		* @author 作者姓名 
		* @ 创建时间：2018年9月11日 下午11:08:37 
		* 说明等 
		*/ 
	}

	@Override//实体类
	public void updateCountext(Serializable feedBackEntity) {
		FeedBackEntity  entity=	(FeedBackEntity)feedBackEntity;
		FeedBackEntity countext = findOneCountext(entity.getSelf_motionId());
		if(entity.getContent()==null || entity.getContent()==""){
			
			entity.setContent(countext.getContent());
		}
		if(entity.getPostscript()==null || entity.getPostscript()==""){
			entity.setPostscript(countext.getPostscript());
		}
		
	       Update update = new Update();
	       update.set("postscript", entity.getPostscript());
	       update.set("content", entity.getContent());
		
		WriteResult updateFirst = mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(entity.getSelf_motionId())), update, FeedBackEntity.class);
		/** 
		* @author 作者姓名 
		* @ 创建时间：2018年9月11日 下午11:08:37 
		* 说明等 
		*/ 
	}

	@Override
	public FeedBackEntity findOneCountext(Serializable feedBackEntity) {
	return	mongoTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(feedBackEntity)),FeedBackEntity.class);
	}

	@Override
	public List<FeedBackEntity> findCountext(Serializable userId) {
		return mongoTemplate.find(new Query().addCriteria(Criteria.where("userId").in(userId)), FeedBackEntity.class);
		
	}

	@Override
	public List<FeedBackEntity> orderFeedBack(Serializable userId) {
		return null;
	}

	@Override
	public List<FeedBackEntity> sortFeedBack(Serializable userId) {
		Query query = new Query();
		query.with(new Sort(new Order(Direction.DESC,"createTime")));
		query.addCriteria(Criteria.where("userId").in(userId));
		List<FeedBackEntity> sortTime = mongoTemplate.find(query, FeedBackEntity.class);
		return sortTime;

	}
	
}
