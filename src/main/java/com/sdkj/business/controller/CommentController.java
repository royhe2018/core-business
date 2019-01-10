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

import com.aliyuncs.utils.StringUtils;
import com.sdkj.business.domain.po.Comment;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CommentService;
import com.sdlh.common.StringUtilLH;

@Controller
public class CommentController {
	
	Logger logger = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	private CommentService commentService;
	
	@RequestMapping(value="/add/comment",method=RequestMethod.POST)
   	@ResponseBody
   	public MobileResultVO addComment(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String orderId = req.getParameter("orderId");
   			String vehicleScore = req.getParameter("vehicleScore");
   			String mannerScore = req.getParameter("mannerScore");
   			String content = req.getParameter("content");
   			Comment comment = new Comment();
   			comment.setOrderId(Long.valueOf(orderId));
   			comment.setUserId(Long.valueOf(userId));
   			if(StringUtils.isNotEmpty(vehicleScore)) {
   				comment.setVehicleScore(Float.valueOf(vehicleScore));
   			}
   			if(StringUtils.isNotEmpty(mannerScore)) {
   				comment.setMannerScore(Float.valueOf(mannerScore));
   			}
   			comment.setContent(content);
   			result = commentService.addComment(comment);
   		}catch(Exception e) {
   			logger.error("查询评论异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
    @RequestMapping(value="/find/comment/list",method=RequestMethod.POST)
   	@ResponseBody
   	public MobileResultVO findCommentList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String driverId = req.getParameter("driverId");
   			String orderId = req.getParameter("orderId");
   			String pageNumStr = req.getParameter("pageNum");
   			String pageSizeStr = req.getParameter("pageSize");
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
   			if(StringUtilLH.isNotEmpty(orderId)){
   				param.put("orderId", orderId);
   			}
   			if(StringUtilLH.isNotEmpty(driverId)){
   				param.put("driverId", driverId);
   			}
   			result = commentService.findCommentList(pageNum, pageSize, param);
   		}catch(Exception e) {
   			logger.error("查询评论异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
