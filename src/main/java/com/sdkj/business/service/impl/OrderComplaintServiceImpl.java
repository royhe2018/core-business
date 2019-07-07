package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.orderComplaint.OrderComplaintMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.domain.po.ComplaintTitle;
import com.sdkj.business.domain.po.OrderComplaint;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderComplaintService;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class OrderComplaintServiceImpl implements OrderComplaintService{
	
	@Autowired
	private OrderComplaintMapper orderComplaintMapper;
	
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Override
	public MobileResultVO submitOrderComplaint(OrderComplaint target) {
		MobileResultVO result = new MobileResultVO();
		target.setComplaintType(1);
		target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		orderComplaintMapper.addOrderComplaint(target);
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", target.getOrderId());
		OrderInfo order  = orderInfoMapper.findSingleOrder(param);
		if(order!=null) {
			order.setComplaintStatus(1);
			orderInfoMapper.updateById(order);
		}
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

	@Override
	public MobileResultVO findComplaintTitleList() {
		MobileResultVO result = new MobileResultVO();
		List<ComplaintTitle> titleList = orderComplaintMapper.findComplaintTitleList();
		result.setData(titleList);
		return result;
	}

}
