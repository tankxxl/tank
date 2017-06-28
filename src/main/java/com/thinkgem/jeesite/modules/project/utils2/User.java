package com.thinkgem.jeesite.modules.project.utils2;
public class User {
	private Long userId;
	private String username;
	private String password;
	private Profile profile;
	
    public User(Long userId, String username, String password, Profile profile) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.profile = profile;
	}
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}