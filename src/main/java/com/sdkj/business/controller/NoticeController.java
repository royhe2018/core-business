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
import com.sdkj.business.service.NoticeService;
import com.sdlh.common.StringUtilLH;

@Controller
public class NoticeController {
	Logger logger = LoggerFactory.getLogger(NoticeController.class);
	
	@Autowired
	private NoticeService noticeService;
	
    @RequestMapping(value="/find/notice/list",method=RequestMethod.POST)
   	@ResponseBody
   	public MobileResultVO findCommentList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String userType = req.getParameter("userType");
   			String cityName = req.getParameter("cityName");
   			String pageNumStr = req.getParameter("pageNum");
   			String pageSizeStr = req.getParameter("pageSize");
   			Map<String,String> paramMap = (Map<String,String>)req.getAttribute("paramMap");
   			if(paramMap!=null){
   				userId = paramMap.get("userId");
   				userType = paramMap.get("userType");
   				cityName = paramMap.get("cityName");
   				pageNumStr = paramMap.get("pageNum");
   				pageSizeStr = paramMap.get("pageSize");
   			}
   			int pageSize =10;
   			int pageNum =1;
   			if(StringUtilLH.isNotEmpty(pageNumStr)){
   				pageNum = Integer.valueOf(pageNumStr);
   			}
   			if(StringUtilLH.isNotEmpty(pageSizeStr)){
   				pageSize = Integer.valueOf(pageSizeStr);
   			}
   			Map<String,Object> param = new HashMap<String,Object>();
   			if(StringUtilLH.isNotEmpty(userId)){
   				param.put("userId", userId);
   			}
   			if(StringUtilLH.isNotEmpty(userType)){
   				param.put("userType", userType);
   			}
   			if(StringUtilLH.isNotEmpty(cityName)){
   				param.put("cityName", cityName);
   			}
   			result = noticeService.findNoticeList(pageNum, pageSize, param);
   		}catch(Exception e) {
   			logger.error("查询公告异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
