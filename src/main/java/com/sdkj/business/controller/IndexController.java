package com.sdkj.business.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.sdkj.business.service.IndexService;
import com.sdkj.business.service.component.optlog.SysLog;

@Controller
public class IndexController {
	private Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private IndexService indexService;
    @RequestMapping(value="/find/index/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询首页信息",optCode="queryIndexInfo")
	public MobileResultVO sendPhoneRegisterSmsCheckCode(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String userId = req.getParameter("userId");
			String city = req.getParameter("city");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId",userId);
			param.put("city",city);
			Map<String,Object> indexInfo = indexService.findIndexPageInfo(param);
			result.setData(indexInfo);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("首页信息异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    @RequestMapping(value="/find/lease/truck/index/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询租车首页信息",optCode="findLeaseTruckIndexInfo")
	public MobileResultVO findLeaseTruckIndexInfo(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			String userId = req.getParameter("userId");
			String city = req.getParameter("city");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("userId",userId);
			param.put("city",city);
			Map<String,Object> indexInfo = indexService.findLeaseTruckIndexPageInfo(param);
			result.setData(indexInfo);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("首页信息异常", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    @RequestMapping(value="/find/appointment/timeListTest",method=RequestMethod.POST)
	@ResponseBody
	public MobileResultVO findAppointmentTimeListTest(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			List<Map<String,Object>> appointmentList = new ArrayList<Map<String,Object>>();
			Map<String,Object> todayItem = new HashMap<String,Object>();
			todayItem.put("day", "今天");
			List<String> todayHourList = new ArrayList<String>();
			todayHourList.add("17点");
			todayHourList.add("18点");
			todayHourList.add("19点");
			todayHourList.add("20点");
			todayItem.put("houreList", todayHourList);
			List<String> minuteItemList = new ArrayList<String>();
			minuteItemList.add("45分");
			List<String> minuteItemList1 = new ArrayList<String>();
			minuteItemList1.add("00分");
			minuteItemList1.add("15分");
			minuteItemList1.add("30分");
			minuteItemList1.add("45分");
			List<List<String>> minList = new ArrayList<List<String>>();
			minList.add(minuteItemList);
			minList.add(minuteItemList1);
			minList.add(minuteItemList1);
			minList.add(minuteItemList1);
			todayItem.put("minList", minList);
			appointmentList.add(todayItem);
			
			Map<String,Object> tomoroItem = new HashMap<String,Object>();
			tomoroItem.put("day", "明天");
			List<String> allHourList = new ArrayList<String>();
			allHourList.add("10点");
			allHourList.add("11点");
			allHourList.add("12点");
			allHourList.add("13点");
			allHourList.add("14点");
			allHourList.add("15点");
			allHourList.add("16点");
			allHourList.add("17点");
			allHourList.add("18点");
			allHourList.add("19点");
			allHourList.add("20点");
			tomoroItem.put("houreList", allHourList);
			List<List<String>> allMinList = new ArrayList<List<String>>();
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			allMinList.add(minuteItemList1);
			tomoroItem.put("minList", allMinList);
			appointmentList.add(tomoroItem);
			
			Map<String,Object> afterTomorrowItem = new HashMap<String,Object>();
			afterTomorrowItem.put("day", "2019-02-12");
			afterTomorrowItem.put("houreList", allHourList);
			afterTomorrowItem.put("minList", allMinList);
			appointmentList.add(afterTomorrowItem);
			result.setData(appointmentList);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("获取预约时间", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
    
    @RequestMapping(value="/find/appointment/timeList",method=RequestMethod.POST)
	@ResponseBody
	public MobileResultVO findAppointmentTimeList(HttpServletRequest req) {
    	MobileResultVO result = new MobileResultVO();
		try {
			List<Map<String,Object>> appointmentList = this.indexService.findAppointmentTimeList();
			result.setData(appointmentList);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}catch(Exception e) {
			logger.error("获取预约时间", e);
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
}
