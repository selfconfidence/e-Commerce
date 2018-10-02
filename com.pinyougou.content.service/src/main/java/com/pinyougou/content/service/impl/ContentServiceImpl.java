package com.pinyougou.content.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyouygou.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@SuppressWarnings("all")
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//新添加的时候清除的缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//更新操作特别,因为有可能会更改分组id所以无法直接清除缓存
		//但是id是唯一的
		  TbContent key = contentMapper.selectByPrimaryKey(content.getId());
		  //这个删除预防更改分组id 先查id在把以前的分组缓存删除
		    redisTemplate.boundHashOps("content").delete(key.getCategoryId());
		  contentMapper.updateByPrimaryKey(content);
		  //一下的判断删除之后的缓存信息.
		  if(content.getCategoryId().longValue()!=key.getCategoryId().longValue()){
			  redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		  }
		 
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//批量查找批量删除缓存
			Long chech = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(chech);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		@Autowired //注入Spring分装redis的主类
    private RedisTemplate redisTemplate;
		@Override
		public List<TbContent> findCategoryId(Long category) {
			//获取缓存指定数据  使用的hash的存储方法 大key 和小key --value形式
			List<TbContent> list=  (List<TbContent>) redisTemplate.boundHashOps("content").get(category);
			
			  //这是一一堆消耗时间的大批量数据  需要进行redis操作
			if(list ==null){
				
				TbContentExample example = new TbContentExample();
				Criteria criteria = example.createCriteria();
				criteria.andCategoryIdEqualTo(category);//查询默认的条件
				criteria.andStatusEqualTo("1");//查询启用的状态数据
				example.setOrderByClause("sort_order");//按照这个字段进行排序
				list =contentMapper.selectByExample(example );
				//如果list是空那么缓存是没有数据的,所以需要将这次查询的数据存入到缓存,下次在获取就会有数据
				redisTemplate.boundHashOps("content").put(category, list);
			}
			
			return list;
		}
	
}
