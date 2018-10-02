package com.pinyougou.entitygroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbOrderItem;

public class ItemCat implements Serializable{
     //购物车的实体类存储,由于存储的和订单的商品详细表是属于一派的我们可以使用到
	 //存储类型  商家id  商家名称
	 private String sellerId;
	 private String sellerName;
	 private List<TbOrderItem> orderItem;
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public List<TbOrderItem> getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(List<TbOrderItem> orderItem) {
		this.orderItem = orderItem;
	}
	 
}
