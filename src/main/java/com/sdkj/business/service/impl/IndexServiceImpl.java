package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdkj.business.dao.distributionFee.DistributionFeeMapper;
import com.sdkj.business.dao.serviceStation.ServiceStationMapper;
import com.sdkj.business.dao.vehicleSpecialRequirement.VehicleSpecialRequirementMapper;
import com.sdkj.business.dao.vehicleTypeInfo.VehicleTypeInfoMapper;
import com.sdkj.business.domain.po.DistributionFee;
import com.sdkj.business.domain.po.ServiceStation;
import com.sdkj.business.domain.po.VehicleSpecialRequirement;
import com.sdkj.business.domain.po.VehicleTypeInfo;
import com.sdkj.business.service.IndexService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.StringUtilLH;

@Service
public class IndexServiceImpl implements IndexService{
	@Autowired
	private DistributionFeeMapper distributionFeeMapper;
	@Autowired
	private VehicleSpecialRequirementMapper vehicleSpecialRequirementMapper;
	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;
	
	@Autowired
	private ServiceStationMapper serviceStationMapper;
	@Override
	public Map<String,Object> findIndexPageInfo(Map<String, Object> param) {
		List<DistributionFee> distributionFeeList = distributionFeeMapper.findDistributionFeeList(param);
		Map<String,Object> result = new HashMap<String,Object>();
		List<VehicleTypeInfo> vehicleTypeList = vehicleTypeInfoMapper.findVehicleTypeInfoList(param);
		List<Map<String,Object>> vehicleTypeInfoList = new ArrayList<Map<String,Object>>();
		for(VehicleTypeInfo type:vehicleTypeList){
			Map<String,Object> typeItem = new HashMap<String,Object>();
			if(StringUtilLH.isNotEmpty(type.getImage())){
				type.setImage(Constant.ALI_OSS_ACCESS_PREFIX+type.getImage());
			}
			typeItem.put("typeInfo", type);
			param.put("vehicleTypeId", type.getId());
			List<VehicleSpecialRequirement> specialRequirementList = vehicleSpecialRequirementMapper.findVehicleSpecialRequirementList(param);
			typeItem.put("specialRequirementList", specialRequirementList);
			vehicleTypeInfoList.add(typeItem);
		}
		result.put("vehicleTypeInfoList", vehicleTypeInfoList);
		List<Map<String,Object>> serviceVehicleLevelList = new ArrayList<Map<String,Object>>();
		Map<String,Object> specialTruck = new HashMap<String,Object>();
		specialTruck.put("id", 1);
		specialTruck.put("name", "专车");
		specialTruck.put("desc", "专车专用");
		Map<String,Object> backTrackingTruck = new HashMap<String,Object>();
		backTrackingTruck.put("id", 2);
		backTrackingTruck.put("name", "返程车");
		backTrackingTruck.put("desc", "返程利用，减少空载，价格优惠");
		serviceVehicleLevelList.add(specialTruck);
		serviceVehicleLevelList.add(backTrackingTruck);
		result.put("serviceVehicleLevelList", serviceVehicleLevelList);
		result.put("distributionFeeList", distributionFeeList);
		List<String> remarkList = new ArrayList<String>();
		remarkList.add("需装货");
		remarkList.add("一人跟车");
		remarkList.add("两人跟车");
		result.put("remarkList", remarkList);
		return result;
	}
	@Override
	public Map<String, Object> findLeaseTruckIndexPageInfo(
			Map<String, Object> param) {
		Map<String,Object> result = new HashMap<String,Object>();
		List<VehicleTypeInfo> vehicleTypeList = vehicleTypeInfoMapper.findVehicleTypeInfoList(param);
		List<Map<String,Object>> vehicleTypeInfoList = new ArrayList<Map<String,Object>>();
		for(VehicleTypeInfo type:vehicleTypeList){
			Map<String,Object> typeItem = new HashMap<String,Object>();
			if(StringUtilLH.isNotEmpty(type.getImage())){
				type.setImage(Constant.ALI_OSS_ACCESS_PREFIX+type.getImage());
			}
			typeItem.put("typeInfo", type);
			param.put("vehicleTypeId", type.getId());
			List<VehicleSpecialRequirement> specialRequirementList = vehicleSpecialRequirementMapper.findVehicleSpecialRequirementList(param);
			typeItem.put("specialRequirementList", specialRequirementList);
			vehicleTypeInfoList.add(typeItem);
		}
		result.put("vehicleTypeInfoList", vehicleTypeInfoList);
		
		List<ServiceStation> serviceStationList = serviceStationMapper.findServiceStationList(param);
		if(serviceStationList==null){
			serviceStationList = new ArrayList<ServiceStation>();
		}
		result.put("serviceStationList", serviceStationList);
		return result;
	}
	
}
