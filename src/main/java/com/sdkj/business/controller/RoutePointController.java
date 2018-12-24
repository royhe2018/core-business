package com.sdkj.business.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.RoutePointService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class RoutePointController {
	
	Logger logger = LoggerFactory.getLogger(RoutePointController.class);
	
	@Autowired
	private RoutePointService routePointService;
	
    @RequestMapping(value="/point/arrive",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="到达途经点",optCode="pointArrive")
   	public MobileResultVO arrivePoint(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String pointId = req.getParameter("pointId");
   			String driverId = req.getParameter("driverId");
   			String lat = req.getParameter("lat");
   			String log = req.getParameter("log");
   			OrderRoutePoint point = new OrderRoutePoint(); 
   			point.setOrderId(Long.valueOf(orderId));
   			point.setId(Long.valueOf(pointId));
   			point.setLat(lat);
   			point.setLog(log);
   			result = routePointService.routePointArrive(point,driverId);
   		}catch(Exception e) {
   			logger.error("途经点到达异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/point/leave",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="离开途经点",optCode="pointLeave")
   	public MobileResultVO leavePoint(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String pointId = req.getParameter("pointId");
   			String driverId = req.getParameter("driverId");
   			String lat = req.getParameter("lat");
   			String log = req.getParameter("log");
   			OrderRoutePoint point = new OrderRoutePoint(); 
   			point.setOrderId(Long.valueOf(orderId));
   			point.setId(Long.valueOf(pointId));
   			point.setLat(lat);
   			point.setLog(log);
   			result = routePointService.routePointLeave(point,driverId);
   		}catch(Exception e) {
   			logger.error("途经点离开异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
