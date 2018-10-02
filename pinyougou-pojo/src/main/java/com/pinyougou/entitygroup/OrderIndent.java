package com.pinyougou.entitygroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderIdToString;
import com.pinyougou.pojo.TbOrderItem;

public class OrderIndent implements Serializable {
       private TbOrderIdToString tbOrder;
       private List<TbOrderItem> tbOrderItem;
	public TbOrderIdToString getTbOrder() {
		return tbOrder;
	}
	public void setTbOrder(TbOrderIdToString tbOrder) {
		this.tbOrder = tbOrder;
	}
	public List<TbOrderItem> getTbOrderItem() {
		return tbOrderItem;
	}
	public void setTbOrderItem(List<TbOrderItem> tbOrderItem) {
		this.tbOrderItem = tbOrderItem;
	}
       
}
