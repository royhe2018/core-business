package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface NoticeService {
	public MobileResultVO findNoticeList(int pageNum,int pageSize,Map<String,Object> param);
}
