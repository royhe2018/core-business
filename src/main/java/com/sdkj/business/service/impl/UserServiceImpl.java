package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdkj.business.dao.driverInfo.DriverInfoMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CheckCodeService;
import com.sdkj.business.service.UserService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.StringUtilLH;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	
	@Autowired
	private CheckCodeService checkCodeService;
	@Override
	public MobileResultVO userLogin(String userPhone,String userType,String checkCode) {
		MobileResultVO result = new MobileResultVO();
		if(checkCodeService.validCheckCode(userPhone, checkCode)){
			result.setMessage(MobileResultVO.LOGIN_SUCCESS_MESSAGE);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("account", userPhone);
			param.put("userType", userType);
			User dbUser = userMapper.findSingleUser(param);
			if(dbUser==null){
				dbUser = new User();
				dbUser.setAccount(userPhone);
				dbUser.setUserType(Integer.valueOf(userType));
				userMapper.insert(dbUser);
			}else{
				if(StringUtilLH.isNotEmpty(dbUser.getHeadImg())){
					dbUser.setHeadImg(Constant.ALI_OSS_ACCESS_PREFIX+dbUser.getHeadImg());
				}
			}
			Map<String,Object> loginData = new HashMap<String,Object>();
			loginData.put("userInfo", dbUser);
			if("2".equals(userType)){
				param.clear();
				param.put("userId", dbUser.getId());
				DriverInfo driver = driverInfoMapper.findSingleDriver(param);
				if(driver==null){
					loginData.put("driverStatus", 0);
				}else {
					loginData.put("driverStatus",driver.getStatus());
				}
				loginData.put("mapServiceId", "8914");
			}
			result.setData(loginData);
		}else{
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("验证码错误!");
		}
		return result;
	}
	@Override
	public MobileResultVO updateUser(User user) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", user.getId());
		User dbUser = userMapper.findSingleUser(param);
		if(dbUser!=null){
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			if(StringUtilLH.isNotEmpty(user.getHeadImg())){
				dbUser.setHeadImg(user.getHeadImg());
			}
			if(StringUtilLH.isNotEmpty(user.getNickName())){
				dbUser.setNickName(user.getNickName());
			}
			
			if(user.getSex()!=null){
				dbUser.setSex(user.getSex());
			}
			if(StringUtilLH.isNotEmpty(user.getRegistrionId())){
				dbUser.setRegistrionId(user.getRegistrionId());
			}
			userMapper.updateById(dbUser);
			if(StringUtilLH.isNotEmpty(dbUser.getHeadImg())){
				dbUser.setHeadImg(Constant.ALI_OSS_ACCESS_PREFIX+dbUser.getHeadImg());
			}
			result.setData(dbUser);
		}else{
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.LOGIN_FAIL_MESSAGE);
		}
		return result;
	}
	@Override
	public MobileResultVO bindingReferee(String userId, String refereePhone, String checkCode) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage("绑定推荐人失败!");
		if(checkCodeService.validCheckCode(refereePhone, checkCode)){
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("id", userId);
			User user = userMapper.findSingleUser(param);
			param.clear();
			param.put("account", refereePhone);
			param.put("userType", Constant.USER_TYPE_DRIVER);
			User dbDriverUser = userMapper.findSingleUser(param);
			if(dbDriverUser!=null) {
				user.setRefereeId(dbDriverUser.getId());
				userMapper.updateById(user);
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage("绑定推荐人成功!");
			}else {
				param.put("account", refereePhone);
				param.put("userType", Constant.USER_TYPE_CUSTOMER);
				User dbClientUser = userMapper.findSingleUser(param);
				if(dbClientUser!=null) {
					user.setRefereeId(dbClientUser.getId());
					userMapper.updateById(user);
					result.setCode(MobileResultVO.CODE_SUCCESS);
					result.setMessage("绑定推荐人成功!");
				}else {
					result.setMessage("推荐人不存在!");
				}
			}
		}else {
			result.setMessage("验证码错误!");
		}
		return result;
	}
	
}
