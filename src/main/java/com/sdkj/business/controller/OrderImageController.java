package com.sdkj.business.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.OrderImage;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderImageService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.DateUtilLH;

@Controller
public class OrderImageController {
	
	private Logger logger = LoggerFactory.getLogger(OrderImageController.class);
	
	@Autowired
	private OrderImageService orderImageService;
	
	@RequestMapping(value="/upload/order/image",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="上传订单图片",optCode="uploadOrderImage")
   	public MobileResultVO uploadOrderImage(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String imagePath = req.getParameter("imagePath");
   			String imageType = req.getParameter("imageType");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
				imagePath = paramMap.get("imagePath");
				imageType = paramMap.get("imageType");
			}
   			if(StringUtils.isEmpty(orderId) || StringUtils.isEmpty(imagePath) || StringUtils.isEmpty(imageType)) {
   				result = new MobileResultVO();
   	   			result.setCode(MobileResultVO.CODE_FAIL);
   	   			result.setMessage("参数有误!");
   			}else {
   	   			OrderImage target = new OrderImage();
   	   			target.setCreateTime(DateUtilLH.getCurrentTime());
   	   			target.setImagePath(imagePath);
   	   			target.setImageType(Integer.valueOf(imageType));
   	   			target.setOrderId(Long.valueOf(orderId));
   	   			result = orderImageService.addOrderImage(target);
   			}
   		}catch(Exception e) {
   			logger.error("上传订单图片异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
	
	@RequestMapping(value="/find/order/image",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询订单图片",optCode="uploadOrderImage")
   	public MobileResultVO findOrderImage(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				orderId = paramMap.get("orderId");
			}
   			if(StringUtils.isEmpty(orderId)) {
   				result = new MobileResultVO();
   	   			result.setCode(MobileResultVO.CODE_FAIL);
   	   			result.setMessage("参数有误!");
   			}else {
   	   			Map<String,Object> params = new HashMap<String,Object>();
   	   			params.put("orderId", orderId);
   	   			result = orderImageService.findOrderImageInfo(params);
   			}
   		}catch(Exception e) {
   			logger.error("查询订单图片异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
