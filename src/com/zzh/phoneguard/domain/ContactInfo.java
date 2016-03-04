package com.zzh.phoneguard.domain;
/**
 * 一个联系人的信息类:
 * @author Administrator
 * 
 * @see
 * 		联系人条目:String id;
 * 		联系人姓名:String name;
 * 		email:String email;
 * 		手机号码:String phone;
 * 		即时通讯 : String qq;
 */ 
public class ContactInfo {
	
	    //联系人条目
	    private String id;
	    //联系人姓名
	    private String name;
	    //email
	    private String email;
	    //手机号码
	    private String phone;
	    //即时通讯
	    private String qq;
	    //地址
	    private String address;
	    
	    public String getAddress() {
	        return address;
	    }
	    public void setAddress(String address) {
	        this.address = address;
	    }
	    public String getId() {
	        return id;
	    }
	    public void setId(String id) {
	        this.id = id;
	    }
	    public String getName() {
	        return name;
	    }
	    public void setName(String name) {
	        this.name = name;
	    }
	    public String getEmail() {
	        return email;
	    }
	    public void setEmail(String email) {
	        this.email = email;
	    }
	    public String getPhone() {
	        return phone;
	    }
	    public void setPhone(String phone) {
	        this.phone = phone;
	    }
	    public String getQq() {
	        return qq;
	    }
	    public void setQq(String qq) {
	        this.qq = qq;
	    }
	    @Override
	    public String toString() {
	        return "ContactInfo [id=" + id + ", name=" + name + ", email=" + email
	                + ", phone=" + phone + ", qq=" + qq + ", address=" + address
	                + "]";
	    }   
	
}
