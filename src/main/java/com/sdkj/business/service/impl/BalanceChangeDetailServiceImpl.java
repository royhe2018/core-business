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
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.BalanceChangeDetail;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.BalanceChangeDetailService;
import com.sdkj.business.service.component.optlog.SysLog;

@Service
@Transactional
public class BalanceChangeDetailServiceImpl implements
		BalanceChangeDetailService {
	
	Logger logger = LoggerFactory.getLogger(BalanceChangeDetailServiceImpl.class);
	
	@Autowired
	private BalanceChangeDetailMapper balanceChangeDetailMapper;
	
	@Autowired
	private UserMapper userMapper;
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

}
