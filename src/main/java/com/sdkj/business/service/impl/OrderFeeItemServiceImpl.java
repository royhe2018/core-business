package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.orderFeeItem.OrderFeeItemMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderFeeItemService;
import com.sdkj.business.service.component.wxPay.WXPayComponent;
import com.sdkj.business.service.component.wxPay.WxappPayDto;
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
	@Override
	public MobileResultVO addFeeItem(List<OrderFeeItem> itemList) {
		MobileResultVO result = new MobileResultVO();
		if(itemList!=null && itemList.size()>0){
			Map<String,Object> orderQueryMap=new HashMap<String,Object>();
			orderQueryMap.put("id", itemList.get(0).getOrderId());
			OrderInfo order = orderInfoMapper.findSingleOrder(orderQueryMap);
			if(order.getPayStatus().intValue()!=0){
				order.setPayStatus(1);
				orderInfoMapper.updateById(order);
			}
			for(OrderFeeItem target:itemList){
				target.setStatus(0);
				orderFeeItemMapper.insert(target);
			}
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
		WxappPayDto dto = wxPayComponent.prePay(attachInfo, orderNo, payFee, "顺道拉货", "运费支付");
        if(null!=dto){
        	result.setData(dto);
        }
        logger.info("pay order info:"+JsonUtil.convertObjectToJsonStr(result));
		return result;
	}

	@Override
	public MobileResultVO wxPayNotify(String notifyInfo) {
		logger.info("wxPayNotify start");
		MobileResultVO result= new MobileResultVO();
		Map<String,String> notifyMap = wxPayComponent.parseXmlToMap(notifyInfo);
		logger.info("notifyMap:"+JsonUtil.convertObjectToJsonStr(notifyMap));
		String resultCode = notifyMap.get("result_code");
		if("SUCCESS".equals(resultCode)){
			String attach = notifyMap.get("attach");
			String totalFee = notifyMap.get("total_fee");
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
					order.setPayStatus(1);//未付清
				}else{
					logger.info("setPayStatus 2");
					order.setPayStatus(2);//已付清
				}
				orderInfoMapper.updateById(order);
			}
		}
		return result;
	}

}
