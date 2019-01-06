package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface ServiceQuestionService {
	public MobileResultVO findServiceQuestionList(Map<String,Object> param);
}
