package com.thinkgem.jeesite.modules.demo.dao;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.demo.entity.Orders;
import com.thinkgem.jeesite.modules.demo.entity.OrdersCustom;
import com.thinkgem.jeesite.modules.demo.entity.User;

import java.util.List;

@MyBatisDao
public interface OrdersCustomMapper {
	
	/**
	 * 查询订单（关联用户）以及订单明细
	 */
	public List<Orders> findOrdersAndOrderDetailResultMap();
	
	/** 查询用户及用户所购买的商品信息 */
    public List<User> findUserAndItemsResultMap();

	/**
	 * 查询订单，关联查询用户信息
	 */
	public List<OrdersCustom> findOrdersUser();
	
	public List<Orders> findOrdersUser1();
	/**
	 * 查询订单关联查询用户信息，使用resultMap实现
	 * @return
	 */
	public List<Orders> findOrdersUserResultMap();
	
	
	
	  /**查询订单，关联查询用户，用户按需延迟加载*/
    public List<Orders> findOrdersUserLazyLoading();
    /**根据Id查询用户（这个方法本应该放在UserMapper类的，测试方便先放在这）*/
    public User findUserById(int id);
}
