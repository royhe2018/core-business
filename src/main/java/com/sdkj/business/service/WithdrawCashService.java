package com.sdkj.business.service;

import com.sdkj.business.domain.po.WithdrawCashRecord;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface WithdrawCashService {
	
	public MobileResultVO submitWithdrawCashRecord(WithdrawCashRecord record);
	
	public MobileResultVO findUserWithdrawCashRecord(String userId);
}
