package com.sdkj.business.domain.vo;

public class MobileResultVO {
	public static int CODE_SUCCESS =1;
	public static int CODE_FAIL=0;
	public static String CHECKCODE_SUCCESS_MESSAGE = "发送成功!";
	public static String CHECKCODE_FAIL_MESSAGE = "发送失败!";
	public static String LOGIN_SUCCESS_MESSAGE = "登陆成功!";
	public static String LOGIN_FAIL_MESSAGE = "登陆失败!";
	public static String REGISTER_SUCCESS_MESSAGE = "注册成功!";
	public static String REGISTER_FAIL_MESSAGE = "注册失败!";
	public static String OPT_SUCCESS_MESSAGE = "操作成功!";
	public static String OPT_FAIL_MESSAGE = "操作失败!";
	private int code=1;
	private String message;
	private Object data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
