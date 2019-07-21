package com.sdkj.business.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userPhone = paramMap.get("userPhone");
			}
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
   			String passWord = req.getParameter("passWord");
   			String loginType = req.getParameter("loginType");
   			String registrionId = req.getParameter("registrionId");
   			String cityName = req.getParameter("cityName");
   			String imei = req.getParameter("imei");
   			String idfa = req.getParameter("idfa");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userPhone = paramMap.get("userPhone");
				userType = paramMap.get("userType");
				checkCode = paramMap.get("checkCode");
				passWord = paramMap.get("passWord");
				loginType = paramMap.get("loginType");
				registrionId = paramMap.get("registrionId");
				cityName = paramMap.get("cityName");
				imei = paramMap.get("imei");
				idfa = paramMap.get("idfa");
			}
   			result = userService.userLogin(userPhone, userType, checkCode,passWord,loginType,registrionId,cityName,imei,idfa);
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
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				headImg = paramMap.get("headImg");
				nickName = paramMap.get("nickName");
				sex = paramMap.get("sex");
				registrionId = paramMap.get("registrionId");
			}
   			
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
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				refereePhone = paramMap.get("refereePhone");
			}
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
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
				refereePhone = paramMap.get("refereePhone");
				checkCode = paramMap.get("checkCode");
			}
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
   			String cityName = req.getParameter("cityName");
   			String type = req.getParameter("type");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				cityName = paramMap.get("cityName");
				type = paramMap.get("type");
			}
   			Map<String,Object> param = new HashMap<String,Object>();
   			if(StringUtils.isNotEmpty(type)) {
   				param.put("clientType", type);
   			}
   			if(StringUtils.isNotEmpty(cityName)) {
   				param.put("cityName", cityName);
   			}
   			result = this.userService.getSysConfig(param);
   		}catch(Exception e) {
   			logger.error("获取系统配置异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
    
    @RequestMapping(value="/find/user/shareImg",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询用户分享二维码",optCode="findUserShareImg")
   	public MobileResultVO findUserShareImg(HttpServletRequest req) {
       	MobileResultVO result = new MobileResultVO();
   		try {
   			String userId = req.getParameter("userId");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				userId = paramMap.get("userId");
			}
			if(StringUtils.isNotEmpty(userId)){
				result.setData("https://sdlh.oss-cn-qingdao.aliyuncs.com/system/cli_500px.png");
			}else{
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("缺少用户参数");
			}
   			
   		}catch(Exception e) {
   			logger.error("获取系统配置异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
