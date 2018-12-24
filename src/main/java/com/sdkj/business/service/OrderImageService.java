package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.po.OrderImage;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface OrderImageService {
	public MobileResultVO addOrderImage(OrderImage target);
	public MobileResultVO findOrderImageInfo(Map<String,Object> params);
}
