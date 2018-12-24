package com.sdkj.business.domain.po;

public class ClientVersion {
	
	private Long id;
	private Integer clientType;
	private String version;
	private String upgradeDesc;
	private String setupFileName;
	private String filePath;
	private Integer status;
	private String createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getClientType() {
		return clientType;
	}
	public void setClientType(Integer clientType) {
		this.clientType = clientType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUpgradeDesc() {
		return upgradeDesc;
	}
	public void setUpgradeDesc(String upgradeDesc) {
		this.upgradeDesc = upgradeDesc;
	}
	public String getSetupFileName() {
		return setupFileName;
	}
	public void setSetupFileName(String setupFileName) {
		this.setupFileName = setupFileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
