package com.sdkj.business.dao.serviceStation;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.ServiceStation;

public interface ServiceStationMapper {
	
    List<ServiceStation> findServiceStationList(Map<String,Object> param);
}