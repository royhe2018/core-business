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

import com.sdkj.business.domain.po.UserBankCard;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.UserBackCardService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class UserBankCardController {
	Logger logger = LoggerFactory.getLogger(UserBankCardController.class);
	
	@Autowired
	private UserBackCardService userBackCardService;
	
    @RequestMapping(value="/bind/bank/card",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="绑定银行卡",optCode="bindBankCard")
   	public MobileResultVO bindBankCard(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String ownerName = req.getParameter("ownerName");
   			String cardNum = req.getParameter("cardNum");
   			String ownerCardNum = req.getParameter("ownerCardNum");
   			String reservePhone = req.getParameter("reservePhone");
   			String checkCode = req.getParameter("checkCode");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				ownerName = paramMap.get("ownerName");
				cardNum = paramMap.get("cardNum");
				ownerCardNum = paramMap.get("ownerCardNum");
				reservePhone = paramMap.get("reservePhone");
				checkCode = paramMap.get("checkCode");
			}
   			UserBankCard userCard = new UserBankCard();
   			userCard.setUserId(Long.valueOf(userId));
   			userCard.setOwnerName(ownerName);
   			userCard.setCardNum(cardNum);
   			userCard.setOwnerCardNum(ownerCardNum);
   			userCard.setReservePhone(reservePhone);
   			result = userBackCardService.bindUserBackCard(userCard,checkCode);
   		}catch(Exception e) {
   			logger.error("绑定银行卡", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/find/bank/card/list",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询银行卡列表",optCode="findBankCardList")
   	public MobileResultVO findBankCardList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
			}
   			result = userBackCardService.findUserCardList(userId);
   		}catch(Exception e) {
   			logger.error("查询银行卡列表", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/unbind/bank/card",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="解绑银行卡列表",optCode="unbindBankCard")
   	public MobileResultVO unbindBankCard(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String cardId = req.getParameter("cardId");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				cardId = paramMap.get("cardId");
			}
   			result = userBackCardService.unBindUserCard(userId, cardId);
   		}catch(Exception e) {
   			logger.error("解绑银行卡异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
