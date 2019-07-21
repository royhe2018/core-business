package com.sdkj.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.PageHelper;
import com.sdkj.business.dao.distributionSetting.DistributionSettingMapper;
import com.sdkj.business.dao.driverInfo.DriverInfoMapper;
import com.sdkj.business.dao.leaseTruckOrder.LeaseTruckOrderMapper;
import com.sdkj.business.dao.orderFeeItem.OrderFeeItemMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.dao.orderRoutePoint.OrderRoutePointMapper;
import com.sdkj.business.dao.subCompany.SubCompanyMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.dao.vehicleSpecialRequirement.VehicleSpecialRequirementMapper;
import com.sdkj.business.dao.vehicleTypeInfo.VehicleTypeInfoMapper;
import com.sdkj.business.domain.po.DistributionSetting;
import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.po.LeaseTruckOrder;
import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.po.SubCompany;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.po.VehicleSpecialRequirement;
import com.sdkj.business.domain.po.VehicleTypeInfo;
import com.sdkj.business.domain.vo.LableValInfoItem;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AliMQProducer;
import com.sdkj.business.service.OrderFeeItemService;
import com.sdkj.business.service.OrderService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.HttpRequestUtil;
import com.sdlh.common.JsonUtil;
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
	
	@Value("${map.service.url}")
	private String mapServiceUrl;
	
	@Autowired
	private LeaseTruckOrderMapper leaseTruckOrderMapper;

	@Autowired
	private OrderFeeItemMapper orderFeeItemMapper;

	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;

	@Autowired
	private DistributionSettingMapper distributionSettingMapper;
	
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	
	@Value("${road.distance.url}")
	private String roadDistanceUrl;
	
	@Autowired
	private SubCompanyMapper subCompanyMapper;
	
	@Autowired
	private OrderFeeItemService orderFeeItemService;
	
	@Override
	public MobileResultVO submitOrder(OrderInfo order, List<OrderRoutePoint> routePointList) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> resultData = new HashMap<String, Object>();
		order.setStatus(Constant.ORDER_STATUS_WEIJIEDAN);
		order.setCreateTime(DateUtilLH.getCurrentTime());
		correctOrderCity(order,routePointList);
		Map<String, Object> queryMap = new HashMap<String, Object>();
		//queryMap.put("id", order.getUserId());
		//User orderUser = userMapper.findSingleUser(queryMap);
		
		queryMap.clear();
		queryMap.put("userId", order.getUserId());
		queryMap.put("cancleStatus", 0);
		queryMap.put("status", 8);
		logger.info("queryMap:"+JsonUtil.convertObjectToJsonStr(queryMap));
		List<OrderInfo> orderForPayList = this.orderInfoMapper.findOrderList(queryMap);
		logger.info("orderForPayList:"+JsonUtil.convertObjectToJsonStr(orderForPayList));
		//存在未支付的订单，则不让提交新订单
		if(orderForPayList!=null && orderForPayList.size()>0) {
			logger.info("有未完成的支付单");
			result.setCode(2);
			result.setMessage("有未完成的支付单");
			OrderInfo lastNoPayOrder = orderForPayList.get(0);
			Map<String, Object> lastOrderPayInfo = caculateOrderPayInfo(lastNoPayOrder);
			result.setData(lastOrderPayInfo);
			logger.info("lastOrderPayInfo:"+JsonUtil.convertObjectToJsonStr(lastOrderPayInfo));
		}else {
			orderInfoMapper.insert(order);
			resultData.put("orderId", order.getId());
			for (OrderRoutePoint point : routePointList) {
				point.setOrderId(order.getId());
				point.setStatus(Constant.ROUTE_POINT_STATUS_NOT_ARRIVE);
				if (StringUtils.isNotEmpty(point.getContactUserName())) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("account", point.getContactUserPhone());
					param.put("userType", Constant.USER_TYPE_CUSTOMER);
					User dbUser = userMapper.findSingleUser(param);
					if (dbUser != null) {
						point.setDealUserId(dbUser.getId());
					}
				}
				point.setOverTimeFee(0f);
				point.setOverTimeFeeStatus(Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_NO_CAL);
				orderRoutePointMapper.insert(point);
			}

			if (order.getStartFee() != null && order.getStartFee().floatValue() > 0) {
				OrderFeeItem startFee = new OrderFeeItem();
				startFee.setFeeAmount(order.getStartFee());
				startFee.setFeeName("起步费");
				startFee.setOrderId(order.getId());
				startFee.setFeeType(1);
				startFee.setStatus(0);
				orderFeeItemMapper.insert(startFee);
			}
			if (order.getExtraFee() != null && order.getExtraFee().floatValue() > 0) {
				OrderFeeItem extraFee = new OrderFeeItem();
				extraFee.setFeeAmount(order.getExtraFee());
				extraFee.setFeeName("运途费");
				extraFee.setOrderId(order.getId());
				extraFee.setFeeType(2);
				extraFee.setStatus(0);
				orderFeeItemMapper.insert(extraFee);
			}
			if (order.getAttachFee() != null && order.getAttachFee().floatValue() > 0) {
				OrderFeeItem attachFee = new OrderFeeItem();
				attachFee.setFeeAmount(order.getAttachFee());
				attachFee.setFeeName("附加费");
				attachFee.setOrderId(order.getId());
				attachFee.setFeeType(3);
				attachFee.setStatus(0);
				attachFee.setDriverFee(order.getAttachFee());
				orderFeeItemMapper.insert(attachFee);
			}
			result.setData(resultData);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}
		//sendDispathOrderMessage(order, routePointList);
		return result;
	}

	private Map<String, Object> caculateOrderPayInfo(OrderInfo lastNoPayOrder) {
		logger.info("no pay order id:"+lastNoPayOrder.getId());
		Map<String,Object> orderPayInfo = new HashMap<String,Object>();
		Map<String, Object> queryMap = new HashMap<String,Object>();
		orderPayInfo.put("orderId", lastNoPayOrder.getId());
		queryMap.put("orderId", lastNoPayOrder.getId());
		List<Map<String,Object>> feeStatusList = orderFeeItemMapper.findOrderFeeByPayStatus(queryMap);
		if(feeStatusList!=null && feeStatusList.size()>0){
			Float totalAmount =0f;
			Float totalPaidAmount =0f;
			Float totalForPayAmount = 0f;
			for(Map<String,Object> item:feeStatusList){
				Float amount = Float.valueOf(item.get("money").toString());
				if("0".equals(item.get("payStatus").toString())){
					totalForPayAmount += amount;
				}else{
					totalPaidAmount += amount;
				}
				totalAmount +=amount;
			}
			orderPayInfo.put("totalAmount", totalAmount);
			orderPayInfo.put("totalPaidAmount", totalPaidAmount);
			orderPayInfo.put("totalForPayAmount", totalForPayAmount);
		}
		orderPayInfo.put("totalDistance", lastNoPayOrder.getTotalDistance());
		queryMap.clear();
		queryMap.put("orderId", lastNoPayOrder.getId());
		List<OrderRoutePoint> lastOrderRoutePointList = orderRoutePointMapper.findRoutePointList(queryMap);
		Date startTime = new Date();
		Date endTime = new Date();
		if(lastOrderRoutePointList!=null){
			OrderRoutePoint startPoint = lastOrderRoutePointList.get(0);
			String startTimeStr = startPoint.getArriveTime();
			logger.info("startPoint id:"+startPoint.getId()+";"+startTimeStr);
			if(StringUtils.isNotEmpty(startTimeStr)){
				startTime = DateUtilLH.convertStr2Date(startTimeStr, "yyyy-MM-dd HH:mm:ss");
			}
			OrderRoutePoint endPoint =lastOrderRoutePointList.get(lastOrderRoutePointList.size()-1);
			String endTimeStr = endPoint.getArriveTime();
			logger.info("endPoint id:"+endPoint.getId()+";"+endTimeStr);
			if(StringUtils.isNotEmpty(endTimeStr)){
				endTime = DateUtilLH.convertStr2Date(endTimeStr, "yyyy-MM-dd HH:mm:ss");
			}
		}
		orderPayInfo.put("totalTimes", (endTime.getTime()-startTime.getTime())/(1000*60)+"");
		orderPayInfo.put("preContent", "您的订单尚未支付:");
		orderPayInfo.put("afterContent", "");
		return orderPayInfo;
	}

	private void distributeOrderFee(User orderUser,User driver,OrderInfo order, OrderFeeItem feeItem) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", driver.getId());
		DriverInfo driverInfo = driverInfoMapper.findSingleDriver(param);
		param.clear();
		param.put("city", order.getCityName());
		param.put("feeType", feeItem.getFeeType());
		param.put("driverType", driverInfo.getDriverType());
		param.put("vehicleType", order.getVehicleTypeId());
		logger.info("param:"+JsonUtil.convertObjectToJsonStr(param));
		List<DistributionSetting> feeDispatchSettingList = distributionSettingMapper
				.findDistributionSettingList(param);
		if (feeDispatchSettingList != null && feeDispatchSettingList.size()>0) {
			Float distributeTotalFee = 0f;
			DistributionSetting distributionSetting = feeDispatchSettingList.get(0);
			Float clientRefereeRealAmount = distributionSetting.getClientRefereeAmount()*feeItem.getFeeAmount();
			BigDecimal clientRefereeBg = new BigDecimal(clientRefereeRealAmount).setScale(2, RoundingMode.UP);
			feeItem.setClientRefereeFee(clientRefereeBg.floatValue());
			distributeTotalFee += feeItem.getClientRefereeFee();
			
			Float driverRefereeRealAmount = distributionSetting.getDriverRefereeAmount()*feeItem.getFeeAmount();
			BigDecimal driverRefereeBg = new BigDecimal(driverRefereeRealAmount).setScale(2, RoundingMode.UP);
			feeItem.setDriverRefereeFee(driverRefereeBg.floatValue());
			distributeTotalFee += feeItem.getDriverRefereeFee();
			
			Float platformRealAmount = distributionSetting.getPlatformAmount()*feeItem.getFeeAmount();
			BigDecimal platformBg = new BigDecimal(platformRealAmount).setScale(2, RoundingMode.UP);
			feeItem.setPlatFormFee(platformBg.floatValue());
			distributeTotalFee += feeItem.getPlatFormFee();
			
			Float subcompanyRealAmount = distributionSetting.getSubcompanyAmount()*feeItem.getFeeAmount();
			BigDecimal subcompanyBg = new BigDecimal(subcompanyRealAmount).setScale(2, RoundingMode.UP);
			feeItem.setSubCompanyFee(subcompanyBg.floatValue());
			distributeTotalFee += feeItem.getSubCompanyFee();
			param.clear();
			param.put("manageCity", order.getCityName());
			SubCompany subCompany = subCompanyMapper.findSingleSubCompany(param);
			if(subCompany!=null){
				feeItem.setSubCompanyId(subCompany.getId());
			}
			
			feeItem.setDriverFee(feeItem.getFeeAmount()-distributeTotalFee);
		} else {
			feeItem.setDriverFee(feeItem.getFeeAmount());
			feeItem.setClientRefereeFee(0f);
			feeItem.setDriverRefereeFee(0f);
			feeItem.setPlatFormFee(0f);
			feeItem.setSubCompanyFee(0f);
		}
		if(orderUser.getRefereeId()!=null) {
			feeItem.setClientRefereeId(orderUser.getRefereeId());
		}else if(feeItem.getClientRefereeFee()!=null) {
			feeItem.setPlatFormFee(feeItem.getPlatFormFee()+feeItem.getClientRefereeFee());
			feeItem.setClientRefereeFee(0f);
		}
	}

	public void sendDispathOrderMessage(OrderInfo order, List<OrderRoutePoint> routePointList) {
		logger.info("sendDispathOrderMessage start");
		Map<String, Object> orderInfo = new HashMap<String, Object>();
		orderInfo.put("orderId", order.getId());
		orderInfo.put("useTime", order.getUseTruckTime());
		OrderRoutePoint startPoint = routePointList.get(0);
		OrderRoutePoint endPoint = routePointList.get(routePointList.size() - 1);
		orderInfo.put("startPointName", startPoint.getPlaceName());
		orderInfo.put("startPointAddress", startPoint.getAddress());
		orderInfo.put("startPointLocation", startPoint.getLat() + "," + startPoint.getLog());
		orderInfo.put("endPointName", endPoint.getPlaceName());
		orderInfo.put("endPointAddress", endPoint.getAddress());
		orderInfo.put("endPointLocation", endPoint.getLat() + "," + endPoint.getLog());
		orderInfo.put("totalDistance", order.getTotalDistance());
		Map<String, Object> param = new HashMap<String, Object>();
		orderInfo.put("specialRequirement", "");
		if (StringUtilLH.isNotEmpty(order.getSpecialRequirementIds())) {
			String[] ids = order.getSpecialRequirementIds().split(",");
			param.put("ids", ids);
			List<VehicleSpecialRequirement> requirementList = vehicleSpecialRequirementMapper
					.findVehicleSpecialRequirementList(param);
			StringBuffer requirement = new StringBuffer();
			if (requirementList != null && requirementList.size() > 0) {
				for (VehicleSpecialRequirement item : requirementList) {
					requirement.append("," + item.getRequireName());
				}
				if (requirement.toString().startsWith(",")) {
					orderInfo.put("specialRequirement", requirement.toString().replaceFirst(",", ""));
				}
			}
		}
		orderInfo.put("remark", order.getRemark());
		param.clear();
		param.put("orderId", order.getId());
		Map<String,Object> orderFeeDistribute = orderFeeItemMapper.findOrderFeeDistribute(param);
		orderInfo.put("totalFee", orderFeeDistribute.get("driverFee"));
		orderInfo.put("startFee", orderFeeDistribute.get("driverFee"));
		orderInfo.put("extraFee", orderFeeDistribute.get("driverFee"));
		orderInfo.put("attachFee", orderFeeDistribute.get("driverFee"));
		orderInfo.put("payStatus", "0");
		int sendResult = aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_DISPATCH_ORDER, orderInfo);
		logger.info("order dispatch sendResult:" + sendResult);
	}

	@Override
	public MobileResultVO orderTaked(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if (dbOrder != null) {
			if (dbOrder.getCancleStatus().intValue() == Constant.ORDER_CANCLE_STATUS_CANCLE) {
				logger.error("接单失败,订单失效：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单已失效");
			}else if (dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_WEIJIEDAN) {
				logger.error("接单失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单已被接单");
			} else {
				dbOrder.setDriverId(order.getDriverId());
				dbOrder.setTakedTime(DateUtilLH.getCurrentTime());
				dbOrder.setStatus(Constant.ORDER_STATUS_JIEDAN);
				int updateCount = orderInfoMapper.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("接单失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("抢单失败,订单已被接单!");
				} else {
					param.clear();
					param.put("id", order.getDriverId());
					User driver = this.userMapper.findSingleUser(param);
					param.clear();
					param.put("id", dbOrder.getUserId());
					User orderUser = this.userMapper.findSingleUser(param);
					if(driver!=null) {
						param.clear();
						param.put("orderId", order.getId());
						List<OrderFeeItem> feeItemList = this.orderFeeItemMapper.findOrderFeeItemList(param);
						if(feeItemList!=null && feeItemList.size()>0) {
							for(OrderFeeItem feeItem:feeItemList) {
								logger.info("feeItem11:"+JsonUtil.convertObjectToJsonStr(feeItem));
								distributeOrderFee(orderUser,driver,dbOrder, feeItem);
								logger.info("feeItem22:"+JsonUtil.convertObjectToJsonStr(feeItem));
								feeItem.setDriverId(order.getDriverId());
								if(feeItem.getDriverRefereeFee()!=null) {
									if(driver.getRefereeId()!=null) {
										feeItem.setDriverRefereeId(driver.getRefereeId());
									}else {
										//没有司机推荐人将钱直接划到平台
										feeItem.setPlatFormFee(feeItem.getPlatFormFee()+feeItem.getDriverRefereeFee());
										feeItem.setDriverRefereeFee(0f);
									}
								}
								orderFeeItemMapper.updateByPrimaryKey(feeItem);
							}
						}
					}
					// 发送接单成功广播
					Map<String, Object> orderInfoMap = new HashMap<String, Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_TAKED_ORDER, orderInfoMap);
					result.setMessage("抢单成功!");
					
					//发送撤回消息，从已推送用户处撤回消息
					Map<String, Object> orderInfoBackMap = new HashMap<String, Object>();
					orderInfoBackMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_DISMISS_ORDER, orderInfoMap);
					
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
		if (dbOrder != null) {
			if (dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_JIEDAN
					&& dbOrder.getStatus().intValue() != Constant.ORDER_STATUS_SIJIDAODAZHUANGHUO) {
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态有误,不可装货!");
			} else {
				dbOrder.setCloseCodePic(order.getCloseCodePic());
				dbOrder.setThingListPic(order.getThingListPic());
				dbOrder.setStatus(Constant.ORDER_STATUS_ZHUANGHUO);
				int updateCount = orderInfoMapper.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("接单失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("抢单失败,订单已被接单!");
				} else {
					// 发送装货成功广播
					Map<String, Object> orderInfoMap = new HashMap<String, Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_STOWAGE_ORDER, orderInfoMap);
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
		if (dbOrder != null) {
			if (dbOrder.getStatus().intValue() < Constant.ORDER_STATUS_ZHUANGHUO
					|| dbOrder.getStatus().intValue() > Constant.ORDER_STATUS_SIJIDAODASHOUHUO) {
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态有误,不可签收!");
			} else {
				dbOrder.setSignNamePic(order.getSignNamePic());
				dbOrder.setReceiveUserId(order.getReceiveUserId());
				dbOrder.setSignReceiveTime(DateUtilLH.getCurrentTime());
				dbOrder.setStatus(Constant.ORDER_STATUS_YIQIANSHOU);
				int updateCount = orderInfoMapper.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("签收失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("签收失败,订单已被签收!");
				} else {
					// 发送签收成功广播
					Map<String, Object> orderInfoMap = new HashMap<String, Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_SIGN_RECEIVE_ORDER,
							orderInfoMap);
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
		if (dbOrder != null) {
			if (dbOrder.getStatus().intValue() > Constant.ORDER_STATUS_JIEDAN) {
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态变更,不可取消!");
			}else if(dbOrder.getPayStatus()!=null && dbOrder.getPayStatus().intValue()!=Constant.ORDER_PAY_STATUS_NOPAY) {
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单已支付,不可取消!");
			} else {
				dbOrder.setCancleStatus(Constant.ORDER_CANCLE_STATUS_CANCLE);
				dbOrder.setCancelReason(order.getCancelReason());
				int updateCount = orderInfoMapper.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("取消失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("取消失败,订单信息已变更!");
				} else {
					// 发送取消成功广播  推送给已接单的司机
					Map<String, Object> orderInfoMap = new HashMap<String, Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_CANCLE_ORDER, orderInfoMap);
					//发送撤回消息，从已推送用户处撤回消息
					Map<String, Object> orderInfoBackMap = new HashMap<String, Object>();
					orderInfoBackMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_DISMISS_ORDER, orderInfoMap);
					
					result.setMessage("取消成功!");
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO findOrderList(int pageNum, int pageSize, Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		PageHelper.startPage(pageNum, pageSize, false);
		List<Map<String, Object>> orderDisplayList = null;
		if(param.containsKey("orderPeriod") && "4".equals(param.get("orderPeriod")+"")){
			Map<String,Object> queryMap = new HashMap<String,Object>();
			queryMap.put("id", param.get("driverId"));
			User driver = userMapper.findSingleUser(queryMap);
			if(driver!=null && StringUtils.isNotEmpty(driver.getRegisterCity())){
				param.put("driverCity", driver.getRegisterCity());
				queryMap.clear();
				queryMap.put("userId", driver.getId());
				DriverInfo driverInfo = driverInfoMapper.findSingleDriver(queryMap);
				if(driverInfo!=null){
					List<Integer> vehicleTypeList = getDriverCanTakedVehicleTypeList(driverInfo);
					if(vehicleTypeList.size()>0){
						param.put("vehicleTypeList", vehicleTypeList);
					}
				}
				orderDisplayList = orderInfoMapper.findRoadBackOrderList(param);	
			}
		}else{
			orderDisplayList = orderInfoMapper.findOrderInfoListDisplay(param);
		}
		if (orderDisplayList != null) {
			Map<String, Object> queryMap = new HashMap<String, Object>();
			for (Map<String, Object> orderItem : orderDisplayList) {
				Object orderId = orderItem.get("orderId");
				queryMap.put("orderId", orderId);
				List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(queryMap);
				orderItem.put("receiverType", 1);
				if (placePointList != null && placePointList.size() > 0) {
					orderItem.put("placePointList", placePointList);
					OrderRoutePoint startPoint = placePointList.get(0);
					if(param.containsKey("userId")){
						String userId = param.get("userId").toString();
						if(startPoint.getDealUserId()!=null) {
							if(startPoint.getDealUserId()!=null && userId.equals(startPoint.getDealUserId().toString())){
								orderItem.put("receiverType", 2);
							}
						}else {
							if(userId.equals(orderItem.get("userId").toString())) {
								orderItem.put("receiverType", 2);
							}
						}
					}
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
		if (order != null) {
			param.clear();
			param.put("id", order.getDriverId());
			User driver = userMapper.findSingleUser(param);
			List<List<LableValInfoItem>> orderDetailInfo = new ArrayList<List<LableValInfoItem>>();
			List<LableValInfoItem> orderBaseInfo = new ArrayList<LableValInfoItem>();
			LableValInfoItem orderCreateTime = new LableValInfoItem();
			orderCreateTime.setLable("下单时间");
			orderCreateTime.setValue(order.getCreateTime());
			orderBaseInfo.add(orderCreateTime);
			param.clear();
			param.put("orderId", order.getId());
			List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(param);
			OrderRoutePoint startPoint = placePointList.get(0);
			OrderRoutePoint endPoint = placePointList.get(placePointList.size() - 1);
			if (startPoint != null) {
				LableValInfoItem orderStartPlace = new LableValInfoItem();
				orderStartPlace.setLable("出发地");
				orderStartPlace.setValue(startPoint.getPlaceName());
				orderBaseInfo.add(orderStartPlace);
			}
			if (endPoint != null) {
				LableValInfoItem orderEndPlace = new LableValInfoItem();
				orderEndPlace.setLable("目的地");
				orderEndPlace.setValue(endPoint.getPlaceName());
				orderBaseInfo.add(orderEndPlace);
			}

			LableValInfoItem orderAmount = new LableValInfoItem();
			orderAmount.setLable("订单金额");
			orderAmount.setValue(order.getTotalFee() + "");
			orderBaseInfo.add(orderAmount);
			orderDetailInfo.add(orderBaseInfo);
			if (order.getStatus() > Constant.ORDER_STATUS_WEIJIEDAN && driver != null) {
				List<LableValInfoItem> orderTakedInfo = new ArrayList<LableValInfoItem>();
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
			result.setCode(MobileResultVO.CODE_SUCCESS);
			Map<String, Object> orderDetail = new HashMap<String, Object>();
			orderDetail.put("orderId", order.getId());
			orderDetail.put("orderStatus", order.getStatus());
			orderDetail.put("detailInfo", orderDetailInfo);
			result.setData(orderDetail);
		}
		return result;
	}

	@Override
	public MobileResultVO findOrderRoutePointDetailInfo(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		logger.info(JsonUtil.convertObjectToJsonStr(param));
		Map<String, Object> orderItem = orderInfoMapper.findSingleOrderInfoDisplay(param);
		if (orderItem != null && !orderItem.isEmpty()) {
			Object orderId = orderItem.get("orderId");
			Map<String, Object> queryMap = new HashMap<String, Object>();
			if(Constant.ORDER_STATUS_CONFIRMFEE==Integer.valueOf(orderItem.get("status").toString()).intValue()) {
				queryMap.clear();
				queryMap.put("id", orderId);
			    OrderInfo order = orderInfoMapper.findSingleOrder(queryMap);
				Map<String,Object> payInfo = this.caculateOrderPayInfo(order);
				orderItem.put("payInfo", payInfo);
			}
			queryMap.clear();
			queryMap.put("orderId", orderId);
			List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(queryMap);
			if (placePointList != null && placePointList.size() > 0) {
				orderItem.put("receiverType", 1);
				OrderRoutePoint startPoint = placePointList.get(0);
				if(param.containsKey("userId")){
					String userId = param.get("userId").toString();
					if(startPoint.getDealUserId()!=null && userId.equals(startPoint.getDealUserId().toString())){
						orderItem.put("receiverType", 2);
					}
				}
				for(OrderRoutePoint point:placePointList){
					if(StringUtils.isNotEmpty(point.getArriveTime())){
						point.setArriveTime(point.getArriveTime().substring(11));
					}else{
						point.setArriveTime("--");
					}
					if(StringUtils.isNotEmpty(point.getLeaveTime())){
						point.setLeaveTime(point.getLeaveTime().substring(11));
					}else{
						point.setLeaveTime("--");
					}
				}
				orderItem.put("placePointList", placePointList);
			}
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setData(orderItem);
		}
		return result;
	}

	@Override
	public MobileResultVO submitLeaseTruckOrder(LeaseTruckOrder order, List<OrderRoutePoint> routePointList) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> resultData = new HashMap<String, Object>();
		order.setStatus(Constant.ORDER_STATUS_WEIJIEDAN);
		order.setCreateTime(DateUtilLH.getCurrentTime());
		leaseTruckOrderMapper.insert(order);
		resultData.put("orderId", order.getId());
		for (int i = 0; i < routePointList.size(); i++) {
			OrderRoutePoint point = routePointList.get(i);
			point.setOrderId(order.getId());
			point.setStatus(Constant.ROUTE_POINT_STATUS_NOT_ARRIVE);
			orderRoutePointMapper.insert(point);
			if (i == 0) {
				resultData.put("sendType", point.getSendFlag());
				resultData.put("sendPlaceName", point.getPlaceName());
				resultData.put("sendPlaceAddress", point.getAddress());
			} else if (i == 1) {
				resultData.put("fetchType", point.getSendFlag());
				resultData.put("fetchPlaceName", point.getPlaceName());
				resultData.put("fetchPlaceAddress", point.getAddress());
			}
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getVehicleTypeId());
		VehicleTypeInfo vehicleType = vehicleTypeInfoMapper.findSingleVehicleTypeInfo(param);
		resultData.put("vehicleType", vehicleType);
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
	
	@Override
	public MobileResultVO orderCancleByDriver(OrderInfo order) {
		MobileResultVO result = new MobileResultVO();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", order.getId());
		OrderInfo dbOrder = orderInfoMapper.findSingleOrder(param);
		if (dbOrder != null) {
			if (dbOrder.getStatus().intValue() >= Constant.ORDER_STATUS_ZHUANGHUO) {
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("订单状态变更,不可取消!");
			} else {
				dbOrder.setCancleStatus(Constant.ORDER_CANCLE_STATUS_CANCLE);
				int updateCount = orderInfoMapper.updateById(dbOrder);
				if (updateCount != 1) {
					logger.error("取消失败：orderid:" + dbOrder.getId() + ";driverId:" + dbOrder.getDriverId());
					result.setCode(MobileResultVO.CODE_FAIL);
					result.setMessage("取消失败,订单信息已变更!");
				} else {
					// 发送司机端取消成功广播
					Map<String, Object> orderInfoMap = new HashMap<String, Object>();
					orderInfoMap.put("orderId", dbOrder.getId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_CANCLE_ORDER_BY_DRIVER, orderInfoMap);
					result.setMessage("取消成功!");
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO copyOrder(String orderId, String userId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("id", orderId);
		OrderInfo order = this.orderInfoMapper.findSingleOrder(queryMap);
		order.setId(null);
		order.setDriverId(null);
		queryMap.clear();
		queryMap.put("orderId", orderId);
		List<OrderRoutePoint> routePointList = orderRoutePointMapper.findRoutePointList(queryMap);
		if(routePointList!=null && routePointList.size()>0){
			for(OrderRoutePoint routePoint:routePointList){
				routePoint.setId(null);
				routePoint.setArriveTime(null);
				routePoint.setLeaveTime(null);
			}
		}
		this.submitOrder(order, routePointList);
		Map<String,Object> orderResult = new HashMap<String,Object>();
		orderResult.put("orderId", order.getId());
		orderResult.put("startPlaceName", routePointList.get(0).getPlaceName());
		orderResult.put("endPlaceName", routePointList.get(routePointList.size()-1).getPlaceName());
		orderResult.put("totalFee", order.getTotalFee());
		orderResult.put("createTime", order.getCreateTime());
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("下单成功");
		result.setData(orderResult);
		return result;
	}

	@Override
	public MobileResultVO findOrderTakedStatus(String userId, String orderId) {
		Map<String,Object> param = new HashMap<String,Object>();
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		param.put("id", orderId);
		OrderInfo order = orderInfoMapper.findSingleOrder(param);
		if(order!=null  && order.getStatus().intValue()>0 && order.getDriverId()!=null) {
			result.setData(1);
		}else {
			result.setData(0);
		}
		return result;
	}

	@Override
	public MobileResultVO caculateRoutDistance(String origin, String destination, String waypoints) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("origin", origin);
		param.put("destination", destination);
		param.put("waypoints", waypoints);
		JsonNode distanceResult = HttpRequestUtil.getForJsonResult(roadDistanceUrl, param);
		if(distanceResult!= null && distanceResult.has("data")) {
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			result.setData(distanceResult.get("data").asInt());
		}
		return result;
	}

	@Override
	public MobileResultVO findBackRoadOrder(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		List<Map<String,Object>> orderDisplayList = new ArrayList<Map<String,Object>>();
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("id", param.get("userId"));
		User driver = userMapper.findSingleUser(queryMap);
		if(driver!=null){
			param.put("driverCity", driver.getRegisterCity());
			queryMap.clear();
			queryMap.put("userId", driver.getId());
			DriverInfo driverInfo = driverInfoMapper.findSingleDriver(queryMap);
			if(driverInfo!=null){
				List<Integer> vehicleTypeList = getDriverCanTakedVehicleTypeList(driverInfo);
				if(vehicleTypeList.size()>0){
					param.put("vehicleTypeList", vehicleTypeList);
				}
			}
			List<Map<String,Object>> orderDisplayListAll = orderInfoMapper.findBackRoadOrder(param);	
			if (orderDisplayListAll != null) {
				for (Map<String, Object> orderItem : orderDisplayListAll) {
					Object orderId = orderItem.get("orderId");
					String juli = orderItem.get("juli")+"";
					if(StringUtils.isNotEmpty(juli)){
						Float juliInt = Float.valueOf(juli);
						orderItem.put("juli",juliInt.intValue());
						if(juliInt.intValue()<50){
							queryMap.clear();
							queryMap.put("id", orderId);
							OrderInfo order = this.orderInfoMapper.findSingleOrder(queryMap);
							queryMap.clear();
							queryMap.put("orderId", orderId);
							List<OrderRoutePoint> placePointList = orderRoutePointMapper.findRoutePointList(queryMap);
							if (placePointList != null && placePointList.size() > 0) {
								OrderRoutePoint startPoint = placePointList.get(0);
								orderItem.put("startPlaceName", startPoint.getPlaceName());
								orderItem.put("startAddress", startPoint.getAddress());
								OrderRoutePoint endPoint = placePointList.get(placePointList.size()-1);
								orderItem.put("endPlaceName", endPoint.getPlaceName());
								orderItem.put("endAddress", endPoint.getAddress());
								String totalDriverFee = orderFeeItemService.caculateOrderFee(order, driverInfo.getDriverType());
								orderItem.put("totalFee", totalDriverFee);
								orderDisplayList.add(orderItem);
							}
						}
					}
				}
			}
			result.setData(orderDisplayList);
		}
		return result;
	}
	
	private List<Integer> getDriverCanTakedVehicleTypeList(DriverInfo driverInfo) {
		List<Integer> vehicleTypeList = new ArrayList<Integer>();
		if(driverInfo.getVehicleTypeId().intValue()==6){
			vehicleTypeList.add(6);
		}if(driverInfo.getVehicleTypeId().intValue()==7){
			vehicleTypeList.add(6);
			vehicleTypeList.add(7);
		}if(driverInfo.getVehicleTypeId().intValue()==8){
			vehicleTypeList.add(6);
			vehicleTypeList.add(7);
			vehicleTypeList.add(8);
		}if(driverInfo.getVehicleTypeId().intValue()==5){
			vehicleTypeList.add(5);
			vehicleTypeList.add(6);
			vehicleTypeList.add(7);
			vehicleTypeList.add(8);
		}if(driverInfo.getVehicleTypeId().intValue()==9){
			vehicleTypeList.add(5);
			vehicleTypeList.add(6);
			vehicleTypeList.add(7);
			vehicleTypeList.add(8);
			vehicleTypeList.add(9);
		}if(driverInfo.getVehicleTypeId().intValue()==10){
			vehicleTypeList.add(5);
			vehicleTypeList.add(6);
			vehicleTypeList.add(7);
			vehicleTypeList.add(8);
			vehicleTypeList.add(9);
			vehicleTypeList.add(10);
		}
		return vehicleTypeList;
	}
	
	private void correctOrderCity(OrderInfo order, List<OrderRoutePoint> routePointList){
		try{
			OrderRoutePoint startPoint = routePointList.get(0);
			String requestUrl = mapServiceUrl+"/query/location/city?location="+startPoint.getLog()+","+startPoint.getLat();
			logger.info("requestUrl:"+requestUrl);
			String locationCityResultStr= HttpRequestUtil.get(requestUrl);
			logger.info("locationCityResult:"+locationCityResultStr);
			JsonNode locationCityResult =JsonUtil.convertStrToJson(locationCityResultStr);
			if(locationCityResult!=null && locationCityResult.has("data")){
				String cityName = locationCityResult.get("data").asText();
				if(StringUtils.isNotEmpty(cityName)&& !"null".equals(cityName)){
					order.setCityName(cityName);
				}
			}
		}catch(Exception e){
			logger.error("查询起点城市异常", e);
		}
	}
}
