package com.sdkj.business.domain.po;

public class DistributionSetting {
	
	private Long id;
	private String province;
	private String city;
	private String driverType;
	private Integer vehicleType;
	private Integer feeType;
	private Float driverAmount;
	private Float clientRefereeAmount;
	private Float driverRefereeAmount;
	private Float platformAmount;
	
	
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
	public String getDriverType() {
		return driverType;
	}
	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}
	public Integer getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(Integer vehicleType) {
		this.vehicleType = vehicleType;
	}
	public Integer getFeeType() {
		return feeType;
	}
	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}
	public Float getDriverAmount() {
		return driverAmount;
	}
	public void setDriverAmount(Float driverAmount) {
		this.driverAmount = driverAmount;
	}
	public Float getClientRefereeAmount() {
		return clientRefereeAmount;
	}
	public void setClientRefereeAmount(Float clientRefereeAmount) {
		this.clientRefereeAmount = clientRefereeAmount;
	}
	public Float getDriverRefereeAmount() {
		return driverRefereeAmount;
	}
	public void setDriverRefereeAmount(Float driverRefereeAmount) {
		this.driverRefereeAmount = driverRefereeAmount;
	}
	public Float getPlatformAmount() {
		return platformAmount;
	}
	public void setPlatformAmount(Float platformAmount) {
		this.platformAmount = platformAmount;
	}
}
