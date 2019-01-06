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

import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CheckCodeService;
import com.sdkj.business.service.UserService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.StringUtilLH;

@Controller
public class UserController {
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private CheckCodeService checkCodeService;
	@RequestMapping(value="/send/checkcode",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="发送验证码",optCode="sendCheckCode")
   	public MobileResultVO sendCheckCode(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userPhone = req.getParameter("userPhone");
   			result = checkCodeService.sendCheckCode(userPhone);
   		}catch(Exception e) {
   			logger.error("发送验证码异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
    @RequestMapping(value="/user/login",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="用户登陆",optCode="userLogin")
   	public MobileResultVO userLogin(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userPhone = req.getParameter("userPhone");
   			String userType = req.getParameter("userType");
   			String checkCode = req.getParameter("checkCode");
   			result = userService.userLogin(userPhone, userType, checkCode);
   		}catch(Exception e) {
   			logger.error("用户登陆异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
	
    @RequestMapping(value="/user/update",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="用户信息修改",optCode="userUpate")
   	public MobileResultVO updateUser(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String headImg = req.getParameter("headImg");
   			String nickName = req.getParameter("nickName");
   			String sex = req.getParameter("sex");
   			String registrionId = req.getParameter("registrionId");
   			logger.info("userId:"+userId+";headImg:"+headImg+";sex:"+sex+";registrionId:"+registrionId);
   			User user = new User();
   			user.setId(Long.valueOf(userId));
   			user.setHeadImg(headImg);
   			user.setNickName(nickName);
   			if(StringUtilLH.isNotEmpty(sex)){
   				user.setSex(Integer.valueOf(sex));
   			}
   			user.setRegistrionId(registrionId);
   			result = userService.updateUser(user);
   		}catch(Exception e) {
   			logger.error("修改用户信息异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
	@RequestMapping(value="/send/binding/checkcode",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="发送验证码",optCode="sendCheckCode")
   	public MobileResultVO sendBindingCheckCode(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String refereePhone = req.getParameter("refereePhone");
   			String userId = req.getParameter("userId");
   			result = checkCodeService.sendBindingCheckCode(refereePhone,userId);
   		}catch(Exception e) {
   			logger.error("发送推荐人验证码异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/binding/referee",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="绑定推荐人",optCode="userUpate")
   	public MobileResultVO bindingReferee(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String userId = req.getParameter("userId");
   			String refereePhone = req.getParameter("refereePhone");
   			String checkCode = req.getParameter("checkCode");
   			logger.info("userId:"+userId+";refereePhone:"+refereePhone+";checkCode:"+checkCode);
   			result = userService.bindingReferee(userId,refereePhone,checkCode);
   		}catch(Exception e) {
   			logger.error("绑定推荐人异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/get/sys/config",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="获取系统配置",optCode="getSysConfig")
   	public MobileResultVO getSysConfig(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String type = req.getParameter("type");
   			result = new MobileResultVO();
   			Map<String,Object> config = new HashMap<String,Object>();
   			config.put("serviceTel", "029-8312116"+type);
   			result.setData(config);
   		}catch(Exception e) {
   			logger.error("获取系统配置异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
