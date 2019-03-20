package com.sdkj.business.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.DriverInfoService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.StringUtilLH;

@Controller
public class DriverInfoController {
	
	Logger logger = LoggerFactory.getLogger(DriverInfoController.class);
	
	@Autowired
	private DriverInfoService driverInfoService;
	
	@RequestMapping(value="/driver/info/register",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="司机资料注册",optCode="driverInfoRegister")
	public MobileResultVO driverInfoRegister(HttpServletRequest req) {
    	MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String idCardNo = req.getParameter("idCardNo");
			String idCardImage = req.getParameter("idCardImage");
			String idCardBackImage = req.getParameter("idCardBackImage");
			String drivingLicenseFileNo = req.getParameter("drivingLicenseFileNo");
			String drivingLicenseImage = req.getParameter("drivingLicenseImage");
			String registerCity = req.getParameter("registerCity");
			String carNo = req.getParameter("carNo");
			String carDrivingImage = req.getParameter("carDrivingImage");
			
			String driverType = req.getParameter("driverType");
			String vehicleTypeId = req.getParameter("vehicleTypeId");
			String driverName = req.getParameter("driverName");
			String carFrontPhoto = req.getParameter("carFrontPhoto");
			String carLateralPhoto = req.getParameter("carLateralPhoto");
			String carRearPhoto = req.getParameter("carRearPhoto");
			DriverInfo driverInfo = new DriverInfo();
			
			if(StringUtilLH.isEmpty(userId) || StringUtilLH.isEmpty(idCardNo) ||StringUtilLH.isEmpty(idCardImage) 
					||StringUtilLH.isEmpty(drivingLicenseFileNo) ||StringUtilLH.isEmpty(drivingLicenseImage) ||StringUtilLH.isEmpty(carNo) ||
					StringUtilLH.isEmpty(carDrivingImage) ||StringUtilLH.isEmpty(idCardBackImage)){
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("信息不完整");
			}else{
				driverInfo.setUserId(Long.valueOf(userId));
				driverInfo.setIdCardNo(idCardNo);
				driverInfo.setIdCardImage(idCardImage);
				driverInfo.setIdCardBackImage(idCardBackImage);
				driverInfo.setDrivingLicenseFileNo(drivingLicenseFileNo);
				driverInfo.setDrivingLicenseImage(drivingLicenseImage);
				driverInfo.setCarNo(carNo);
				driverInfo.setCarDrivingImage(carDrivingImage);
				driverInfo.setRegisterCity(registerCity);
				
				driverInfo.setDriverType(Integer.valueOf(driverType));
				driverInfo.setVehicleTypeId(Long.valueOf(vehicleTypeId));
				driverInfo.setDriverName(driverName);
				driverInfo.setCarFrontPhoto(carFrontPhoto);
				driverInfo.setCarLateralPhoto(carLateralPhoto);
				driverInfo.setCarRearPhoto(carRearPhoto);
				result = this.driverInfoService.registerDriverInfo(driverInfo);
			}
		}catch(Exception e) {
			logger.error("司机信息注册异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
	
	
	
	@RequestMapping(value="/find/driver/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询司机注册资料",optCode="findDriverInfo")
	public MobileResultVO findDriverInfo(HttpServletRequest req) {
    	MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String id = req.getParameter("id");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId", userId);
			param.put("id", id);
			result = driverInfoService.findDriverInfo(param);
		}catch(Exception e) {
			logger.error("司机信息查询异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
	
	@RequestMapping(value="/update/driver/ondutystatus",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="修改司机上下班状态",optCode="updateDriverOnDutyStatus")
	public MobileResultVO updateDriverOnDutyStatus(HttpServletRequest req) {
    	MobileResultVO result = null;
		try {
			String userId = req.getParameter("userId");
			String onDutyStatus = req.getParameter("onDutyStatus");
			if(StringUtilLH.isNotEmpty(userId)&&StringUtilLH.isNotEmpty(onDutyStatus)){
				DriverInfo driverInfo = new DriverInfo();
				driverInfo.setUserId(Long.valueOf(userId));
				driverInfo.setOnDutyStatus(Integer.valueOf(onDutyStatus));
				result = driverInfoService.updateDriverOnDutyStatus(driverInfo);
			}else{
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("缺少必填信息");
			}
		}catch(Exception e) {
			logger.error("司机信息查询异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
	
    @RequestMapping(value="/find/driver/detailInfo",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="获取司机详情",optCode="findDriverDetailInfo")
   	public MobileResultVO findDriverDetailInfo(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String driverId = req.getParameter("driverId");
   			Map<String,Object> param = new HashMap<String,Object>();
   			if(StringUtils.isNotEmpty(driverId)) {
   				param.put("driverId", driverId);
   			}
   			result = driverInfoService.findDriverDetailInfo(param); 
   		}catch(Exception e) {
   			logger.error("获取司机详情异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
