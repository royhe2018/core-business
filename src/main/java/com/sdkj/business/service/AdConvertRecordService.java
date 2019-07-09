package com.sdkj.business.service;

import com.sdkj.business.domain.po.AdConvertRecord;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface AdConvertRecordService {
	public MobileResultVO addAdConvertRecord(AdConvertRecord target);
	
	public MobileResultVO callBackAdConvert(String idfa,String imei);
}
