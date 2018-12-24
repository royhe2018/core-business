package com.sdkj.business.dao.driverInfo;

import java.util.Map;

import com.sdkj.business.domain.po.DriverInfo;

public interface DriverInfoMapper {

    int insert(DriverInfo record);

    DriverInfo findSingleDriver(Map<String,Object> param);
    
    DriverInfo findDriverInfoExist(Map<String,Object> param);

    int updateByPrimaryKey(DriverInfo record);
}