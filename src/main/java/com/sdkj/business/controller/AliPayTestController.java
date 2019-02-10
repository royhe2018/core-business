package com.sdkj.business.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.component.aliPay.ALIPayComponent;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class AliPayTestController {
	
	private Logger logger = LoggerFactory.getLogger(AliPayTestController.class);
	
	@Autowired
	private ALIPayComponent aLIPayComponent;
	
	@RequestMapping(value="/generator/ali/pay/info",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="生成支付宝预支付信息",optCode="generatorAliPayInfo")
   	public MobileResultVO generatorAliPayInfo(HttpServletRequest req) {
       	MobileResultVO result = new MobileResultVO();
   		try {
   			String orderId = req.getParameter("req");
   			String orderNo="ali"+orderId+"_"+System.currentTimeMillis();
   			String prePayInfo = aLIPayComponent.generatorAliPayOrderInfo(orderNo,"", 0.01f);
   			result.setData(prePayInfo);
   		}catch(Exception e) {
   			logger.error("查询用户余额异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
