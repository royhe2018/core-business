package com.sdkj.business.dao.orderImage;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderImage;

public interface OrderImageMapper {
	public void insert(OrderImage image);
	
	public List<OrderImage> findOrderImageList(Map<String,Object> param);
}
