package com.sdkj.business.service;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface BalanceChangeDetailService {
	public MobileResultVO findUserBalanceInfo(Integer userId);
	
	public MobileResultVO findUserBalanceChangePageInfo(int pageNum,int pageSize,Integer userId);
	
	public void distributeOrderFeeToUser(Long orderId,List<Long> payItemIdList);
	
    public MobileResultVO findUserPerformance(Map<String,Object> param);
 
}
