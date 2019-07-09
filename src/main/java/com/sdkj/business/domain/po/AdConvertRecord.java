package com.sdkj.business.domain.po;

public class AdConvertRecord {
	
	private Integer id;
	private String adType;
	private String adid;
	private Integer terminalType;
	private Integer userType;
	private String idfa;
	private String imei;
	private String callBackStatus;
	private String createTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAdType() {
		return adType;
	}
	public void setAdType(String adType) {
		this.adType = adType;
	}
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public Integer getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(Integer terminalType) {
		this.terminalType = terminalType;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getCallBackStatus() {
		return callBackStatus;
	}
	public void setCallBackStatus(String callBackStatus) {
		this.callBackStatus = callBackStatus;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
