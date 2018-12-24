package com.sdkj.business.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.logging.inner.Logger;
import com.sdkj.business.dao.checkCode.CheckCodeMapper;
import com.sdkj.business.domain.po.CheckCode;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CheckCodeService;
import com.sdkj.business.util.SmsUtil;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class CheckCodeServiceImpl implements CheckCodeService {
	Logger logger = Logger.getLogger(CheckCodeServiceImpl.class);
	
	@Autowired
	private CheckCodeMapper checkCodeMapper;
	@Override
	public MobileResultVO sendCheckCode(String phoneNumber) {
		MobileResultVO result = new MobileResultVO();
		String code = SmsUtil.getRandomNumber(6);
		Map<String,String> param = new HashMap<String,String>();
		param.put("checkCode", code);
		boolean sendResult = SmsUtil.sendSms(phoneNumber, "SMS_152509976", param);
		if(sendResult) {
			result.setCode(MobileResultVO.CODE_SUCCESS);
			CheckCode checkCode = new CheckCode();
			checkCode.setCode(code);
			checkCode.setPhoneNumber(phoneNumber);
			checkCode.setCreateTime(DateUtilLH.getCurrentTime());
			checkCodeMapper.insert(checkCode);
		}else {
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("发送异常");
		}
		return result;
	}

	@Override
	public boolean validCheckCode(String phoneNumber, String code) {
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.MINUTE, -2);
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("phoneNumber", phoneNumber);
		queryMap.put("code", code);
		queryMap.put("validTimeStart", DateUtilLH.convertDate2Str(ca.getTime(), "yyyy-MM-dd HH:mm:ss"));
		List<CheckCode> codeList = checkCodeMapper.findCheckCodeList(queryMap);
		if(codeList!=null && codeList.size()>0) {
			return true;
		}
		return false;
	}

}
