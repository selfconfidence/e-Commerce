package com.pinyougou.entitygroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class Goods implements Serializable {
	private TbGoods goods;  //SPU的基本信息   一对一的存储方式u
	private TbGoodsDesc desc; //SUP的基本信息  由于字段名多所以采用了两张表的存储方式及
	private List<TbItem> itemList = new ArrayList<>();  //SKU 3*3SPU的规格详情,一对多.
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getDesc() {
		return desc;
	}
	public void setDesc(TbGoodsDesc desc) {
		this.desc = desc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}



}
