package com.pinyougou.entitygroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

public class Specification implements Serializable {
	private TbSpecification tbSpecification;
	
	private List<TbSpecificationOption> tbSpecificationOptions;

	public TbSpecification getTbSpecification() {
		return tbSpecification;
	}

	public void setTbSpecification(TbSpecification tbSpecification) {
		this.tbSpecification = tbSpecification;
	}

	public List<TbSpecificationOption> getTbSpecificationOptions() {
		return tbSpecificationOptions;
	}

	public void setTbSpecificationOptions(List<TbSpecificationOption> tbSpecificationOptions) {
		this.tbSpecificationOptions = tbSpecificationOptions;
	}

}
