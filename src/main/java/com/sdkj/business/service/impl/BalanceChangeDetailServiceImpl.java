package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.sdkj.business.dao.balanceChangeDetail.BalanceChangeDetailMapper;
import com.sdkj.business.dao.orderFeeItem.OrderFeeItemMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.BalanceChangeDetail;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.BalanceChangeDetailService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class BalanceChangeDetailServiceImpl implements
		BalanceChangeDetailService {
	
	Logger logger = LoggerFactory.getLogger(BalanceChangeDetailServiceImpl.class);
	
	@Autowired
	private BalanceChangeDetailMapper balanceChangeDetailMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private OrderFeeItemMapper orderFeeItemMapper;
	
	@Override
	public MobileResultVO findUserBalanceInfo(Integer userId) {
		MobileResultVO result = new MobileResultVO();
		result.setMessage("查询成功");
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("id", userId);
		User user = userMapper.findSingleUser(queryMap);
		Map<String,Object> balanceDetail = new HashMap<String,Object>();
		balanceDetail.put("currentBalance", user.getBalance());
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("userId", userId);
		PageHelper.startPage(1, 10, false);
		List<BalanceChangeDetail> changeList = balanceChangeDetailMapper.findBalanceChangeList(param);
		if(changeList==null || changeList.size()==0){
			changeList = new ArrayList<BalanceChangeDetail>();
		}
		balanceDetail.put("balanceChangeList", changeList);
		result.setData(balanceDetail);
		return result;
	}

	@Override
	public MobileResultVO findUserBalanceChangePageInfo(int pageNum,
			int pageSize, Integer userId) {
		MobileResultVO result = new MobileResultVO();
		PageHelper.startPage(pageNum, pageSize, false);
		Map<String,Object> param = new HashMap<String,Object>();
		List<BalanceChangeDetail> changeList = balanceChangeDetailMapper.findBalanceChangeList(param);
		result.setMessage("查询成功");
		result.setData(changeList);
		return result;
	}

	@Override
	public void distributeOrderFeeToUser(Long orderId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("orderId", orderId);
		param.put("status", Constant.FEE_ITEM_PAY_STATUS_PAIED);
		Map<String,Object> orderFeeDistribute = orderFeeItemMapper.findOrderFeeDistribute(param);
		if(orderFeeDistribute!=null) {
			//给司机分款
			if(orderFeeDistribute.containsKey("driverId") && orderFeeDistribute.containsKey("driverFee")) {
				distributeFeeToUserAccount(orderId,orderFeeDistribute,"driverId","driverFee",Constant.BALANCE_CHANGE_TYPE_INCOME);
			}
			//给客户推荐人分款
			if(orderFeeDistribute.containsKey("clientRefereeId") && orderFeeDistribute.containsKey("clientRefereeFee")) {
				distributeFeeToUserAccount(orderId,orderFeeDistribute,"clientRefereeId","clientRefereeFee",Constant.BALANCE_CAHNGE_TYPE_PERFORMANCEDRAWING);
			}
			//给司机推荐人分款
			if(orderFeeDistribute.containsKey("driverRefereeId") && orderFeeDistribute.containsKey("driverRefereeFee")) {
				distributeFeeToUserAccount(orderId,orderFeeDistribute,"driverRefereeId","driverRefereeFee",Constant.BALANCE_CAHNGE_TYPE_PERFORMANCEDRAWING);
			}
			//给平台分款
			if(orderFeeDistribute.containsKey("platFormFee")) {
				distributeFeeToUserAccount(orderId,orderFeeDistribute,"platFormId","platFormFee",Constant.BALANCE_CAHNGE_TYPE_PERFORMANCEDRAWING);
			}
		}
	}

	private void distributeFeeToUserAccount(Long orderId,Map<String, Object> orderFeeDistribute,String userIdKey,String userFeeKey,int changeType) {
		Long driverId = Long.valueOf(orderFeeDistribute.get(userIdKey)+"");
		Float driverFee = Float.valueOf(orderFeeDistribute.get(userFeeKey)+"");
		 Map<String, Object> param = new HashMap<String,Object>();
		param.put("id", driverId);
		User driver = userMapper.findSingleUser(param);
		param.put("amount", driverFee);
		userMapper.addUserBalance(param);
		BalanceChangeDetail changeDetail = new BalanceChangeDetail();
		Float beforeBlance =0f;
		if(driver.getBalance()!=null) {
			beforeBlance = driver.getBalance();
		}
		changeDetail.setBalanceBeforeChange(beforeBlance);
		changeDetail.setBalanceAfterChange(beforeBlance+driverFee);
		changeDetail.setChangeAmount(driverFee);
		changeDetail.setChangeTime(DateUtilLH.getCurrentTime());
		changeDetail.setChangeType(changeType);
		changeDetail.setItemId(orderId);
		changeDetail.setUserId(driverId);
		balanceChangeDetailMapper.insert(changeDetail);
	}

}
