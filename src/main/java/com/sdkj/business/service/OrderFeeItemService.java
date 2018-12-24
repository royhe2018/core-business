package com.sdkj.business.service;

import java.util.List;

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
	
	public MobileResultVO wxPayNotify(String notifyInfo);
}
