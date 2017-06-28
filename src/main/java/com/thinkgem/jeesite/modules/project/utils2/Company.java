package com.thinkgem.jeesite.modules.project.utils2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 	BeanUtils可以直接get和set一个属性的值。它将property分成3种类型：
 *	Simple ——简单类型，如String 、Int ……
 *	Indexed ——索引类型，如数组、arrayList ……
 *	Maped ——这个不用说也该知道，就是指Map啦，比如HashMap ……
 *
 * 	BeanUtils.getProperty(ClassName, PropertyName);//不管属性是什么类型，都返回字符串类型
	PropertyUtils.getProperty(Object bean, String name)//返回字段原始类型
 *
 * 访问不同类型的数据可以直接调用函数getProperty和setProperty。
 * 它们都只有2个参数，第一个是JavaBean对象，第二个是要操作的属性名。
 * Company c = new Company();
c.setName("Simple");
对于Simple类型，参数二直接是属性名即可
//Simple
System.out.println(BeanUtils.getProperty(c, "name"));
对于Map类型，则需要以“属性名(key值)”的形式
//Map
System.out.println(BeanUtils.getProperty(c, "address(A2)"));
HashMap am = new HashMap();
am.put("1", "234-222-1222211");
am.put("2", "021-086-1232323");
BeanUtils.setProperty(c, "telephone", am);
System.out.println(BeanUtils.getProperty(c, "telephone (2)"));
对于Indexed，则为“属性名[索引值]”，注意这里对于ArrayList和数组都可以用一样的方式进行操作。
//index
System.out.println(BeanUtils.getProperty(c, "otherInfo[2]"));
BeanUtils.setProperty(c, "product[1]", "NOTES SERVER");
System.out.println(BeanUtils.getProperty(c, "product[1]"));
当然这3种类也可以组合使用啦！
//nest
System.out.println(BeanUtils.getProperty(c, "employee[1].name"));
3 、此外，还有一个很重要的方法copyProperty，可以直接进行Bean之间的clone。
Company c2 = new Company();
BeanUtils.copyProperties(c2, c);
但是这种copy都是浅拷贝，复制后的2个Bean的同一个属性可能拥有同一个对象的ref，这个在使用时要小心，特别是对于属性为自定义类的情况。
4、最后还有populate，它用于将一个map的值填充到一个bean中，其函数原型如下：
public void populate (java.lang.Object bean,
java.util.Map properties)
throws java.lang.IllegalAccessException,
java.lang.reflect.InvocationTargetException
在struts中这个函数被用于从http request中取得参数添加到FormBean。
 * 
 * @author rgz
 *
 */
public class Company {
	private String name;
	private HashMap address = new HashMap();
	private String[] otherInfo;
	private ArrayList product;
	private ArrayList employee;
	private HashMap telephone;

	public Company() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress(String type) {
		return address.get(type).toString();
	}

	public void setAddress(String type, String address) {
		this.address.put(type, address);
	}

	public String[] getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String[] otherInfo) {
		this.otherInfo = otherInfo;
	}

	public ArrayList getProduct() {
		return product;
	}

	public void setProduct(ArrayList product) {
		this.product = product;
	}

	public ArrayList getEmployee() {
		return employee;
	}

	public void setEmployee(ArrayList employee) {
		this.employee = employee;
	}

	public HashMap getTelephone() {
		return telephone;
	}

	public void setTelephone(HashMap telephone) {
		this.telephone = telephone;
	}
}