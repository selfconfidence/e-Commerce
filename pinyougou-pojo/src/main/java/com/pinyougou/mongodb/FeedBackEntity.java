package com.pinyougou.mongodb;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/** 
* @author misterWei
* @ 创建时间：2018年9月11日 下午10:22:38 
* 用户反馈实体  mongodb存储数据
* 说明等 
*/
@Document
public class FeedBackEntity implements Serializable{


	@Id
   private String self_motionId;
	public String getSelf_motionId() {
		return self_motionId;
	}

	public void setSelf_motionId(String self_motionId) {
		this.self_motionId = self_motionId;
	}

	@Indexed
   private String userId;
	@Indexed(direction = IndexDirection.DESCENDING)
    private Date createTime;
	
    private Date updateTime;
    @Field
    private String  content;
    
    @Field    
    private String postscript;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPostscript() {
		return postscript;
	}

	public void setPostscript(String postscript) {
		this.postscript = postscript;
	}
    
    
}
