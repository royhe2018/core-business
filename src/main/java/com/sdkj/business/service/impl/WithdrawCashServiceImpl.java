package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.balanceChangeDetail.BalanceChangeDetailMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.dao.withdrawCashRecord.WithdrawCashRecordMapper;
import com.sdkj.business.domain.po.BalanceChangeDetail;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.po.WithdrawCashRecord;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.WithdrawCashService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class WithdrawCashServiceImpl implements WithdrawCashService {
	
	@Autowired
	private BalanceChangeDetailMapper balanceChangeDetailMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private WithdrawCashRecordMapper withdrawCashRecordMapper;
	@Override
	public MobileResultVO submitWithdrawCashRecord(WithdrawCashRecord record) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		Map<String,Object> qureyMap = new HashMap<String,Object>();
		qureyMap.put("id", record.getUserId());
		User user = userMapper.findSingleUser(qureyMap);
		if(user!=null){
			if(user.getBalance().floatValue()>=record.getCashAmount().floatValue()){
				float oldBalance = user.getBalance().floatValue();
				BalanceChangeDetail changeDetail = new BalanceChangeDetail();
				changeDetail.setBalanceAfterChange(oldBalance-record.getCashAmount().floatValue());
				changeDetail.setBalanceBeforeChange(oldBalance);
				changeDetail.setChangeAmount(record.getCashAmount().floatValue());
				changeDetail.setChangeTime(DateUtilLH.getCurrentTime());
				changeDetail.setChangeType(Constant.BALANCE_CHANGE_TYPE_WITHDRAW);
				changeDetail.setBelongType(Constant.BALANCE_CHANGE_BELONG_TYPE_USER);
				changeDetail.setBelongId(record.getUserId());
				user.setBalance(oldBalance-record.getCashAmount().floatValue());
				userMapper.updateById(user);
				record.setCreateTime(DateUtilLH.getCurrentTime());
				record.setStatus(1);
				withdrawCashRecordMapper.insert(record);
				changeDetail.setItemId(record.getId());
				balanceChangeDetailMapper.insert(changeDetail);
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage("提现成功");
			}else{
				result.setMessage("余额不足");
			}
		}else{
			result.setMessage("用户不存在");
		}
		return result;
	}

	@Override
	public MobileResultVO findUserWithdrawCashRecord(String userId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		List<Map<String,Object>> withdrawCashList = withdrawCashRecordMapper.findWithdrawCashRecord(param);
		result.setData(withdrawCashList);
		result.setMessage("查询成功");
		return result;
	}

}
