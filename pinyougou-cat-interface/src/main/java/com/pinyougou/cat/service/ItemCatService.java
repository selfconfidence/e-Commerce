package com.pinyougou.cat.service;

import java.util.List;

import com.pinyougou.entitygroup.ItemCat;

public interface ItemCatService {
	
	//购物车的实际接口  需要一个List<ItemCat> 的返回值   参数也应该是 List<ItemCat>   商家id  商家名称等
	
	public List<ItemCat> addItemCat(List<ItemCat> catList,Long itemId,Integer num);
	public List<ItemCat> findRedisItemCart(String userName);
	public void addRedisItemCart(List<ItemCat> catList,String userName);
	public List<ItemCat> mergeCartList(List<ItemCat> catList1,List<ItemCat> catList2);
	public List<ItemCat> deleteItemCart(List<ItemCat> itemCart,Long itemId);
	public List<ItemCat> deleteItemCartOne(List<ItemCat> itemCart,Long itemId);
	

}
