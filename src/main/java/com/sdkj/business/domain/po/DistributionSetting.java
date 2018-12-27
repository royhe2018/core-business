package com.sdkj.business.domain.po;

public class DistributionSetting {
	private Long id;
	private String province;
	private String city;
	private Integer feeType;
	private Integer roleType;
	private Integer distributionMethod;
	private Float amount;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getFeeType() {
		return feeType;
	}
	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	public Integer getRoleType() {
		return roleType;
	}
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}
	public Integer getDistributionMethod() {
		return distributionMethod;
	}
	public void setDistributionMethod(Integer distributionMethod) {
		this.distributionMethod = distributionMethod;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	
}
