package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	public List<TbBrand> findAll();

	public PageResult findPage(Integer pageIndex, Integer pageSize);

	public void tbBrandAdd(TbBrand tbBrand) throws Exception;

	public TbBrand findBid(Long id);

	public void updatetbBrand(TbBrand brand) throws Exception;

	public void delete(Long[] ids);

	public PageResult findPage(TbBrand tbBrand, Integer pageIndex, Integer pageSize);
    public List<Map>findOptionList();
}
