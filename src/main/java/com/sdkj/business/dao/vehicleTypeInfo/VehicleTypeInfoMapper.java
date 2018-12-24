package com.sdkj.business.dao.vehicleTypeInfo;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.VehicleTypeInfo;

public interface VehicleTypeInfoMapper {
    int insert(VehicleTypeInfo record);
    int deleteByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(VehicleTypeInfo record);
    VehicleTypeInfo findSingleVehicleTypeInfo(Map<String,Object> param);
    List<VehicleTypeInfo> findVehicleTypeInfoList(Map<String,Object> param);
}