/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.supplier.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 供应商Entity
 * @author jicdata
 * @version 2016-02-25
 */
public class Supplier extends DataEntity<Supplier> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 供应商名称
	
	public Supplier() {
		super();
	}

	public Supplier(String id){
		super(id);
	}

	@Length(min=1, max=64, message="供应商名称长度必须介于 1 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}