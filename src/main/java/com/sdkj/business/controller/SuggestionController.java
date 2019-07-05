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

import com.sdkj.business.domain.po.Suggestion;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.SuggestionService;

@Controller
public class SuggestionController {
	Logger logger = LoggerFactory.getLogger(SuggestionController.class);
	
	@Autowired
	private SuggestionService suggestionService;
	
    @RequestMapping(value="/add/suggestion",method=RequestMethod.POST)
   	@ResponseBody
   	public MobileResultVO addSuggestion(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String title = req.getParameter("title");
   			String description = req.getParameter("description");
   			String photo1 = req.getParameter("photo1");
   			String photo2 = req.getParameter("photo2");
   			String photo3 = req.getParameter("photo3");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   				title = paramMap.get("title");
   				description = paramMap.get("description");
   				photo1 = paramMap.get("photo1");
   				photo2 = paramMap.get("photo2");
   				photo3 = paramMap.get("photo3");
   			}
   			Suggestion suggestion = new Suggestion(); 
   			suggestion.setDescription(description);
   			suggestion.setUserId(Integer.valueOf(userId));
   			suggestion.setTitle(title);
   			suggestion.setPhoto1(photo1);
   			suggestion.setPhoto2(photo2);
   			suggestion.setPhoto3(photo3);
   			result = suggestionService.submitSuggestion(suggestion);
   		}catch(Exception e) {
   			logger.error("添加建议异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
