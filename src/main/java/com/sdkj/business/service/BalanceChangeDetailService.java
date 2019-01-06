package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.vo.MobileResultVO;

public interface BalanceChangeDetailService {
	public MobileResultVO findUserBalanceInfo(Integer userId);
	
	public MobileResultVO findUserBalanceChangePageInfo(int pageNum,int pageSize,Integer userId);
	
	public void distributeOrderFeeToUser(Long orderId);
	
    public MobileResultVO findUserPerformance(Map<String,Object> param);
 
}
