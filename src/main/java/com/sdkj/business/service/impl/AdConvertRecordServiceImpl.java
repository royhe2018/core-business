package com.sdkj.business.service.impl;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.openservices.shade.org.apache.commons.codec.digest.Md5Crypt;
import com.sdkj.business.dao.adConvertRecord.AdConvertRecordMapper;
import com.sdkj.business.domain.po.AdConvertRecord;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AdConvertRecordService;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.HttpRequestUtil;

@Service
@Transactional
public class AdConvertRecordServiceImpl implements AdConvertRecordService {
	private Logger logger = LoggerFactory
			.getLogger(AdConvertRecordServiceImpl.class);
	@Autowired
	private AdConvertRecordMapper adConvertRecordMapper;

	@Override
	public MobileResultVO addAdConvertRecord(AdConvertRecord target) {
		if (StringUtils.isNotEmpty(target.getIdfa())) {
			target.setTerminalType(1);
		} else if (StringUtils.isNotEmpty(target.getImei())) {
			target.setTerminalType(2);
		}
		target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		adConvertRecordMapper.addAdConvertRecord(target);
		MobileResultVO result = new MobileResultVO();
		return result;
	}

	@Override
	public MobileResultVO callBackAdConvert(String idfa, String imei) {
		String url = "http://ad.toutiao.com/track/activate/?callback=CALLBACK_URL";
		Map<String, Object> param = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(idfa)) {
			param.put("idfa", idfa);
		} else if (StringUtils.isNotEmpty(imei)) {
			param.put("imei", imei);
		}
		AdConvertRecord record = adConvertRecordMapper
				.findSingleAdConvertRecord(param);
		if (record != null) {
			if (record.getTerminalType().intValue() == 1) {
				url += "&idfa=" + idfa;
			} else if (record.getTerminalType().intValue() == 2) {
				url += "&imei=" + md5(imei);
			}
			url += "&event_type=1&adid=" + record.getAdid();
		}
		logger.info("toutiao call back url:" + url);
		String result = HttpRequestUtil.get(url);
		logger.info("result:" + result);
		return null;
	}

	public String md5(String source) {
		StringBuffer sb = new StringBuffer(32);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes("utf-8"));
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.toUpperCase().substring(1, 3));
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

}
