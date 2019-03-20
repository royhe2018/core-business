package com.sdkj.business.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.vehicleTypeInfo.VehicleTypeInfoMapper;
import com.sdkj.business.domain.po.VehicleTypeInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.VehicleTypeInfoService;

@Service
@Transactional
public class VehicleTypeInfoServiceImpl implements VehicleTypeInfoService {

	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;
	
	@Override
	public MobileResultVO findVehicleTypeInfo(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		List<VehicleTypeInfo> vehicleTypeList = vehicleTypeInfoMapper.findVehicleTypeInfoList(param);
		result.setData(vehicleTypeList);
		return result;
	}

}
