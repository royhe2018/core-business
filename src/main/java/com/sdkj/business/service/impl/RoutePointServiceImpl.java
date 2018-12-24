package com.sdkj.business.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.dao.orderRoutePoint.OrderRoutePointMapper;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AliMQProducer;
import com.sdkj.business.service.RoutePointService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class RoutePointServiceImpl implements RoutePointService {
	
	@Autowired
	private OrderRoutePointMapper orderRoutePointMapper;
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
	@Autowired
	private AliMQProducer aliMQProducer;
	@Value("${ali.mq.order.dispatch.topic}")
	private String orderDispatchTopic;
	
	@Override
	public MobileResultVO routePointArrive(OrderRoutePoint point,String driverId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", point.getId());
		OrderRoutePoint pointDB = orderRoutePointMapper.findSingleRoutePoint(param);
		pointDB.setStatus(Constant.ROUTE_POINT_STATUS_ARRIVED);
		pointDB.setArriveTime(DateUtilLH.getCurrentTime());
		orderRoutePointMapper.updateByPrimaryKeySelective(pointDB);
		param.clear();
		param.put("id", point.getOrderId());
		OrderInfo order = orderInfoMapper.findSingleOrder(param);
		param.clear();
		param.put("orderId", point.getOrderId());
		List<OrderRoutePoint> pointList = orderRoutePointMapper.findRoutePointList(param);
		if(pointList!=null && pointList.size()>0){
			OrderRoutePoint startPoint = pointList.get(0);
			if(startPoint.getId().intValue()==point.getId().intValue()){
				order.setStatus(Constant.ORDER_STATUS_SIJIDAODAZHUANGHUO);
			}
			OrderRoutePoint endPoint = pointList.get(pointList.size()-1);
			if(endPoint.getId().intValue()==point.getId().intValue()){
				order.setStatus(Constant.ORDER_STATUS_SIJIDAODASHOUHUO);
			}
			orderInfoMapper.updateById(order);
		}
		//发送到达途经点广播
		Map<String,Object> pointInfoMap = new HashMap<String,Object>();
		pointInfoMap.put("orderId", pointDB.getOrderId());
		pointInfoMap.put("pointId", pointDB.getId());
		this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_ARRIVE_ROUTE_POINT,pointInfoMap);
		return result;
	}

	@Override
	public MobileResultVO routePointLeave(OrderRoutePoint point,String driverId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", point.getId());
		OrderRoutePoint pointDB = orderRoutePointMapper.findSingleRoutePoint(param);
		pointDB.setStatus(Constant.ROUTE_POINT_STATUS_LEAVED);
		pointDB.setLeaveTime(DateUtilLH.getCurrentTime());
		String arriveTimeStr = pointDB.getArriveTime();
		Date arriveTime = DateUtilLH.convertStr2Date(arriveTimeStr, DateUtilLH.timeFormat);
		Date leavedTime = new Date();
		pointDB.setWaitTime((int)(leavedTime.getTime()-arriveTime.getTime())/(60*1000));
		orderRoutePointMapper.updateByPrimaryKeySelective(pointDB);
		param.clear();
		param.put("id", point.getOrderId());
		OrderInfo order = orderInfoMapper.findSingleOrder(param);
		param.clear();
		param.put("orderId", point.getOrderId());
		List<OrderRoutePoint> pointList = orderRoutePointMapper.findRoutePointList(param);
		if(pointList!=null && pointList.size()>0){
			OrderRoutePoint startPoint = pointList.get(0);
			if(startPoint.getId().intValue()==point.getId().intValue()){
				order.setStatus(Constant.ORDER_STATUS_YUNTUZHONG);
				orderInfoMapper.updateById(order);
			}
		}
		//发送离开途经点广播
		Map<String,Object> pointInfoMap = new HashMap<String,Object>();
		pointInfoMap.put("orderId", pointDB.getOrderId());
		pointInfoMap.put("pointId", pointDB.getId());
		this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_LEAVE_ROUTE_POINT,pointInfoMap);
		return result;
	}

}
