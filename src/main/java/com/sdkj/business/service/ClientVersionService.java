package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface ClientVersionService {
	public MobileResultVO findValidClientVersion(String clientType,String versionNum);
}
