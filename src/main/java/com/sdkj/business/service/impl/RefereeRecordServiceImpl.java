package com.sdkj.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.refereeRecord.RefereeRecordMapper;
import com.sdkj.business.domain.po.RefereeRecord;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.RefereeRecordService;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class RefereeRecordServiceImpl implements RefereeRecordService{
	
	@Autowired
	private RefereeRecordMapper refereeRecordMapper;
	
	@Override
	public MobileResultVO addRefereeRecord(RefereeRecord target) {
		target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		refereeRecordMapper.insert(target);
		MobileResultVO result = new MobileResultVO();
		return result;
	}

}
