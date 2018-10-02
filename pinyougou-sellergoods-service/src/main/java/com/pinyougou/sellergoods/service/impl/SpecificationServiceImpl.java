package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entitygroup.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationmapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)  
	public void add(Specification specification) {
		 TbSpecification tbSpecification = specification.getTbSpecification();
		 //接受参数之后传递Service层获取产品规格  由于在Mapper中已经定义好了 返回主键,所以在执行插入之后就会返回主键
		specificationMapper.insert(tbSpecification);
		
		List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOptions();
		    //遍历这个规格详情表内容
		  for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
			    //通过返回的规格主键存储关联关系: 
			  tbSpecificationOption.setSpecId(tbSpecification.getId());
			  
			  specificationmapper.insert(tbSpecificationOption);
		}
	
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//更新操作:  因为前端页面对话框中 我们没法判断是更新规格数多少条所以
		
		    //逻辑操作就是 先把相同的给删除然后在执行添加操作
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		List<TbSpecificationOption> tbSpecificationOptions = specification.getTbSpecificationOptions();
		//删除操作
		  TbSpecificationOptionExample example =new TbSpecificationOptionExample();
		  
		  com.pinyougou.pojo.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
		 createCriteria.andSpecIdEqualTo(tbSpecification.getId());
		 specificationmapper.deleteByExample(example);
		 //添加操作
		 for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptions) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			 specificationmapper.insert(tbSpecificationOption);
		}
		 
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification = new Specification();
		specification.setTbSpecification(specificationMapper.selectByPrimaryKey(id));
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.pinyougou.pojo.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
	    createCriteria.andSpecIdEqualTo(id);
		specification.setTbSpecificationOptions(specificationmapper.selectByExample(example));
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> findspecificationList() {
			return specificationMapper.findspecificationList();
		}

		
	
}
