package com.sdkj.business.service;

import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface UserService {
	public MobileResultVO userLogin(String userPhone,String userType,String checkCode);
	public MobileResultVO updateUser(User user);
	public MobileResultVO bindingReferee(String userId,String refereePhone,String checkCode);
}
