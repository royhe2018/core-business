package com.sdkj.business.dao.driverTrace;

import java.util.Map;

import com.sdkj.business.domain.po.DriverTrace;

public interface DriverTraceMapper {
	public DriverTrace findSingleTrace(Map<String,Object> param);
	public int insert(DriverTrace target);
}
