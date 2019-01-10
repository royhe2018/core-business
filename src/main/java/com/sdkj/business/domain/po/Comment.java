package com.sdkj.business.domain.po;

public class Comment {
	
	private Long id;
	private Long orderId;
	private Long userId;
	private Long driverId;
	private Float vehicleScore;
	private Float mannerScore;
	private Float averageScore;
	private String content;
	private String createTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	public Float getVehicleScore() {
		return vehicleScore;
	}
	public void setVehicleScore(Float vehicleScore) {
		this.vehicleScore = vehicleScore;
	}
	public Float getMannerScore() {
		return mannerScore;
	}
	public void setMannerScore(Float mannerScore) {
		this.mannerScore = mannerScore;
	}
	public Float getAverageScore() {
		return averageScore;
	}
	
	public void setAverageScore(Float averageScore) {
		this.averageScore = averageScore;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
