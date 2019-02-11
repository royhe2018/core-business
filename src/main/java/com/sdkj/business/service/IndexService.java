package com.sdkj.business.service;

import java.util.List;
import java.util.Map;

public interface IndexService {
	public Map<String,Object> findIndexPageInfo(Map<String,Object> param);
	
	public Map<String,Object> findLeaseTruckIndexPageInfo(Map<String,Object> param);
	
	public List<Map<String,Object>> findAppointmentTimeList();
}
