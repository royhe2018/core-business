package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface VehicleTypeInfoService {
	public MobileResultVO findVehicleTypeInfo(Map<String,Object> param);
}
