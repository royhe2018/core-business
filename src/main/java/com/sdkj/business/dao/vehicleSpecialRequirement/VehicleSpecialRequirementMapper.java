package com.sdkj.business.dao.vehicleSpecialRequirement;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.VehicleSpecialRequirement;

public interface VehicleSpecialRequirementMapper {
    
    VehicleSpecialRequirement findSingleVehicleSpecialRequirement(Map<String,Object> param);
    List<VehicleSpecialRequirement> findVehicleSpecialRequirementList(Map<String,Object> param);
}