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

import com.github.pagehelper.StringUtil;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.BalanceChangeDetailService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class BalanceController {
	
	Logger logger = LoggerFactory.getLogger(BalanceController.class);
	
	@Autowired
	private BalanceChangeDetailService balanceChangeDetailService;
	
	
	@RequestMapping(value="/find/user/balance",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询用户当前余额",optCode="queryUserBalance")
   	public MobileResultVO findUserBalance(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   			}
   			result = balanceChangeDetailService.findUserBalanceInfo(Integer.valueOf(userId));
   		}catch(Exception e) {
   			logger.error("查询用户余额异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
	@RequestMapping(value="/find/balance/change/list",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询余额变更明细",optCode="queryBalanceChangeDetail")
   	public MobileResultVO findBalanceChangeList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String pageNumStr = req.getParameter("pageNum");
   			String pageSizeStr = req.getParameter("pageSize");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   				pageNumStr = paramMap.get("pageNum");
   				pageSizeStr = paramMap.get("pageSize");
   			}
   			int pageNum =1;
   			int pageSize = 10;
   			if(StringUtil.isNotEmpty(pageNumStr)){
   				pageNum = Integer.valueOf(pageNumStr);
   			}
   			if(StringUtil.isNotEmpty(pageSizeStr)){
   				pageSize = Integer.valueOf(pageSizeStr);
   			}
   			result = balanceChangeDetailService.findUserBalanceChangePageInfo(pageNum, pageSize, Integer.valueOf(userId));
   		}catch(Exception e) {
   			logger.error("查询余额变更明细", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
	@RequestMapping(value="/find/user/performance",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询用户业绩提成",optCode="findUserPerformance")
   	public MobileResultVO findUserPerformance(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String driverId = req.getParameter("driverId");
   			String startTime = req.getParameter("startTime");
   			String endTime = req.getParameter("endTime");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   				driverId = paramMap.get("driverId");
   				startTime = paramMap.get("startTime");
   				endTime = paramMap.get("endTime");
   			}
   			Map<String,Object> param = new HashMap<String,Object>();
   			if(StringUtil.isNotEmpty(userId)){
   				param.put("userId", userId);
   			}else if(StringUtil.isNotEmpty(driverId)) {
   				param.put("userId", driverId);
   			}
   			if(StringUtil.isNotEmpty(startTime)){
   				param.put("startTime", startTime);
   			}
   			if(StringUtil.isNotEmpty(endTime)){
   				param.put("endTime", endTime+" 23:59:59");
   			}
   			result = balanceChangeDetailService.findUserPerformance(param);
   		}catch(Exception e) {
   			logger.error("查询用户业绩提成异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
