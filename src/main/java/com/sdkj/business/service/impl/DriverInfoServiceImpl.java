package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.utils.StringUtils;
import com.sdkj.business.dao.comment.CommentMapper;
import com.sdkj.business.dao.driverInfo.DriverInfoMapper;
import com.sdkj.business.domain.po.Comment;
import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.DriverInfoService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.StringUtilLH;

@Service
@Transactional
public class DriverInfoServiceImpl implements DriverInfoService{
	
	Logger logger = LoggerFactory.getLogger(DriverInfoServiceImpl.class);
	
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	
	@Autowired
	private CommentMapper commentMapper;
	@Override
	public MobileResultVO registerDriverInfo(DriverInfo target) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		Map<String,Object> existQuery = new HashMap<String,Object>();
		existQuery.put("carNo", target.getCarNo());
		existQuery.put("idCardNo", target.getIdCardNo());
		existQuery.put("drivingLicenseFileNo", target.getDrivingLicenseFileNo());
		DriverInfo existDriver = driverInfoMapper.findDriverInfoExist(existQuery);
		if(existDriver!=null){
			result.setMessage("资料信息已存在!");
		}else{
			target.setCreateTime(DateUtilLH.getCurrentTime());
			target.setStatus(1);
			target.setIdCardType(1);
			target.setDrivingLicenseNo(target.getIdCardNo());
			driverInfoMapper.insert(target);
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			target.setIdCardImage(Constant.ALI_OSS_ACCESS_PREFIX+target.getIdCardImage());
			target.setIdCardBackImage(Constant.ALI_OSS_ACCESS_PREFIX+target.getIdCardBackImage());
			target.setDrivingLicenseImage(Constant.ALI_OSS_ACCESS_PREFIX+target.getDrivingLicenseImage());
			target.setCarDrivingImage(Constant.ALI_OSS_ACCESS_PREFIX+target.getCarDrivingImage());
			result.setData(target);
		}
		return result;
	}

	@Override
	public MobileResultVO findDriverInfo(Map<String,Object> param) {
		MobileResultVO result = new MobileResultVO();
		DriverInfo driver = driverInfoMapper.findSingleDriver(param);
		if(driver==null){
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("信息不存在!");
		}else{
			driver.setIdCardImage(Constant.ALI_OSS_ACCESS_PREFIX+driver.getIdCardImage());
			driver.setIdCardBackImage(Constant.ALI_OSS_ACCESS_PREFIX+driver.getIdCardBackImage());
			driver.setDrivingLicenseImage(Constant.ALI_OSS_ACCESS_PREFIX+driver.getDrivingLicenseImage());
			driver.setCarDrivingImage(Constant.ALI_OSS_ACCESS_PREFIX+driver.getCarDrivingImage());
			result.setData(driver);
		}
		return result;
	}

	@Override
	public MobileResultVO updateDriverOnDutyStatus(DriverInfo target) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage(MobileResultVO.OPT_FAIL_MESSAGE);
		int updateCount = driverInfoMapper.updateByPrimaryKey(target);
		if(updateCount>0){
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		}
		return result;
	}
	
	@Override
	public MobileResultVO findDriverDetailInfo(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		Map<String,Object> driverBaseInfo = driverInfoMapper.findDriverDetailInfo(param);
		if(driverBaseInfo!=null && driverBaseInfo.containsKey("headImg") && StringUtilLH.isNotEmpty(driverBaseInfo.get("headImg")+"")){
			driverBaseInfo.put("headImg", Constant.ALI_OSS_ACCESS_PREFIX+driverBaseInfo.get("headImg"));
		}
		List<Comment> commentList = commentMapper.findCommentList(param);
		Map<String,Object> driverInfo = new HashMap<String,Object>();
		driverInfo.put("baseInfo",driverBaseInfo);
		driverInfo.put("commentList", commentList);
		result.setData(driverInfo);
		return result;
	}
}
