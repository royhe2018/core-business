package com.sdkj.business.domain.po;

public class RefereeRecord {
	private Long id;
	private String userDeviceId;
	private Long refereeUserId;
	private String createTime;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserDeviceId() {
		return userDeviceId;
	}
	public void setUserDeviceId(String userDeviceId) {
		this.userDeviceId = userDeviceId;
	}
	public Long getRefereeUserId() {
		return refereeUserId;
	}
	public void setRefereeUserId(Long refereeUserId) {
		this.refereeUserId = refereeUserId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
