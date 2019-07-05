package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.orderComplaint.OrderComplaintMapper;
import com.sdkj.business.domain.po.OrderComplaint;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderComplaintService;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class OrderComplaintServiceImpl implements OrderComplaintService{
	
	@Autowired
	private OrderComplaintMapper orderComplaintMapper;
	@Override
	public MobileResultVO submitOrderComplaint(OrderComplaint target) {
		MobileResultVO result = new MobileResultVO();
		target.setComplaintType(1);
		target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		orderComplaintMapper.addOrderComplaint(target);
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("保存成功!");
		return result;
	}

	@Override
	public MobileResultVO findOrderComplaintList(String orderId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("orderId", orderId);
		List<OrderComplaint> complaintList = orderComplaintMapper.findOrderComplaintList(param);
		result.setData(complaintList);
		return result;
	}

}
