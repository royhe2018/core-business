package com.sdkj.business.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.AdConvertRecord;
import com.sdkj.business.service.AdConvertRecordService;

@Controller
public class AdConvertRecordController {
	private Logger logger = LoggerFactory.getLogger(AdConvertRecordController.class);
	
	@Autowired
	private AdConvertRecordService adConvertRecordService;
	
	@RequestMapping(value="/toutian/ad/click")
	@ResponseBody
   	public String driverToutianAdClick(HttpServletRequest req) {
   		try {
   			String adid = req.getParameter("adid");
   			String idfa = req.getParameter("idfa");
   			String imei = req.getParameter("imei");
   			AdConvertRecord target = new AdConvertRecord();
   			target.setAdid(adid);
   			target.setAdType("toutian");
   			target.setCallBackStatus("0");
   			target.setIdfa(idfa);
   			target.setImei(imei);
   			target.setUserType(2);
   			adConvertRecordService.addAdConvertRecord(target);
   		}catch(Exception e) {
   			logger.error("保存司机端广告点击异常", e);
   		}
   		return "success";
   	}
	
	@RequestMapping(value="/client/toutian/ad/click")
	@ResponseBody
   	public String clientToutianAdClick(HttpServletRequest req) {
   		try {
   			String adid = req.getParameter("adid");
   			String idfa = req.getParameter("idfa");
   			String imei = req.getParameter("imei");
   			AdConvertRecord target = new AdConvertRecord();
   			target.setAdid(adid);
   			target.setAdType("toutian");
   			target.setCallBackStatus("0");
   			target.setIdfa(idfa);
   			target.setImei(imei);
   			target.setUserType(1);
   			adConvertRecordService.addAdConvertRecord(target);
   		}catch(Exception e) {
   			logger.error("保存司机端广告点击异常", e);
   		}
   		return "success";
   	}
	
	@RequestMapping(value="/call/back/test")
	@ResponseBody
   	public String callBack(HttpServletRequest req) {
   		try {
   			String idfa = req.getParameter("idfa");
   			String imei = req.getParameter("imei");
   			adConvertRecordService.callBackAdConvert(idfa, imei);
   		}catch(Exception e) {
   			logger.error("注册回调测试", e);
   		}
   		return "success";
   	}
}
