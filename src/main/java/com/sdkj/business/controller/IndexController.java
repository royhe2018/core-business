package com.sdkj.business.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.IndexService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class IndexController {
	private Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private IndexService indexService;
    @RequestMapping(value="/find/index/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询首页信息",optCode="queryIndexInfo")
	public MobileResultVO sendPhoneRegisterSmsCheckCode(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String userId = req.getParameter("userId");
			String city = req.getParameter("city");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId",userId);
			param.put("city",city);
			Map<String,Object> indexInfo = indexService.findIndexPageInfo(param);
			result.setData(indexInfo);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("首页信息异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    @RequestMapping(value="/find/lease/truck/index/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询租车首页信息",optCode="findLeaseTruckIndexInfo")
	public MobileResultVO findLeaseTruckIndexInfo(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String userId = req.getParameter("userId");
			String city = req.getParameter("city");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId",userId);
			param.put("city",city);
			Map<String,Object> indexInfo = indexService.findLeaseTruckIndexPageInfo(param);
			result.setData(indexInfo);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("首页信息异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
}
