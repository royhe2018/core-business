package com.sdkj.business.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.VehicleTypeInfoService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class VehicleTypeInfoController {
	Logger logger = LoggerFactory.getLogger(VehicleTypeInfoController.class);
	
	@Autowired
	private VehicleTypeInfoService vehicleTypeInfoService;
	
    @RequestMapping(value="/find/vehicle/type/info/list",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询指定城市开通车型列表",optCode="findVehicleTypeInfoList")
   	public MobileResultVO findVehicleTypeInfoList(HttpServletRequest req) {
       	MobileResultVO result = null;
   		try {
   			String cityName = req.getParameter("cityName");
   			Map<String, String> paramMap = (Map<String, String>) req.getAttribute("paramMap");
			if (paramMap != null) {
				cityName = paramMap.get("cityName");
			}
   			Map<String,Object> param = new HashMap<String,Object>();
   			param.put("cityName", cityName);
   			result = vehicleTypeInfoService.findVehicleTypeInfo(param);
   		}catch(Exception e) {
   			logger.error("查询指定城市开通车型列表异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
   	}
}
