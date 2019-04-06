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
import com.sdkj.business.service.ServiceQuestionService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class ServiceQuestionController {
	
	Logger logger = LoggerFactory.getLogger(ServiceQuestionController.class);
	
	@Autowired
	private ServiceQuestionService serviceQuestionService;
	
    @RequestMapping(value="/find/service/question",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询常见问题列表",optCode="findBankCardList")
   	public MobileResultVO findServiceQuestion(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String type = req.getParameter("type");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				type = paramMap.get("type");
			}
   			Map<String,Object> param = new HashMap<String,Object>();
   			if(StringUtil.isNotEmpty(type)){
   				param.put("type", type);
   			}
   			result = serviceQuestionService.findServiceQuestionList(param);
   		}catch(Exception e) {
   			logger.error("查询常见问题列表异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
