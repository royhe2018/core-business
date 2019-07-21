package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface UserService {
	public MobileResultVO userLogin(String userPhone,String userType,String checkCode,String passWord,String loginType,String registrionId,String cityName,String imei,String idfa) throws Exception;
	public MobileResultVO updateUser(User user);
	public MobileResultVO bindingReferee(String userId,String refereePhone,String checkCode);
	public MobileResultVO getSysConfig(Map<String,Object> param);
}
