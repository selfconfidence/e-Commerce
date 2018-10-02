package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.CRUDResult;
import entity.PageResult;

@RestController // 这个也是声明Spring容器只不过rest是增强了一些东西而已
@RequestMapping("/brand")
@SuppressWarnings("all")
public class BrandController {

	@Reference // 使用dubbox提供的注解,用于查找在注册服务器中指定的service服务
	private BrandService branService;

	@RequestMapping("/findAll")
	public List<TbBrand> findAll() {

		return branService.findAll();
	}

	@RequestMapping("/pageLook")
	public PageResult<TbBrand> pageLook(Integer pageIndex, Integer pageSize) {

		return branService.findPage(pageIndex, pageSize);
	}

	@RequestMapping("/tbBrandAdd")
	public CRUDResult tbBrandAdd(@RequestBody TbBrand brand) {
		try {
			branService.tbBrandAdd(brand);
			return new CRUDResult(true, "添加成功!");
		} catch (Exception exception) {
			exception.printStackTrace();
			return new CRUDResult(false, "添加失败,请查看是否有重复数据!");
		}

	}

	@RequestMapping("/findBid")
	public TbBrand findBid(Long id) {
		return branService.findBid(id);
	}

	@RequestMapping("/updatetbBrand")
	public CRUDResult updatetbBrand(@RequestBody TbBrand brand) {
		try {
			branService.updatetbBrand(brand);
			return new CRUDResult(true, "更新成功!");
		} catch (Exception exception) {
			exception.printStackTrace();
			return new CRUDResult(false, "更新失败,请查看是否有重复数据!");
		}
	}
	@RequestMapping("/delete")
	public CRUDResult deleteBrand(Long[] ids){
		try {
			branService.delete(ids);
			return new CRUDResult(true, "删除成功!");
		} catch (Exception exception) {
			exception.printStackTrace();
			return new CRUDResult(false, "删除失败,网络状态不太好!");
		}
	}
	@RequestMapping("/search")
	public PageResult<TbBrand> pageLook(@RequestBody TbBrand tbBrand,Integer pageIndex, Integer pageSize) {
		 
		return branService.findPage(tbBrand, pageIndex, pageSize);
	
	}
	@RequestMapping("/findOptionList")
	public List<Map> findOptionList(){
		return branService.findOptionList();
	}

}
