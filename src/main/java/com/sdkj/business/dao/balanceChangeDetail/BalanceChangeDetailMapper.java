package com.sdkj.business.dao.balanceChangeDetail;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.BalanceChangeDetail;

public interface BalanceChangeDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BalanceChangeDetail record);

    BalanceChangeDetail findSingleBalanceChange(Map<String,Object> param);
    
    List<BalanceChangeDetail> findBalanceChangeList(Map<String,Object> param);

    int update(BalanceChangeDetail record);
    
    public Map<String,Object> findUserPerformance(Map<String,Object> param);
    
    public List<Map<String,Object>> findUserPerformanceDetail(Map<String,Object> param);
}