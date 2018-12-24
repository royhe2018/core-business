package com.sdkj.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.sysLog.SysLogEntityMapper;
import com.sdkj.business.domain.po.SysLogEntity;
import com.sdkj.business.service.SysLogService;

@Service
@Transactional
public class SysLogServiceImpl implements SysLogService {
	
	@Autowired
	private SysLogEntityMapper sysLogEntityMapper;
	public void saveSysLog(SysLogEntity log) {
		sysLogEntityMapper.insert(log);
	}
}
