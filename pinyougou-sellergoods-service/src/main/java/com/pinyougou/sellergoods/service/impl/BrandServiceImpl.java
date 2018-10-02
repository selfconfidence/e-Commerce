package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service // 注意一定要加上这个Service注解并且是dubbox的
@SuppressWarnings("all")
public class BrandServiceImpl implements BrandService {
	@Autowired // dao层在本地调用并执行的所以用普通的注入即可和service不同
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(Integer pageIndex, Integer pageSize) {
		// 使用mybatis的方法封装 //将当前页数以及 每次查询的记录数传递
		PageHelper.startPage(pageIndex, pageSize);
		// 查询 //
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		// 将这些分装好的数据,传入到我们封装的pageResule对象中即可.//底层已经帮我们分装好分页的数据.我们只需要拿到并使用即可
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	@Override
	public void tbBrandAdd(TbBrand tbBrand) throws Exception {
		TbBrandExample brandExample = new TbBrandExample();
		Criteria createCriteria = brandExample.createCriteria();
		createCriteria.andNameEqualTo(tbBrand.getName());
		List<TbBrand> selectByExample = brandMapper.selectByExample(brandExample);
		if (selectByExample.size() == 0 || selectByExample == null || selectByExample.equals("{}")) {
			brandMapper.insert(tbBrand);
		} else {

			throw new Exception("重复数据");
		}

	}

	@Override
	public TbBrand findBid(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updatetbBrand(TbBrand brand) throws Exception {
		TbBrandExample brandExample = new TbBrandExample();
		Criteria createCriteria = brandExample.createCriteria();
		createCriteria.andNameEqualTo(brand.getName());
		List<TbBrand> selectByExample = brandMapper.selectByExample(brandExample);
		if (selectByExample.size() == 0 || selectByExample == null || selectByExample.equals("{}")) {
			brandMapper.updateByPrimaryKeySelective(brand);
		} else {

			throw new Exception("重复数据");
		}
	}

	@Override
	public void delete(Long[] ids) {
		for (Long long1 : ids) {
			brandMapper.deleteByPrimaryKey(long1);
		}

	}

	@Override
	public PageResult findPage(TbBrand tbBrand, Integer pageIndex, Integer pageSize) {
		PageHelper.startPage(pageIndex, pageSize);
	
		//根据条件进行查询:
		 TbBrandExample brandExample = new TbBrandExample();
		   //判断操作,如果不进行那么就会造成逻辑操作不正确
		 if(tbBrand!= null){
			 Criteria createCriteria = brandExample.createCriteria();
			 if(tbBrand.getName()!=null && tbBrand.getName()!=""){
				 createCriteria.andNameLike("%"+tbBrand.getName()+"%");
			 }
			 if(tbBrand.getFirstChar()!= null && tbBrand.getFirstChar()!=""){
				 createCriteria.andFirstCharLike(tbBrand.getFirstChar());
			 }
		 }
	   Page<TbBrand> page =	(Page) brandMapper.selectByExample(brandExample);
		   
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findOptionList() {
		return brandMapper.findOptionList();
				
	}

}
