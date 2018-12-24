package com.sdkj.business.service;

import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface RoutePointService {
	public MobileResultVO routePointArrive(OrderRoutePoint point,String driverId);
	public MobileResultVO routePointLeave(OrderRoutePoint point,String driverId);
}
