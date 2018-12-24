package com.sdkj.business.dao.withdrawCashRecord;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.WithdrawCashRecord;

public interface WithdrawCashRecordMapper {

    int insert(WithdrawCashRecord record);

    List<Map<String,Object>> findWithdrawCashRecord(Map<String,Object> param);
}