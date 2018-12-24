package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface DriverInfoService {
	public MobileResultVO registerDriverInfo(DriverInfo target);
	
	public MobileResultVO findDriverInfo(Map<String,Object> param);
	
	public MobileResultVO updateDriverOnDutyStatus(DriverInfo target);
}
