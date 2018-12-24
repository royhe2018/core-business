package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.sdkj.business.dao.leaseTruckOrder.LeaseTruckOrderMapper;
import com.sdkj.business.dao.orderFeeItem.OrderFeeItemMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.dao.orderRoutePoint.OrderRoutePointMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.dao.vehicleSpecialRequirement.VehicleSpecialRequirementMapper;
import com.sdkj.business.dao.vehicleTypeInfo.VehicleTypeInfoMapper;
import com.sdkj.business.domain.po.LeaseTruckOrder;
import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.po.VehicleSpecialRequirement;
import com.sdkj.business.domain.po.VehicleTypeInfo;
import com.sdkj.business.domain.vo.LableValInfoItem;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AliMQProducer;
import com.sdkj.business.service.OrderService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.StringUtilLH;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private OrderRoutePointMapper orderRoutePointMapper;
	@Autowired
	private VehicleSpecialRequirementMapper vehicleSpecialRequirementMapper;
	@Autowired
	private AliMQProducer aliMQProducer;
	@Autowired
	private UserMapper userMapper;
	@Value("${ali.mq.order.dispatch.topic}")
	private String orderDispatchTopic;
	
	@Autowired
	private LeaseTruckOrderMapper leaseTruckOrderMapper;
	
	@Autowired
	private OrderFeeItemMapper orderFeeItemMapper;
	
	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;
	
	@Override
	public MobileResultVO submitOrder(OrderInfo order,
			List<OrderRoutePoint> routePointList) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> resultData = new HashMap<String,Object>();
		order.setStatus(Constant.ORDER_STATUS_WEIJIEDAN);
		order.setCreateTime(DateUtilLH.getCurrentTime());
		orderInfoMapper.insert(order);
		resultData.put("orderId", order.getId());
		for (OrderRoutePoint point : routePointList) {
			point.setOrderId(order.getId());
			point.setStatus(Constant.ROUTE_POINT_STATUS_NOT_ARRIVE);
			if(StringUtils.isNotEmpty(point.getContactUserName())){
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("account", point.getContactUserPhone());
				param.put("userType", Constant.USER_TYPE_CUSTOMER);
				User dbUser = userMapper.findSingleUser(param);
				if(dbUser!=null){
					point.setDealUserId(dbUser.getId());
				}
			}
			orderRoutePointMapper.insert(point);
		}
		
		if(order.getStartFee()!=null && order.getStartFee().floatValue()>0){
			OrderFeeItem startFee = new OrderFeeItem();
			startFee.setFeeAmount(order.getStartFee());
			startFee.setFeeName("起步费");
			startFee.setOrderId(order.getId());
			startFee.setFeeType(1);
			startFee.setStatus(0);
			orderFeeItemMapper.insert(startFee);
		}
		if(order.getExtraFee()!=null && order.getExtraFee().floatValue()>0){
			OrderFeeItem extraFee = new OrderFeeItem();
			extraFee.setFeeAmount(order.getExtraFee());
			extraFee.setFeeName("运途费");
			extraFee.setOrderId(order.getId());
			extraFee.setFeeType(2);
			extraFee.setStatus(0);
			orderFeeItemMapper.insert(extraFee);
		}
		if(order.getAttachFee()!=null && order.getAttachFee().floatValue()>0){
			OrderFeeItem attachFee = new OrderFeeItem();
			attachFee.setFeeAmount(order.getAttachFee());
			attachFee.setFeeName("运途费");
			attachFee.setOrderId(order.getId());
			attachFee.setFeeType(3);
			attachFee.setStatus(0);
			orderFeeItemMapper.insert(attachFee);
		}
		if(order.getStartFee()!=null && order.getStartFee().floatValue()>0){
			OrderFeeItem startFee = new OrderFeeItem();
			startFee.setFeeAmount(order.getStartFee());
			startFee.setFeeName("起步费");
			startFee.setOrderId(order.getId());
		}
		result.setData(resultData);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		sendDispathOrderMessage(order,routePointList);
		return result;
	}

	private void sendDispathOrderMessage(OrderInfo order,List<OrderRoutePoint> routePointList) {
		try{
			Map<String, Object> orderInfo = new HashMap<String, Object>();
			orderInfo.put("orderId", order.getId());
			orderInfo.put("useTime", order.getUseTruckTime());
			OrderRoutePoint startPoint = routePointList.get(0);
			OrderRoutePoint endPoint = routePointList.get(routePointList.size()-1);
			orderInfo.put("startPointName", startPoint.getPlaceName());
			orderInfo.put("startPointAddress", startPoint.getAddress());
			orderInfo.put("startPointLocation", startPoint.getLat()+","+startPoint.getLog());
			orderInfo.put("endPointName", endPoint.getPlaceName());
			orderInfo.put("endPointAddress", endPoint.getAddress());
			orderInfo.put("endPointLocation", endPoint.getLat()+","+endPoint.getLog());
			Map<String,Object> param = new HashMap<String,Object>();
			orderInfo.put("specialRequirement", "");
			if(StringUtilLH.isNotEmpty(order.getSpecialRequirementIds())){
				String[] ids = order.getSpecialRequirementIds().split(",");
				param.put("ids", ids);
				List<VehicleSpecialRequirement> requirementList = vehicleSpecialRequirementMapper.findVehicleSpecialRequirementList(param);
				StringBuffer requirement = new StringBuffer();
				if(requirementList!=null && requirementList.size()>0){
					for(VehicleSpecialRequirement item:requirementList){
						requirement.append(","+item.getRequireName());
					}
					if(requirement.toString().startsWith(",")){
						orderInfo.put("specialRequirement", requirement.toString().replaceFirst(",", ""));
					}
				}
			}
			orderInfo.put("remark", order.getRemark());
			orderInfo.put("totalFee", order.getTotalFee());
			orderInfo.put("startFee", order.getStartFee());
			orderInfo.put("extraFee", order.getExtraFee());
			orderInfo.put("attachFee", order.getAttachFee());
			orderInfo.put("payStatus", "0");
			int sendResult = aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_DISPATCH_ORDER,orderInfo);
			logger.info("order dispatch sendResult:"+sendResult);
		}catch(Exception e){
			logger.error("推送订单派送消息异常", e);
		}
	}

	@Override
	public MobileResultVO orderTaked(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if (dbOrder != null) {
			if (dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_WEIJIEDAN) {
				logger.error("接单失败：orderid:" + dbOrder.getId() + ";driverId:"
						+ dbOrder.getDriverId());
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单已被接单");
			} else {
				dbOrder.setDriverId(order.getDriverId());
				dbOrder.setTakedTime(DateUtilLH.getCurrentTime());
				dbOrder.setStatus(Constant.ORDER_STATUS_JIEDAN);
				int updateCount = orderInfoMapper
						.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("接单失败：orderid:" + dbOrder.getId()
							+ ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("抢单失败,订单已被接单!");
				}else{
					//发送接单成功广播
					Map<String,Object> orderInfoMap = new HashMap<String,Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_TAKED_ORDER,orderInfoMap);
					result.setMessage("抢单成功!");
				}
			}
		} else {
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		}
		return result;
	}

	@Override
	public MobileResultVO orderStowage(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if(dbOrder!=null){
			if(dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_JIEDAN
					&& dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_SIJIDAODAZHUANGHUO){
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态有误,不可装货!");
			}else{
				dbOrder.setCloseCodePic(order.getCloseCodePic());
				dbOrder.setThingListPic(order.getThingListPic());
				dbOrder.setStatus(Constant.ORDER_STATUS_ZHUANGHUO);
				int updateCount = orderInfoMapper
						.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("接单失败：orderid:" + dbOrder.getId()
							+ ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("抢单失败,订单已被接单!");
				}else{
					//发送装货成功广播
					Map<String,Object> orderInfoMap = new HashMap<String,Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_STOWAGE_ORDER,orderInfoMap);
					result.setMessage("装货完成成功!");
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO orderSignReceive(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if(dbOrder!=null){
			if(dbOrder.getStatus().intValue() < Constant.ORDER_STATUS_ZHUANGHUO
					|| dbOrder.getStatus().intValue() > Constant.ORDER_STATUS_SIJIDAODASHOUHUO){
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态有误,不可签收!");
			}else{
				dbOrder.setSignNamePic(order.getSignNamePic());
				dbOrder.setReceiveUserId(order.getReceiveUserId());
				dbOrder.setSignReceiveTime(DateUtilLH.getCurrentTime());
				dbOrder.setStatus(Constant.ORDER_STATUS_YIQIANSHOU);
				int updateCount = orderInfoMapper
						.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("签收失败：orderid:" + dbOrder.getId()
							+ ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("签收失败,订单已被签收!");
				}else{
					//发送签收成功广播
					Map<String,Object> orderInfoMap = new HashMap<String,Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_SIGN_RECEIVE_ORDER,orderInfoMap);
					result.setMessage("签收成功!");
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO orderCancle(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if(dbOrder!=null){
			if(dbOrder.getStatus().intValue() > Constant.ORDER_STATUS_JIEDAN){
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态变更,不可取消!");
			}else{
				dbOrder.setCancleStatus(Constant.ORDER_CANCLE_STATUS_CANCLE);
				int updateCount = orderInfoMapper
						.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("取消失败：orderid:" + dbOrder.getId()
							+ ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("取消失败,订单信息已变更!");
				}else{
					//发送取消成功广播
					Map<String,Object> orderInfoMap = new HashMap<String,Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_CANCLE_ORDER,orderInfoMap);
					result.setMessage("取消成功!");
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO findOrderList(int pageNum, int pageSize,
			Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		PageHelper.startPage(pageNum, pageSize, false);
		List<Map<String,Object>> orderDisplayList = orderInfoMapper.findOrderInfoListDisplay(param);
		if(orderDisplayList!=null){
			Map<String,Object> queryMap = new HashMap<String,Object>();
			for(Map<String,Object> orderItem:orderDisplayList){
				Object orderId = orderItem.get("orderId");
				queryMap.put("orderId", orderId);
				List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(queryMap);
				if(placePointList!=null && placePointList.size()>0) {
					orderItem.put("placePointList", placePointList);
				}
			}
		}
		result.setData(orderDisplayList);
		return result;
	}

	@Override
	public MobileResultVO findOrderDetailInfo(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		OrderInfo order = orderInfoMapper.findSingleOrder(param);
		if(order!=null){
			param.clear();
			param.put("id", order.getDriverId());
			User driver = userMapper.findSingleUser(param);
			List<List<LableValInfoItem>> orderDetailInfo = new ArrayList<List<LableValInfoItem>>();
			List<LableValInfoItem> orderBaseInfo=new ArrayList<LableValInfoItem>();
			LableValInfoItem orderCreateTime = new LableValInfoItem();
			orderCreateTime.setLable("下单时间");
			orderCreateTime.setValue(order.getCreateTime());
			orderBaseInfo.add(orderCreateTime);
			param.clear();
			param.put("orderId", order.getId());
			List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(param);
			OrderRoutePoint startPoint = placePointList.get(0);
			OrderRoutePoint endPoint = placePointList.get(placePointList.size()-1);
			if(startPoint!=null){
				LableValInfoItem orderStartPlace = new LableValInfoItem();
				orderStartPlace.setLable("出发地");
				orderStartPlace.setValue(startPoint.getPlaceName());
				orderBaseInfo.add(orderStartPlace);
			}
			if(endPoint!=null){
				LableValInfoItem orderEndPlace = new LableValInfoItem();
				orderEndPlace.setLable("目的地");
				orderEndPlace.setValue(endPoint.getPlaceName());
				orderBaseInfo.add(orderEndPlace);
			}
			
			LableValInfoItem orderAmount = new LableValInfoItem();
			orderAmount.setLable("订单金额");
			orderAmount.setValue(order.getTotalFee()+"");
			orderBaseInfo.add(orderAmount);
			orderDetailInfo.add(orderBaseInfo);
			if(order.getStatus()>Constant.ORDER_STATUS_WEIJIEDAN && driver!=null){
				List<LableValInfoItem> orderTakedInfo=new ArrayList<LableValInfoItem>();
				LableValInfoItem takedDriverName = new LableValInfoItem();
				takedDriverName.setLable("接单人");
				takedDriverName.setValue(driver.getNickName());
				orderTakedInfo.add(takedDriverName);
				
				LableValInfoItem takedDriverPhone = new LableValInfoItem();
				takedDriverPhone.setLable("接单人手机号");
				takedDriverPhone.setValue(driver.getAccount());
				orderTakedInfo.add(takedDriverPhone);
				
				LableValInfoItem takedTime = new LableValInfoItem();
				takedTime.setLable("接单时间");
				takedTime.setValue(order.getTakedTime());
				orderTakedInfo.add(takedTime);
				orderDetailInfo.add(orderTakedInfo);
			}
			if(false){
				for(int i=0;i<placePointList.size();i++){
					OrderRoutePoint routePoint = placePointList.get(i);
					List<LableValInfoItem> routePointInfo = new ArrayList<LableValInfoItem>();
					LableValInfoItem routePointName = new LableValInfoItem();
					if(i==0){
						routePointName.setLable("装货点名称");
					}else if(i==(placePointList.size()-1)){
						routePointName.setLable("目的地名称");
					}else{
						routePointName.setLable("途经点名称");
					}
					routePointName.setValue(routePoint.getPlaceName());
					routePointInfo.add(routePointName);
					if(routePoint.getStatus().intValue() == Constant.ROUTE_POINT_STATUS_NOT_ARRIVE){
						LableValInfoItem routePointStatus = new LableValInfoItem();
						routePointStatus.setLable("当前状态");
						routePointStatus.setValue("未到达");
						routePointInfo.add(routePointStatus);
						orderDetailInfo.add(routePointInfo);
						break;
					}else {
						if(routePoint.getStatus().intValue() >= Constant.ROUTE_POINT_STATUS_ARRIVED){
							LableValInfoItem arrvieTime = new LableValInfoItem();
							arrvieTime.setLable("到达时间");
							arrvieTime.setValue(routePoint.getArriveTime());
							routePointInfo.add(arrvieTime);
							
							if(routePoint.getStatus().intValue() == Constant.ROUTE_POINT_STATUS_LEAVED){
								LableValInfoItem leaveTime = new LableValInfoItem();
								leaveTime.setLable("离开时间");
								leaveTime.setValue(routePoint.getLeaveTime());
								routePointInfo.add(leaveTime);
								orderDetailInfo.add(routePointInfo);
								
								LableValInfoItem waitTime = new LableValInfoItem();
								waitTime.setLable("等候时间");
								waitTime.setValue(routePoint.getWaitTime()+"(分)");
								routePointInfo.add(waitTime);
								orderDetailInfo.add(routePointInfo);
							}else{
								Date arriveTime = DateUtilLH.convertStr2Date(routePoint.getArriveTime(), DateUtilLH.timeFormat);
								Date nowTime = new Date();
								LableValInfoItem waitTime = new LableValInfoItem();
								waitTime.setLable("等候时间");
								waitTime.setValue(((nowTime.getTime()-arriveTime.getTime())/6000)+"(分)");
								routePointInfo.add(waitTime);
								orderDetailInfo.add(routePointInfo);
								
								LableValInfoItem routePointStatus = new LableValInfoItem();
								routePointStatus.setLable("当前状态");
								routePointStatus.setValue("已到达，等待装卸货");
								routePointInfo.add(routePointStatus);
								orderDetailInfo.add(routePointInfo);
								break;
							}
						}
					}
				}
			}
			result.setCode(MobileResultVO.CODE_SUCCESS);
			Map<String,Object> orderDetail = new HashMap<String,Object>();
			orderDetail.put("orderId", order.getId());
			orderDetail.put("orderStatus", order.getStatus());
			orderDetail.put("detailInfo", orderDetailInfo);
			result.setData(orderDetail);
		}
		return result;
	}

	@Override
	public MobileResultVO findOrderRoutePointDetailInfo(
			Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		List<Map<String,Object>> orderDisplayList = orderInfoMapper.findOrderInfoListDisplay(param);
		if(orderDisplayList!=null && orderDisplayList.size()==1){
			Map<String,Object> orderItem = orderDisplayList.get(0);
			Object orderId = orderItem.get("orderId");
			Map<String,Object> queryMap = new HashMap<String,Object>();
			queryMap.put("orderId", orderId);
			List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(queryMap);
			if(placePointList!=null && placePointList.size()>0) {
				orderItem.put("placePointList", placePointList);
			}
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setData(orderItem);
		}
		return result;
	}

	@Override
	public MobileResultVO submitLeaseTruckOrder(LeaseTruckOrder order,
			List<OrderRoutePoint> routePointList) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> resultData = new HashMap<String,Object>();
		order.setStatus(Constant.ORDER_STATUS_WEIJIEDAN);
		order.setCreateTime(DateUtilLH.getCurrentTime());
		leaseTruckOrderMapper.insert(order);
		resultData.put("orderId", order.getId());
		for (int i=0;i<routePointList.size();i++) {
			OrderRoutePoint point = routePointList.get(i);
			point.setOrderId(order.getId());
			point.setStatus(Constant.ROUTE_POINT_STATUS_NOT_ARRIVE);
			orderRoutePointMapper.insert(point);
			if(i==0){
				resultData.put("sendType",point.getSendFlag());
				resultData.put("sendPlaceName", point.getPlaceName());
				resultData.put("sendPlaceAddress", point.getAddress());
			}else if(i==1){
				resultData.put("fetchType",point.getSendFlag());
				resultData.put("fetchPlaceName", point.getPlaceName());
				resultData.put("fetchPlaceAddress", point.getAddress());
			}
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", order.getVehicleTypeId());
		VehicleTypeInfo vehicleType = vehicleTypeInfoMapper.findSingleVehicleTypeInfo(param);
		resultData.put("vehicleType",vehicleType );
		resultData.put("startTime", order.getStartTime());
		resultData.put("endTime", order.getEndTime());
		resultData.put("totalTime", order.getEndTime());
		resultData.put("contactName", order.getContactName());
		resultData.put("contactPhone", order.getContactPhone());
		resultData.put("leaseFee", 300);
		resultData.put("sendFee", 50);
		resultData.put("fetchFee", 20);
		resultData.put("mortgageFee", 1500);
		resultData.put("totalFee", 1870);
		result.setData(resultData);
		return result;
	}
}
