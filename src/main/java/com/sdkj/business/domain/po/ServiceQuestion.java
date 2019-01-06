package com.sdkj.business.domain.po;

public class ServiceQuestion {
	private Long id;
	private String title;
	private String url;
	private String createTime;
	
	public Long getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
}
