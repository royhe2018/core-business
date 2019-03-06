package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.orderFeeItem.OrderFeeItemMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.dao.orderRoutePoint.OrderRoutePointMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.OrderRoutePoint;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AliMQProducer;
import com.sdkj.business.service.OrderFeeItemService;
import com.sdkj.business.service.component.aliPay.ALIPayComponent;
import com.sdkj.business.service.component.wxPay.WXPayComponent;
import com.sdkj.business.service.component.wxPay.WxappPayDto;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;

@Service
@Transactional
public class OrderFeeItemServiceImpl implements OrderFeeItemService {
	
	Logger logger = LoggerFactory.getLogger(OrderFeeItemServiceImpl.class);
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	
	@Autowired
	private OrderFeeItemMapper orderFeeItemMapper; 
	@Autowired
	private WXPayComponent wxPayComponent;
	
	@Autowired
	private ALIPayComponent aliPayComponent;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private OrderRoutePointMapper orderRoutePointMapper;
	
	@Autowired
	private AliMQProducer aliMQProducer;
	@Value("${ali.mq.order.dispatch.topic}")
	private String orderDispatchTopic;
	
	@Override
	public MobileResultVO addFeeItem(List<OrderFeeItem> itemList) {
		MobileResultVO result = new MobileResultVO();
		if(itemList!=null && itemList.size()>0){
			Map<String,Object> orderQueryMap=new HashMap<String,Object>();
			orderQueryMap.put("id", itemList.get(0).getOrderId());
			OrderInfo order = orderInfoMapper.findSingleOrder(orderQueryMap);
			//额外费用增加推荐人项分配，暂无分配，后需可增加设置项
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("id",order.getUserId());
			User clientUser = userMapper.findSingleUser(param);
			param.put("id",order.getDriverId());
			User driverUser = userMapper.findSingleUser(param);
			Float noPaidFee=0f;
			for(OrderFeeItem target:itemList){
				if(target.getFeeAmount()>0){
					if(target.getId()!=null) {
						param.clear();
						param.put("id", target.getId());
						OrderFeeItem feeItemDB = orderFeeItemMapper.findSingleOrderFeeItem(param);
						if(feeItemDB.getStatus().intValue()==1||feeItemDB.getFeeType().intValue()==1 ||feeItemDB.getFeeType().intValue()==2) {
							continue;
						}
					}
					noPaidFee +=target.getFeeAmount();
					target.setDriverFee(target.getFeeAmount());
					target.setDriverId(driverUser.getId());
					target.setClientRefereeId(clientUser.getRefereeId());
					target.setClientRefereeFee(0f);
					target.setDriverRefereeId(driverUser.getRefereeId());
					target.setDriverRefereeFee(0f);
					target.setPlatFormFee(0f);
					target.setStatus(Constant.FEE_ITEM_PAY_STATUS_NOPAY);
					if(target.getId()!=null) {
						orderFeeItemMapper.updateByPrimaryKey(target);
					}else {
						orderFeeItemMapper.insert(target);
						if("等候逾时费".equals(target.getFeeName())){
							param.clear();
							param.put("orderId", order.getId());
							List<OrderRoutePoint> routPointList = orderRoutePointMapper.findRoutePointList(param);
							if(routPointList!=null && routPointList.size()>0){
								for(int i=0;i<routPointList.size();i++){
									OrderRoutePoint routePoint = routPointList.get(i);
									if(routePoint.getOverTimeFeeStatus()==Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_CALED){
										routePoint.setOverTimeFeeStatus(Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_FEED);
										orderRoutePointMapper.updateByPrimaryKeySelective(routePoint);
									}else if(routePoint.getOverTimeFeeStatus()==Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_NO_CAL){
										routePoint.setOverTimeFeeStatus(Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_FEED);
										orderRoutePointMapper.updateByPrimaryKeySelective(routePoint);
										break;
									}
								}
							}
						}
					}
				}
			}
			
			
			param.clear();
	        param.put("orderId", order.getId());
	        List<OrderFeeItem> feeItemList = this.orderFeeItemMapper.findOrderFeeItemList(param);
	        if(feeItemList!=null && feeItemList.size()>0){
	        	for(OrderFeeItem item:feeItemList){
	        		if(item.getStatus().intValue()!=1){
	        			noPaidFee +=item.getFeeAmount();
	        		}
	        	}
	        }
			
			if(noPaidFee.floatValue()>0) {
				order.setStatus(Constant.ORDER_STATUS_CONFIRMFEE);
				order.setPayStatus(Constant.ORDER_PAY_STATUS_NOPAID);
				orderInfoMapper.updateById(order);
			}else {
				order.setStatus(Constant.ORDER_STATUS_PAYFINISH);
				order.setPayStatus(Constant.ORDER_PAY_STATUS_PAID);
				orderInfoMapper.updateById(order);
			}
			
			//添加费用支付提醒广播
			Map<String,Object> payRemarkMap = new HashMap<String,Object>();
			payRemarkMap.put("orderId", order.getId());
			payRemarkMap.put("payFeeType", 1);
			this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_PAY_REMARK,payRemarkMap);
		}
		result.setMessage("添加成功");
		return result;
	}

	@Override
	public MobileResultVO findOrderFeeItemList(String orderId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> queryMap= new HashMap<String,Object>();
		queryMap.put("orderId", orderId);
		List<OrderFeeItem> feeItemList = orderFeeItemMapper.findOrderFeeItemList(queryMap);
		List<OrderFeeItem> paidItemList = new ArrayList<OrderFeeItem>();
		List<OrderFeeItem> noPayItemList = new ArrayList<OrderFeeItem>();
		float noPayTotalMoney = 0f;
		String noPayItemIds = "";
		if(feeItemList!=null && feeItemList.size()>0){
			for(OrderFeeItem item:feeItemList){
				if("1".equals(item.getStatus().toString())){
					paidItemList.add(item);
				}else{
					noPayTotalMoney += item.getFeeAmount()*100;
					noPayItemIds +=item.getId()+",";
					noPayItemList.add(item);
				}
			}
		}
		if(noPayItemIds.endsWith(",")){
			noPayItemIds = noPayItemIds.substring(0, noPayItemIds.length()-1);
		}
		noPayTotalMoney = noPayTotalMoney/100;
		Map<String,Object> payInfo = new HashMap<String,Object>();
		payInfo.put("paidItemList", paidItemList);
		payInfo.put("noPayItemList", noPayItemList);
		payInfo.put("noPayTotalMoney", noPayTotalMoney);
		payInfo.put("noPayItemIds", noPayItemIds);
		result.setData(payInfo);
		result.setMessage("查询成功");
		return result;
	}

	@Override
	public MobileResultVO payFeeItem(String itemIds, String orderId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> queryMap= new HashMap<String,Object>();
		queryMap.put("orderId", orderId);
		List<OrderFeeItem> feeItemList = orderFeeItemMapper.findOrderFeeItemList(queryMap);
		boolean  hasNoPayItem = false;
		for(OrderFeeItem feeItem:feeItemList){
			if(itemIds.contains(","+feeItem.getId()+",")){
				feeItem.setStatus(1);
				feeItem.setPayMethod(2);
				feeItem.setPaySerialNum(UUID.randomUUID().toString());
				feeItem.setPayTime(DateUtilLH.getCurrentTime());
				orderFeeItemMapper.updateByPrimaryKey(feeItem);
			}else if(feeItem.getStatus().intValue()==0){
				hasNoPayItem = true;
			}
		}
		Map<String,Object> orderQueryMap=new HashMap<String,Object>();
		orderQueryMap.put("id", orderId);
		OrderInfo order = orderInfoMapper.findSingleOrder(orderQueryMap);
		if(hasNoPayItem){
			order.setPayStatus(1);//未付清
		}else{
			order.setPayStatus(2);//已付清
		}
		orderInfoMapper.updateById(order);
		result.setMessage("支付成功");
		return result;
	}

	@Override
	public MobileResultVO getWXPrePayInfo(String orderId, String itemIds) throws Exception {
		logger.info("orderId:"+orderId+";itemIds:"+itemIds);
		MobileResultVO result = new MobileResultVO();
		 //订单号
        String orderNo="wx"+orderId+"_"+System.currentTimeMillis();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("idList", itemIds.split(","));
        List<OrderFeeItem> feeItemList = this.orderFeeItemMapper.findOrderFeeItemList(param);
        int payFee=0;
        if(feeItemList!=null && feeItemList.size()>0){
        	for(OrderFeeItem item:feeItemList){
        		item.setPaySerialNum(orderNo);
        		orderFeeItemMapper.updateByPrimaryKey(item);
        		logger.info("item.getFeeAmount():"+item.getFeeAmount());
        		payFee +=item.getFeeAmount()*100;
        	}
        }
        logger.info("payFee:"+payFee);
        String attachInfo = itemIds+"|"+orderId;
        payFee=1;//测试，暂时按1分计算
		WxappPayDto dto = wxPayComponent.prePay(attachInfo, orderNo, payFee, "顺道拉货", "运费支付");
        if(null!=dto){
        	result.setData(dto);
        }
        logger.info("pay order info:"+JsonUtil.convertObjectToJsonStr(result));
		return result;
	}

	@Override
	public MobileResultVO getWXDriverPrePayInfo(String orderId, String itemIds) throws Exception {
		logger.info("orderId:"+orderId+";itemIds:"+itemIds);
		MobileResultVO result = new MobileResultVO();
		 //订单号
        String orderNo="wx"+orderId+"_"+System.currentTimeMillis();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("idList", itemIds.split(","));
        List<OrderFeeItem> feeItemList = this.orderFeeItemMapper.findOrderFeeItemList(param);
        int payFee=0;
        if(feeItemList!=null && feeItemList.size()>0){
        	for(OrderFeeItem item:feeItemList){
        		item.setPaySerialNum(orderNo);
        		orderFeeItemMapper.updateByPrimaryKey(item);
        		logger.info("item.getFeeAmount():"+item.getFeeAmount());
        		payFee +=item.getFeeAmount()*100;
        	}
        }
        logger.info("payFee:"+payFee);
        if(payFee==0){
        	result.setCode(MobileResultVO.CODE_FAIL);
        	result.setMessage("该订单费用项已支付!");
        }else{
            String attachInfo = itemIds+"|"+orderId;
            payFee=1;//测试，暂时按1分计算
    		WxappPayDto dto = wxPayComponent.preDriverPay(attachInfo, orderNo, payFee, "顺道拉货", "运费支付");
            if(null!=dto){
            	result.setData(dto);
            }
            logger.info("pay order info:"+JsonUtil.convertObjectToJsonStr(result));
        }
		return result;
	}
	
	@Override
	public MobileResultVO wxPayNotify(String notifyInfo) {
		logger.info("wxPayNotify start");
		MobileResultVO result= new MobileResultVO();
		Map<String,String> notifyMap = wxPayComponent.parseXmlToMap(notifyInfo);
		logger.info("notifyMap:"+JsonUtil.convertObjectToJsonStr(notifyMap));
		String resultCode = notifyMap.get("result_code");
		String attach = notifyMap.get("attach");
		String totalFee = notifyMap.get("total_fee");
		if("SUCCESS".equals(resultCode)){
			notifyFeeItemPay(attach, totalFee);
		}
		return result;
	}

	private void notifyFeeItemPay(String attach, String totalFee) {
		String[] attachArr = attach.split("\\|");
		String feeItemIds = attachArr[0];
		String orderId = attachArr[1];
		logger.info("feeItemIds:"+feeItemIds);
		logger.info("orderId:"+orderId);
		if(!feeItemIds.startsWith(",")){
			feeItemIds = ","+feeItemIds;
		}
		if(!feeItemIds.endsWith(",")){
			feeItemIds = feeItemIds+",";
		}
		Map<String,Object> queryMap= new HashMap<String,Object>();
		queryMap.put("orderId", orderId);
		queryMap.put("status", 0);//查找未付款项
		List<OrderFeeItem> feeItemList = orderFeeItemMapper.findOrderFeeItemList(queryMap);
		boolean  hasNoPayItem = false;
		int payFee=0;
		if(feeItemList!=null && feeItemList.size()>0){
			for(OrderFeeItem item:feeItemList){
				String itemIdKey = ","+item.getId()+",";
				if(feeItemIds.contains(itemIdKey)){
					item.setPayMethod(2);
					item.setPayTime(DateUtilLH.getCurrentTime());
					item.setStatus(1);
		    		orderFeeItemMapper.updateByPrimaryKey(item);
		    		logger.info("payFee amount:"+item.getFeeAmount());
		    		payFee +=item.getFeeAmount()*100;
		    		logger.info("payFee item:"+payFee);
		    		feeItemIds = feeItemIds.replaceAll(itemIdKey, ",");
				}else{
					hasNoPayItem = true;
				}
			}
		}
		logger.info("payFeeFen:"+payFee);
		logger.info("hasNoPayItem:"+hasNoPayItem);
		logger.info("feeItemIds:"+feeItemIds);
		payFee=1;//暂时不计算金额正误，只做测试
		if(!totalFee.equals(payFee+"")){
			logger.info("金额核对有误："+payFee);
		}else if(!",".equals(feeItemIds)){
			logger.info("有不区配的支付项："+feeItemIds);
		}else {
			logger.info("update order status");
			Map<String,Object> orderQueryMap=new HashMap<String,Object>();
			orderQueryMap.put("id", orderId);
			OrderInfo order = orderInfoMapper.findSingleOrder(orderQueryMap);
			if(hasNoPayItem){
				logger.info("setPayStatus 1");
				order.setPayStatus(Constant.ORDER_PAY_STATUS_NOPAID);//未付清
			}else{
				logger.info("setPayStatus 2");
				order.setPayStatus(Constant.ORDER_PAY_STATUS_PAID);//已付清
				if(Constant.ORDER_STATUS_CONFIRMFEE==order.getStatus().intValue()) {
					order.setStatus(Constant.ORDER_STATUS_PAYFINISH);
				}
				
				//通知司机端订单支付
				Map<String,Object> paymentRemarkMap = new HashMap<String,Object>();
				paymentRemarkMap.put("orderId", order.getId());
				this.aliMQProducer.sendMessage(orderDispatchTopic,Constant.MQ_TAG_PAYMENT_ORDER,paymentRemarkMap);
			}
			orderInfoMapper.updateById(order);
			for(OrderFeeItem item:feeItemList){
				String itemIdKey = ","+item.getId()+",";
				if(feeItemIds.contains(itemIdKey)){
					orderFeeItemMapper.updateByPrimaryKey(item);
				}else{
					hasNoPayItem = true;
				}
			}
		}
	}

	@Override
	public MobileResultVO findOrderOverTimeFee(String orderId) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("orderId", orderId);
		List<OrderRoutePoint> routPointList = orderRoutePointMapper.findRoutePointList(param);
		Float overTimeFee = 0f;
		if(routPointList!=null && routPointList.size()>0){
			for(int i=0;i<routPointList.size();i++){
				OrderRoutePoint routePoint = routPointList.get(i);
				if(routePoint.getOverTimeFeeStatus()!=null){
					if(routePoint.getOverTimeFeeStatus()==Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_CALED){
						overTimeFee+=routePoint.getOverTimeFee();
					}else if(routePoint.getOverTimeFeeStatus()==Constant.ROUTE_POINT_OVER_TIME_FEE_STATUS_NO_CAL){
						if(routePoint.getStatus().intValue()==Constant.ROUTE_POINT_STATUS_ARRIVED){
							Date now = new Date();
							Date arriveTime = DateUtilLH.convertStr2Date(routePoint.getArriveTime(), "yyyy-MM-dd HH:mm:ss");
							long between = (now.getTime() - arriveTime.getTime())/1000;
							long min = between/60;
							if(i<2){
								min = min-45;
							}
							if(min>0){
								double reuslt = Math.ceil(min/15.0);
								overTimeFee = overTimeFee +(int)reuslt*10;
							}
						}
						break;
					}
				}
			}
		}
		result.setData(overTimeFee);
		return result;
	}

	@Override
	public MobileResultVO getAliPrePayInfo(String orderId, String itemIds) throws Exception {
		logger.info("orderId:"+orderId+";itemIds:"+itemIds);
		MobileResultVO result = new MobileResultVO();
		 //订单号
        String orderNo="ali"+orderId+"_"+System.currentTimeMillis();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("idList", itemIds.split(","));
        List<OrderFeeItem> feeItemList = this.orderFeeItemMapper.findOrderFeeItemList(param);
        float payFee=0f;
        if(feeItemList!=null && feeItemList.size()>0){
        	for(OrderFeeItem item:feeItemList){
        		item.setPaySerialNum(orderNo);
        		orderFeeItemMapper.updateByPrimaryKey(item);
        		logger.info("item.getFeeAmount():"+item.getFeeAmount());
        		payFee +=item.getFeeAmount();
        	}
        }
        
        if(payFee==0){
        	result.setCode(MobileResultVO.CODE_FAIL);
        	result.setMessage("该订单费用项已支付!");
        }else{
            String attachInfo = itemIds+"|"+orderId;
            payFee=0.01f;//测试，暂时按1分计算
    		String aliPayInfo = aliPayComponent.generatorAliPayOrderInfo(orderNo,attachInfo, payFee);
            if(StringUtils.isNotEmpty(aliPayInfo)){
            	result.setData(aliPayInfo);
            }else {
            	result.setCode(MobileResultVO.CODE_FAIL);
            	result.setMessage("生成支付信息异常");
            }
            logger.info("pay order info:"+JsonUtil.convertObjectToJsonStr(result));
        }
        logger.info("payFee:"+payFee);

		return result;
	}

	@Override
	public MobileResultVO aliPayNotify(Map notifyInfo) {
		Map<String,String> notifyMap = aliPayComponent.parseAliPayNotify(notifyInfo);
		String attach = notifyMap.get("passback_params");
		String totalFeeStr = notifyMap.get("total_amount");
		if(StringUtils.isNotEmpty(attach)&&StringUtils.isNotEmpty(totalFeeStr)){
			int totalFee = (int)(Float.valueOf(totalFeeStr)*100);
			notifyFeeItemPay(attach, totalFee+"");
		}
		return null;
	}

	@Override
	public MobileResultVO findOrderFeeItemPayInfoList(String orderId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> queryMap= new HashMap<String,Object>();
		queryMap.put("orderId", orderId);
		List<Integer> noFeeTypeList = new ArrayList<Integer>();
		noFeeTypeList.add(1);
		noFeeTypeList.add(2);
		queryMap.put("noFeeTypeList",noFeeTypeList );
		List<OrderFeeItem> feeItemList = orderFeeItemMapper.findOrderFeeItemList(queryMap);
		if(feeItemList!=null && feeItemList.size()>0) {
			boolean hasOverTimeFee = false;
			for(OrderFeeItem feeItem:feeItemList) {
				if("等候逾时费".equals(feeItem.getFeeName())) {
					hasOverTimeFee = true;
					break;
				}
			}
			if(!hasOverTimeFee) {
				MobileResultVO overTimeFee = findOrderOverTimeFee(orderId);
				OrderFeeItem overTimeFeeItem = new OrderFeeItem();
				overTimeFeeItem.setFeeAmount(Float.valueOf(overTimeFee.getData().toString()));
				overTimeFeeItem.setFeeName("等候逾时费");
				overTimeFeeItem.setFeeType(3);
				feeItemList.add(overTimeFeeItem);
			}
		}
		
		result.setData(feeItemList);
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		return result;
	}

}
