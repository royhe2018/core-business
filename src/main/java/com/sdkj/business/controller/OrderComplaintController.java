package com.sdkj.business.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.OrderComplaint;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderComplaintService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class OrderComplaintController {
	Logger logger = LoggerFactory.getLogger(OrderComplaintController.class);
	
	
	@Autowired
	private OrderComplaintService orderComplaintService;
	
    @RequestMapping(value="/add/order/complaint",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description = "订单投诉", optCode = "addOrderComplaint")
   	public MobileResultVO addOrderComplaint(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String orderId = req.getParameter("orderId");
   			String title = req.getParameter("title");
   			String description = req.getParameter("description");
   			String photo1 = req.getParameter("photo1");
   			String photo2 = req.getParameter("photo2");
   			String photo3 = req.getParameter("photo3");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   				orderId = paramMap.get("orderId");
   				title = paramMap.get("title");
   				description = paramMap.get("description");
   				photo1 = paramMap.get("photo1");
   				photo2 = paramMap.get("photo2");
   				photo3 = paramMap.get("photo3");
   			}
   			OrderComplaint complaint = new OrderComplaint(); 
   			complaint.setDescription(description);
   			complaint.setOrderId(Integer.valueOf(orderId));
   			complaint.setUserId(Integer.valueOf(userId));
   			complaint.setTitle(title);
   			complaint.setPhoto1(photo1);
   			complaint.setPhoto2(photo2);
   			complaint.setPhoto3(photo3);
   			result = orderComplaintService.submitOrderComplaint(complaint);
   		}catch(Exception e) {
   			logger.error("添加投诉信息异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/find/order/complaint",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description = "查询订单投诉", optCode = "findOrderComplaint")
   	public MobileResultVO findOrderComplaint(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				orderId = paramMap.get("orderId");
   			}
   			result = orderComplaintService.findOrderComplaintList(orderId);
   		}catch(Exception e) {
   			logger.error("查询信息异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/find/complaint/title",method=RequestMethod.POST)
   	@ResponseBody
   	public MobileResultVO findComplaintTitle(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			result = orderComplaintService.findComplaintTitleList();
   		}catch(Exception e) {
   			logger.error("查询信息异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
