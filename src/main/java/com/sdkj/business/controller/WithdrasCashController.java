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

import com.sdkj.business.domain.po.WithdrawCashRecord;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.WithdrawCashService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class WithdrasCashController {
	
	Logger logger = LoggerFactory.getLogger(WithdrasCashController.class);
	
	@Autowired
	private WithdrawCashService withdrawCashService;
	
	@RequestMapping(value="/submit/withdraw/cash",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="用户提现",optCode="submitWithdrawCash")
   	public MobileResultVO submitWithdrawCash(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String cardId = req.getParameter("cardId");
   			String cashAmount = req.getParameter("cashAmount");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				cardId = paramMap.get("cardId");
				cashAmount = paramMap.get("cashAmount");
			}
   			WithdrawCashRecord withdraw = new WithdrawCashRecord();
   			withdraw.setCardId(Long.valueOf(cardId));
   			withdraw.setCashAmount(Float.valueOf(cashAmount));
   			withdraw.setUserId(Long.valueOf(userId));
   			result = withdrawCashService.submitWithdrawCashRecord(withdraw);
   		}catch(Exception e){
   			logger.error("提现异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
	}
	
	@RequestMapping(value="/withdraw/cash/list",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询提现记录",optCode="submitWithdrawCash")
   	public MobileResultVO findWithdrawCashList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
			}
   			result = withdrawCashService.findUserWithdrawCashRecord(userId);
   		}catch(Exception e){
   			logger.error("查询提现记录异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
	}
}
