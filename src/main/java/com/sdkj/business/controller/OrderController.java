package com.sdkj.business.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyuncs.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sdkj.business.domain.po.LeaseTruckOrder;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.StringUtilLH;

@Controller
public class OrderController {

	private Logger logger = LoggerFactory.getLogger(OrderController.class);
	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/order/submit", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "提交订单", optCode = "orderSubmit")
	public MobileResultVO orderSubmit(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			List<OrderRoutePoint> pointList = new ArrayList<OrderRoutePoint>();
			StringBuffer errorMsg = setupAndValidParameter(req, order, pointList);
			if (StringUtilLH.isEmpty(errorMsg.toString())) {
				result = orderService.submitOrder(order, pointList);
				if (result.getCode() == MobileResultVO.CODE_SUCCESS) {
					logger.info("before dispatch");
					orderService.sendDispathOrderMessage(order, pointList);
					logger.info("dispatch end");
				}
			} else {
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("订单提交异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		logger.info("orderSubmit result:" + JsonUtil.convertObjectToJsonStr(result));
		return result;
	}

	@RequestMapping(value = "/order/take", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "提交接单", optCode = "orderTaked")
	public MobileResultVO takeOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			String orderId = req.getParameter("orderId");
			String driverId = req.getParameter("driverId");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				driverId = paramMap.get("driverId");
			}
			logger.info("orderId:" + orderId + ";driverId:" + driverId);
			order.setId(Long.valueOf(orderId));
			order.setDriverId(Long.valueOf(driverId));
			result = orderService.orderTaked(order);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/stowage", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "装货完成", optCode = "orderStowage")
	public MobileResultVO stowageOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			String orderId = req.getParameter("orderId");
			String userId = req.getParameter("userId");
			String closeCodePic = req.getParameter("closeCodePic");
			String thingListPic = req.getParameter("thingListPic");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				userId = paramMap.get("userId");
				closeCodePic = paramMap.get("closeCodePic");
				thingListPic = paramMap.get("thingListPic");
			}
			order.setId(Long.valueOf(orderId));
			order.setCloseCodePic(closeCodePic);
			order.setThingListPic(thingListPic);
			result = orderService.orderStowage(order);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/cancle", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "订单取消", optCode = "orderCancel")
	public MobileResultVO cancelOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			String orderId = req.getParameter("orderId");
			String cancelReason = req.getParameter("cancelReason");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				cancelReason = paramMap.get("cancelReason");
			}
			order.setId(Long.valueOf(orderId));
			order.setCancelReason(cancelReason);
			result = orderService.orderCancle(order);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/sign/receive", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "到达收货点", optCode = "orderSignReceive")
	public MobileResultVO signReceiveOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			String orderId = req.getParameter("orderId");
			String userId = req.getParameter("userId");
			String signNamePic = req.getParameter("signNamePic");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				userId = paramMap.get("userId");
				signNamePic = paramMap.get("signNamePic");
			}
			
			order.setId(Long.valueOf(orderId));
			order.setSignNamePic(signNamePic);
			order.setReceiveUserId(Long.valueOf(userId));
			result = orderService.orderSignReceive(order);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/list", method = RequestMethod.POST)
	@ResponseBody
	public MobileResultVO orderList(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String driverId = req.getParameter("driverId");
			String orderPeriod = req.getParameter("orderPeriod");
			String pageNumStr = req.getParameter("pageNum");
			String pageSizeStr = req.getParameter("pageSize");
			String userType = req.getParameter("userType");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				driverId = paramMap.get("driverId");
				userId = paramMap.get("userId");
				orderPeriod = paramMap.get("orderPeriod");
				pageNumStr = paramMap.get("pageNum");
				pageSizeStr = paramMap.get("pageSize");
				userType = paramMap.get("userType");
			}
			
			int pageSize = 10;
			int pageNum = 1;
			if (StringUtilLH.isNotEmpty(pageNumStr)) {
				pageNum = Integer.valueOf(pageNumStr);
			}
			if (StringUtilLH.isNotEmpty(pageSizeStr)) {
				pageSize = Integer.valueOf(pageSizeStr);
			}
			Map<String, Object> param = new HashMap<String, Object>();
			if (StringUtilLH.isNotEmpty(userId)) {
				param.put("userId", userId);
			}
			if (StringUtilLH.isNotEmpty(orderPeriod)) {
				param.put("orderPeriod", orderPeriod);
			}
			if (StringUtilLH.isNotEmpty(driverId)) {
				param.put("driverId", driverId);
			}
			
			if(StringUtilLH.isNotEmpty(userType)){
				if("1".equals(userType)){
					param.remove("driverId");
				}else if("2".equals(userType)){
					param.remove("userId");
				}
			}
			result = orderService.findOrderList(pageNum, pageSize, param);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/detail", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "订单详情", optCode = "orderDetail")
	public MobileResultVO orderDetailInfo(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String id = req.getParameter("orderId");
			String driverId = req.getParameter("driverId");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				driverId = paramMap.get("driverId");
				userId = paramMap.get("userId");
				id = paramMap.get("orderId");
			}
			Map<String, Object> param = new HashMap<String, Object>();
			if (StringUtilLH.isNotEmpty(userId)) {
				param.put("userId", userId);
			}
			if (StringUtilLH.isNotEmpty(userId)) {
				param.put("userId", userId);
			}
			if (StringUtilLH.isNotEmpty(driverId)) {
				param.put("driverId", driverId);
			}
			if (StringUtilLH.isNotEmpty(id)) {
				param.put("id", id);
			}
			result = orderService.findOrderDetailInfo(param);
		} catch (Exception e) {
			logger.error("订单详情异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/order/routepoint/detail", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "订单到达点列表", optCode = "orderRoutepointDetail")
	public MobileResultVO orderRoutePointDetailInfo(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String id = req.getParameter("orderId");
			String driverId = req.getParameter("driverId");
			String userType = req.getParameter("userType");
			
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				driverId = paramMap.get("driverId");
				userId = paramMap.get("userId");
				id = paramMap.get("orderId");
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			if (StringUtilLH.isNotEmpty(id)) {
				param.put("id", id);
			}
//			if (StringUtilLH.isNotEmpty(userId)) {
//				param.put("userId", userId);
//			}
//			if (StringUtilLH.isNotEmpty(driverId)) {
//				param.put("driverId", driverId);
//			}
//			
//			if(StringUtilLH.isNotEmpty(userType)){
//				if("1".equals(userType)){
//					param.remove("driverId");
//				}else if("2".equals(userType)){
//					param.remove("userId");
//					if(StringUtils.isEmpty(driverId)){
//						param.put("driverId", userId);
//					}
//				}
//			}
			result = orderService.findOrderRoutePointDetailInfo(param);
		} catch (Exception e) {
			logger.error("订单详情异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	private StringBuffer setupAndValidParameter(HttpServletRequest req, OrderInfo order,
			List<OrderRoutePoint> routePointList) {
		StringBuffer errorMsg = new StringBuffer();
		String userId = req.getParameter("userId");
		String contactName = req.getParameter("contactName");
		String contactPhone = req.getParameter("contactPhone");
		String distributionFeeId = req.getParameter("distributionFeeId");
		String extraFee = req.getParameter("extraFee");
		String insuranceFee = req.getParameter("insuranceFee");
		String placeList = req.getParameter("placeList");
		String remark = req.getParameter("remark");
		String serviceVehicleLevel = req.getParameter("serviceVehicleLevel");
		String specialRequirementList = req.getParameter("specialRequirementList");
		String startFee = req.getParameter("startFee");
		String totalFee = req.getParameter("totalFee");
		String useTime = req.getParameter("useTime");
		String cityName = req.getParameter("cityName");
		String totalDistance = req.getParameter("totalDistance");
		String vehicleTypeInfoId = req.getParameter("vehicleTypeInfoId");
		String useTimeType = req.getParameter("useTimeType");

		Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
		if (paramMap != null) {
			userId = paramMap.get("userId");
			contactName = paramMap.get("contactName");
			contactPhone = paramMap.get("contactPhone");
			distributionFeeId = paramMap.get("distributionFeeId");
			extraFee = paramMap.get("extraFee");
			insuranceFee = paramMap.get("insuranceFee");
			placeList = paramMap.get("placeList");
			remark = paramMap.get("remark");
			serviceVehicleLevel = paramMap.get("serviceVehicleLevel");
			specialRequirementList = paramMap.get("specialRequirementList");
			startFee = paramMap.get("startFee");
			totalFee = paramMap.get("totalFee");
			useTime = paramMap.get("useTime");
			cityName = paramMap.get("cityName");
			totalDistance = paramMap.get("totalDistance");
			vehicleTypeInfoId = paramMap.get("vehicleTypeInfoId");
			useTimeType = paramMap.get("useTimeType");
		}

		if (StringUtilLH.isNotEmpty(totalDistance)) {
			order.setTotalDistance(Float.valueOf(totalDistance));
		}
		if (StringUtilLH.isNotEmpty(cityName)) {
			order.setCityName(cityName);
		} else {
			errorMsg.append("当前城市定位异常!");
		}
		if (StringUtilLH.isNotEmpty(userId)) {
			order.setUserId(Long.valueOf(userId));
		} else {
			errorMsg.append("请先登陆!");
		}
		if (StringUtilLH.isNotEmpty(vehicleTypeInfoId)) {
			order.setVehicleTypeId(Long.valueOf(vehicleTypeInfoId));
		} else {
			errorMsg.append("请选择车型!");
		}
		if (StringUtilLH.isNotEmpty(contactName)) {
			order.setContactName(contactName);
		} else {
			errorMsg.append("请输入订单联系人!");
		}
		if (StringUtilLH.isNotEmpty(contactPhone)) {
			order.setContactPhone(contactPhone);
		} else {
			errorMsg.append("请输入订单联系人电话!");
		}
		if (StringUtilLH.isNotEmpty(distributionFeeId)) {
			order.setDistributionFeeId(Long.valueOf(distributionFeeId));
		} else {
			errorMsg.append("配送费匹配错误!");
		}
		if (StringUtilLH.isNotEmpty(extraFee)) {
			order.setExtraFee(Float.valueOf(extraFee));
		} else {
			order.setExtraFee(0f);
		}

		if (StringUtilLH.isNotEmpty(insuranceFee)) {
			order.setInsuranceFee(Float.valueOf(insuranceFee));
		} else {
			order.setInsuranceFee(Float.valueOf(0));
		}
		if (StringUtilLH.isNotEmpty(placeList)) {
			ArrayNode placeNodeList = JsonUtil.convertStrToJsonArray(placeList);
			if (placeNodeList != null && placeNodeList.size() > 0) {
				for (int i = 0; i < placeNodeList.size(); i++) {
					JsonNode placeNode = placeNodeList.get(i);
					OrderRoutePoint placePoint = new OrderRoutePoint();
					placePoint.setAddress(placeNode.get("placeAddress").asText());
					placePoint.setPlaceName(placeNode.get("placeName").asText());
					if (placeNode.has("contactName") && StringUtils.isNotEmpty(placeNode.get("contactName").asText())) {
						placePoint.setContactUserName(placeNode.get("contactName").asText());
					} else {
						placePoint.setContactUserName(contactName);
					}
					if (placeNode.has("contactPhone")
							&& StringUtils.isNotEmpty(placeNode.get("contactPhone").asText())) {
						placePoint.setContactUserPhone(placeNode.get("contactPhone").asText());
					} else {
						placePoint.setContactUserPhone(contactPhone);
					}
					placePoint.setOrderNum(i + 1);
					String location = placeNode.get("placePosition").asText();
					String[] locationArr = location.split(",");
					placePoint.setLog(locationArr[0]);
					placePoint.setLat(locationArr[1]);
					placePoint.setOrderType("1");
					routePointList.add(placePoint);
				}
			} else {
				errorMsg.append("地址有误!");
			}
		} else {
			errorMsg.append("地址有误!");
		}

		if (StringUtilLH.isNotEmpty(remark)) {
			order.setRemark(remark);
		}

		if (StringUtilLH.isNotEmpty(serviceVehicleLevel)) {
			order.setServiceVehicleLevelId(Long.valueOf(serviceVehicleLevel));
		} else {
			errorMsg.append("车辆级别有误!");
		}

		if (StringUtilLH.isNotEmpty(specialRequirementList)) {
			order.setSpecialRequirementIds(specialRequirementList);
		}

		if (StringUtilLH.isNotEmpty(startFee)) {
			order.setStartFee(Float.valueOf(startFee));
		} else {
			errorMsg.append("起步费用有误!");
		}

		if (StringUtilLH.isNotEmpty(totalFee)) {
			order.setTotalFee(Float.valueOf(totalFee));
		} else {
			errorMsg.append("费用有误!");
		}

		if (StringUtilLH.isNotEmpty(useTime)) {
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MINUTE, 15);
			String startTimeLimit = DateUtilLH.convertDate2Str(ca.getTime(), "yyyy-MM-dd HH:mm");
			if (useTime.compareTo(startTimeLimit) < 0) {
				useTime = startTimeLimit;
			}
			// order.setUseTruckTime(useTime+":00");
			order.setUseTruckTime(useTime);
		} else {
			errorMsg.append("用车时间有误!");
		}

		if (StringUtilLH.isNotEmpty(useTimeType)) {
			order.setUseTimeType(Integer.valueOf(useTimeType));
		}
		return errorMsg;
	}

	@RequestMapping(value = "/lease/truck/order/submit", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "提交订单", optCode = "orderSubmit")
	public MobileResultVO leaseTruckOrderSubmit(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			LeaseTruckOrder order = new LeaseTruckOrder();
			List<OrderRoutePoint> pointList = new ArrayList<OrderRoutePoint>();
			StringBuffer errorMsg = setupAndValidParameterWithLeaseOrder(req, order, pointList);
			if (StringUtilLH.isEmpty(errorMsg.toString())) {
				result = orderService.submitLeaseTruckOrder(order, pointList);
			} else {
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage(errorMsg.toString());
			}
		} catch (Exception e) {
			logger.error("订单提交异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	private StringBuffer setupAndValidParameterWithLeaseOrder(HttpServletRequest req, LeaseTruckOrder order,
			List<OrderRoutePoint> routePointList) {
		StringBuffer errorMsg = new StringBuffer();
		String userId = req.getParameter("userId");
		String contactName = req.getParameter("contactName");
		String contactPhone = req.getParameter("contactPhone");
		String serviceStationId = req.getParameter("serviceStationId");
		String vehicleTypeInfoId = req.getParameter("vehicleTypeInfoId");
		String placeList = req.getParameter("placeList");
		String specialRequirementList = req.getParameter("specialRequirementList");
		String startTime = req.getParameter("startTime");
		String endTime = req.getParameter("endTime");
		if (StringUtilLH.isNotEmpty(userId)) {
			order.setUserId(Long.valueOf(userId));
		} else {
			errorMsg.append("请先登陆!");
		}
		if (StringUtilLH.isNotEmpty(vehicleTypeInfoId)) {
			order.setVehicleTypeId(Long.valueOf(vehicleTypeInfoId));
		} else {
			errorMsg.append("请选择车型!");
		}
		if (StringUtilLH.isNotEmpty(contactName)) {
			order.setContactName(contactName);
		}

		if (StringUtilLH.isNotEmpty(contactPhone)) {
			order.setContactPhone(contactPhone);
		}

		if (StringUtilLH.isNotEmpty(placeList)) {
			ArrayNode placeNodeList = JsonUtil.convertStrToJsonArray(placeList);
			if (placeNodeList != null && placeNodeList.size() > 0) {
				for (int i = 0; i < placeNodeList.size(); i++) {
					JsonNode placeNode = placeNodeList.get(i);
					OrderRoutePoint placePoint = new OrderRoutePoint();
					placePoint.setAddress(placeNode.get("placeAddress").asText());
					placePoint.setPlaceName(placeNode.get("placeName").asText());
					if (placeNode.has("sendType")) {
						placePoint.setSendFlag(Integer.valueOf(placeNode.get("sendType").asText()));
					}
					placePoint.setOrderNum(i + 1);
					String location = placeNode.get("placePosition").asText();
					String[] locationArr = location.split(",");
					placePoint.setLog(locationArr[0]);
					placePoint.setLat(locationArr[1]);
					placePoint.setOrderType("2");
					routePointList.add(placePoint);
				}
			} else {
				errorMsg.append("地址有误!");
			}
		} else {
			errorMsg.append("地址有误!");
		}
		if (StringUtilLH.isNotEmpty(specialRequirementList)) {
			order.setSpecialRequirementIds(specialRequirementList);
		}

		if (StringUtilLH.isNotEmpty(serviceStationId)) {
			order.setServiceStationId(Long.valueOf(serviceStationId));
		} else {
			errorMsg.append("服务站信息有误!");
		}

		if (StringUtilLH.isNotEmpty(startTime)) {
			order.setStartTime(startTime);
		} else {
			errorMsg.append("租用时间起有误!");
		}

		if (StringUtilLH.isNotEmpty(endTime)) {
			order.setEndTime(endTime);
		} else {
			errorMsg.append("租用时间起有误!");
		}
		return errorMsg;
	}

	@RequestMapping(value = "/order/cancle/by/driver", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "司机端订单取消", optCode = "cancelOrderByDriver")
	public MobileResultVO cancelOrderByDriver(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			OrderInfo order = new OrderInfo();
			String orderId = req.getParameter("orderId");
			order.setId(Long.valueOf(orderId));
			result = orderService.orderCancleByDriver(order);
		} catch (Exception e) {
			logger.error("订单接单异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/copy/order", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "重新提交订单", optCode = "copyOrder")
	public MobileResultVO copyOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String orderId = req.getParameter("orderId");
			String userId = req.getParameter("userId");
			result = orderService.copyOrder(orderId, userId);
		} catch (Exception e) {
			logger.error("订单提交异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/find/order/taked/status", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "获取订单接单状态", optCode = "findOrderTakedStatus")
	public MobileResultVO findOrderTakedStatus(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String orderId = req.getParameter("orderId");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				userId = paramMap.get("userId");
			}
			result = orderService.findOrderTakedStatus(userId, orderId);
		} catch (Exception e) {
			logger.error("获取订单接单状态异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}

	@RequestMapping(value = "/find/road/distance", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "计算路线距离", optCode = "findRoadDistance")
	public MobileResultVO findRoadDistance(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String origin = req.getParameter("origin");
			String destination = req.getParameter("destination");
			String waypoints = req.getParameter("waypoints");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				origin = paramMap.get("origin");
				destination = paramMap.get("destination");
				waypoints = paramMap.get("waypoints");
			}
			result = orderService.caculateRoutDistance(origin, destination, waypoints);
		} catch (Exception e) {
			logger.error("计算路线距离异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
	
	@RequestMapping(value = "/find/back/road/order", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "查询返程单", optCode = "findRoadDistance")
	public MobileResultVO findBackRoadOrder(HttpServletRequest req) {
		MobileResultVO result = null;
		try {
			String startlon = req.getParameter("startlon");
			String startlat = req.getParameter("startlat");
			String endlon = req.getParameter("endlon");
			String endlat = req.getParameter("endlat");
			String userId = req.getParameter("userId");
			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				startlon = paramMap.get("startlon");
				startlat = paramMap.get("startlat");
				endlon = paramMap.get("endlon");
				endlat = paramMap.get("endlat");
				userId = paramMap.get("userId");
			}
			Map<String,Object> param = new HashMap<String,Object>();
			if(StringUtils.isEmpty(startlat)|| StringUtils.isEmpty(startlon) || StringUtils.isEmpty(userId)){
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("未获取到查询位置!");
			}else{
				param.put("startlon", startlon);
				param.put("startlat", startlat);
				param.put("userId", userId);
				if(StringUtils.isNotEmpty(endlon) && StringUtils.isNotEmpty(endlat)){
					param.put("endlon", endlon);
					param.put("endlat", endlat);
				}
				result = orderService.findBackRoadOrder(param); 
			}
		} catch (Exception e) {
			logger.error("计算路线距离异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
}
