package com.thinkgem.jeesite.JsonTest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    // 进行json操作时忽略该属性
    @JsonIgnore
    private String name;  
    private Integer age;

    // 把Date类型直接转化为想要的格式
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @JsonSerialize(using = DateSerializer.class)
    private Date birthday;

    // 把该属性的名称序列化为另外一个名称
    @JsonProperty("e-mail")
    private String email;  
      
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
      
    public Integer getAge() {  
        return age;  
    }  
    public void setAge(Integer age) {  
        this.age = age;  
    }  
      
    public Date getBirthday() {  
        return birthday;  
    }  
    public void setBirthday(Date birthday) {  
        this.birthday = birthday;  
    }  
      
    public String getEmail() {  
        return email;  
    }  
    public void setEmail(String email) {  
        this.email = email;  
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                '}';
    }
}