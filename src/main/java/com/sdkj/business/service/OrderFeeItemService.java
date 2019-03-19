package com.sdkj.business.service;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface OrderFeeItemService {
	/**
	 * 
	 * @param itemList
	 * @return
	 */
	public MobileResultVO addFeeItem(List<OrderFeeItem> itemList);
	/**
	 * 
	 * @return
	 */
	public MobileResultVO findOrderFeeItemList(String orderId);
	
	public MobileResultVO payFeeItem(String itemIds,String orderIds);
	
	public MobileResultVO getWXPrePayInfo(String orderId,String itemIds)  throws Exception;
	
	public MobileResultVO getWXDriverPrePayInfo(String orderId, String itemIds) throws Exception;
	
	public MobileResultVO wxPayNotify(String notifyInfo);
	
	public MobileResultVO getAliPrePayInfo(String orderId,String itemIds)  throws Exception;
	
	public MobileResultVO aliPayNotify(Map notifyInfo);
	
	public MobileResultVO findOrderFeeItemPayInfoList(String orderId);
}
