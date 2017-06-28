package com.thinkgem.jeesite.modules.demo.entity;

// 订单明细实体
public class OrderDetail {

	// 主键，订单明细表id
	private Integer id;
	// 订单id
	private Integer ordersId;
	// 商品id
	private Integer itemsId;
	// 商品购买数量
	private Integer itemsNum;
	// 明细对应的商品信息
	private Items items;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrdersId() {
		return ordersId;
	}
	public void setOrdersId(Integer ordersId) {
		this.ordersId = ordersId;
	}
	public Integer getItemsId() {
		return itemsId;
	}
	public void setItemsId(Integer itemsId) {
		this.itemsId = itemsId;
	}
	public Integer getItemsNum() {
		return itemsNum;
	}
	public void setItemsNum(Integer itemsNum) {
		this.itemsNum = itemsNum;
	}
	public Items getItems() {
		return items;
	}
	public void setItems(Items items) {
		this.items = items;
	}
	
}
