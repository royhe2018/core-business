package com.sdkj.business.dao.orderComplaint;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.ComplaintTitle;
import com.sdkj.business.domain.po.OrderComplaint;

public interface OrderComplaintMapper {
	public int addOrderComplaint(OrderComplaint target);
	public List<OrderComplaint> findOrderComplaintList(Map<String,Object> param);
	public List<ComplaintTitle> findComplaintTitleList();
}
