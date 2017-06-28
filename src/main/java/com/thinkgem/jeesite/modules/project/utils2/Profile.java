package com.thinkgem.jeesite.modules.project.utils2;

import java.util.Date;
import java.util.Map;

public class Profile {
	
	private Map<String, String> phone;
	private Address[] address;
	private Date birthDate;
	private String email;
	
    public Profile(Map<String, String> phone, Address[] address, Date birthDate, String email) {
		super();
		this.phone = phone;
		this.address = address;
		this.birthDate = birthDate;
		this.email = email;
	}
    
	public Map<String, String> getPhone() {
		return phone;
	}
	
	public void setPhone(Map<String, String> phone) {
		this.phone = phone;
	}
	public Address[] getAddress() {
		return address;
	}
	public void setAddress(Address[] address) {
		this.address = address;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}