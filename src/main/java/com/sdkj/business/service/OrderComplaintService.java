package com.sdkj.business.service;

import com.sdkj.business.domain.po.OrderComplaint;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface OrderComplaintService {
	public MobileResultVO submitOrderComplaint(OrderComplaint target);
	public MobileResultVO findOrderComplaintList(String orderId);
	public MobileResultVO findComplaintTitleList();
}
