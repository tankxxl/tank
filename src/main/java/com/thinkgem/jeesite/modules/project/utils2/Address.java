package com.thinkgem.jeesite.modules.project.utils2;
public class Address {
	private String postCode;
    private String country;
    private String city;
    private String addr;
 
    public Address() {
    }
   
    public Address(String country, String city, String postCode, String addr) {
    	this.country = country;
    	this.city = city;
    	this.postCode = postCode;
    	this.addr = addr;
    }
    
    public String getPostCode() {
 		return postCode;
 	}

 	public void setPostCode(String postCode) {
 		this.postCode = postCode;
 	}

 	public String getCountry() {
 		return country;
 	}

 	public void setCountry(String country) {
 		this.country = country;
 	}

 	public String getCity() {
 		return city;
 	}

 	public void setCity(String city) {
 		this.city = city;
 	}

 	public String getAddr() {
 		return addr;
 	}

 	public void setAddr(String addr) {
 		this.addr = addr;
 	}
}