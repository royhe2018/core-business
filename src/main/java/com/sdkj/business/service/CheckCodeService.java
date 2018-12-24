package com.sdkj.business.service;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface CheckCodeService {
	public MobileResultVO sendCheckCode(String phoneNumber);
	
	public boolean validCheckCode(String phoneNumber,String code);
}
