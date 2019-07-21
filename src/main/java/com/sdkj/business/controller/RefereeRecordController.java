package com.sdkj.business.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.RefereeRecord;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.RefereeRecordService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class RefereeRecordController {
	private Logger logger = LoggerFactory.getLogger(RefereeRecordController.class);
	
	@Autowired
	private RefereeRecordService refereeRecordService;
	
	@RequestMapping(value = "/add/referee/record", method = RequestMethod.POST)
	@ResponseBody
	@SysLog(description = "添加推荐记录异常", optCode = "addRefereeRecord")
	public MobileResultVO addRefereeRecord(RefereeRecord target,HttpServletRequest req,HttpServletResponse response){
		MobileResultVO result = null;
		try{
			result = refereeRecordService.addRefereeRecord(target);
			String originHeader = req.getHeader("Origin");
			response.setHeader("Access-Control-Allow-Origin", originHeader);
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE"); 
			response.setHeader("Access-Control-Max-Age", "0"); 
			response.setHeader("Access-Control-Allow-Headers", "Authorization,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token"); 
			response.setHeader("Access-Control-Allow-Credentials", "true"); 
			response.setHeader("XDomainRequestAllowed","1");  
		}catch(Exception e){
			logger.error("添加推荐记录异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("添加推荐记录异常");
		}
		return result;
	}
}
