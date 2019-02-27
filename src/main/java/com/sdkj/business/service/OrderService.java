package com.sdkj.business.service;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.LeaseTruckOrder;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface OrderService {
	
	public MobileResultVO submitOrder(OrderInfo order,List<OrderRoutePoint> routePointList);
	
	public MobileResultVO orderTaked(OrderInfo order);
	
	public MobileResultVO orderStowage(OrderInfo order);
	
	public MobileResultVO orderSignReceive(OrderInfo order);
	
	public MobileResultVO orderCancle(OrderInfo order);
	
	public MobileResultVO orderCancleByDriver(OrderInfo order);
	
	public MobileResultVO findOrderList(int pageNum,int pageSize,Map<String,Object> param);
	
	public MobileResultVO findOrderDetailInfo(Map<String,Object> param);
	
	public MobileResultVO findOrderRoutePointDetailInfo(Map<String,Object> param);
	
	
	public MobileResultVO submitLeaseTruckOrder(LeaseTruckOrder order,List<OrderRoutePoint> routePointList);
	
	public MobileResultVO copyOrder(String orderId,String userId);
	public void sendDispathOrderMessage(OrderInfo order, List<OrderRoutePoint> routePointList);
}
