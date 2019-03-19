package com.sdkj.business.domain.po;

public class SubCompany {
	private Long id;
	private String companyName;
	private String manageCity;
	private String contanctsName;
	private String contanctsPhone;
	private String address;
	private Float balance;
	private Long parentId;
	private Integer level;
	private String createTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getManageCity() {
		return manageCity;
	}
	public void setManageCity(String manageCity) {
		this.manageCity = manageCity;
	}
	public String getContanctsName() {
		return contanctsName;
	}
	public void setContanctsName(String contanctsName) {
		this.contanctsName = contanctsName;
	}
	public String getContanctsPhone() {
		return contanctsPhone;
	}
	public void setContanctsPhone(String contanctsPhone) {
		this.contanctsPhone = contanctsPhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Float getBalance() {
		return balance;
	}
	public void setBalance(Float balance) {
		this.balance = balance;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
