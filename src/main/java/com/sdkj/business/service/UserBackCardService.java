package com.sdkj.business.service;

import com.sdkj.business.domain.po.UserBankCard;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface UserBackCardService {
	public MobileResultVO bindUserBackCard(UserBankCard bindCard,String checkCode);
	
	public MobileResultVO findUserCardList(String userId);
	
	public MobileResultVO unBindUserCard(String userId,String cardId);
}
