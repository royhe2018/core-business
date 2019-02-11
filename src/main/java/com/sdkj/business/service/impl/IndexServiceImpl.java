package com.sdkj.business.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.StringUtilLH;

@Service
public class IndexServiceImpl implements IndexService{
	@Autowired
	private DistributionFeeMapper distributionFeeMapper;
	@Autowired
	private VehicleSpecialRequirementMapper vehicleSpecialRequirementMapper;
	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;
	
	@Value("${appointment.hour.list.str}")
	private String hourListStr;
	
	@Value("${appointment.minute.list.str}")
	private String minuteListStr;
 
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
	@Override
	public List<Map<String, Object>> findAppointmentTimeList() {
		List<Map<String,Object>> timeList = new ArrayList<Map<String,Object>>();
		Calendar ca = Calendar.getInstance();
		String[] hourList = hourListStr.split(",");
		String[] minuteList = minuteListStr.split(",");
		int curHour = ca.get(Calendar.HOUR_OF_DAY);
		int curMinute = ca.get(Calendar.MINUTE);
		String curHourStr = curHour+"";
		if(curHourStr.length()<2) {
			curHourStr = "0"+curHourStr;
		}
		String curMinuteStr = curMinute+"";
		if(curMinuteStr.length()<2) {
			curMinuteStr = "0"+curMinuteStr;
		}
		for(int i=0;;i++) {
			ca.add(Calendar.DAY_OF_MONTH, i);
			if(i==0) {
				List<String> todayHourList = new ArrayList<String>();
				List<List<String>> totalMinuteList = new ArrayList<List<String>>();
				for(String hourItem:hourList) {
					if(curHourStr.compareTo(hourItem)<0) {
						todayHourList.add(hourItem);
					}
				}
				if(todayHourList.size()>0) {
					for(int j=0;j<todayHourList.size();) {
						List<String> hourMinuteList = new ArrayList<String>();
						if(j==0) {
							for(String minuteItem:minuteList) {
								if(curMinuteStr.compareTo(minuteItem)<0) {
									hourMinuteList.add(minuteItem);
								}
							}
							if(hourMinuteList.size()>0) {
								totalMinuteList.add(hourMinuteList);
								j++;
							}else {
								todayHourList.remove(j);
							}
						}else {
							totalMinuteList.add(Arrays.asList(minuteList));
							j++;
						}
					}
				}
				if(todayHourList.size()>0 && totalMinuteList.size()>0) {
					Map<String,Object> todayItem = new HashMap<String,Object>();
					todayItem.put("day", "今天");
					todayItem.put("houreList", todayHourList);
					todayItem.put("minList", totalMinuteList);
					timeList.add(todayItem);
				}				
			}else {
				List<String> totalHourList = Arrays.asList(hourList);
				List<List<String>> totalMinuteList = new ArrayList<List<String>>();
				for(int k=0;k<totalHourList.size();k++) {
					totalMinuteList.add(Arrays.asList(minuteList));
				}
				if(totalHourList.size()>0 && totalHourList.size()>0) {
					Map<String,Object> dayItem = new HashMap<String,Object>();
					if(i==1) {
						dayItem.put("day", "明天");
					}else {
						dayItem.put("day", DateUtilLH.convertDate2Str(ca.getTime(), "yyyy-MM-dd"));
					}
					dayItem.put("houreList", totalHourList);
					dayItem.put("minList", totalMinuteList);
					timeList.add(dayItem);
				}
			}
			if(timeList.size()>2) {
				break;
			}
		}
		return timeList;
	}
	
	
	
}
