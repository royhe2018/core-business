package com.sdkj.business.service.impl;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.openservices.shade.org.apache.commons.codec.digest.DigestUtils;
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
		Map<String, Object> param = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(target.getIdfa())) {
			param.put("idfa", target.getIdfa());
		} else if (StringUtils.isNotEmpty(target.getImei())) {
			param.put("imei", target.getImei());
		}
		AdConvertRecord record = adConvertRecordMapper
				.findSingleAdConvertRecord(param);
		if(record == null){
			target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			adConvertRecordMapper.addAdConvertRecord(target);
		}
		MobileResultVO result = new MobileResultVO();
		return result;
	}

	@Override
	public MobileResultVO callBackAdConvert(String idfa, String imei) {
		String url = "http://ad.toutiao.com/track/activate/?callback=CALLBACK_URL";
		String requestClientUrl = "http://www.shundaolahuo.com/core-business/client/toutian/ad/click";
		String clientSecretKey = "IuFhzcy-WxBfF-lmY-ZBPypVMrPGHJciB";
		String requestDriverUrl = "http://www.shundaolahuo.com/core-business/toutian/ad/click";
		String driverSecretKey = "beOWfXB-DNsHg-kac-oAtfyOBMHshsnds";
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
				requestClientUrl +="&idfa=" + idfa;
				requestDriverUrl +="&idfa=" + idfa;
			} else if (record.getTerminalType().intValue() == 2) {
				url += "&imei=" + md5(imei);
				url += "&muid=" + md5(imei);
				requestClientUrl += "&imei=" + md5(imei);
				requestDriverUrl += "&imei=" + md5(imei);
				requestClientUrl += "&muid=" + md5(imei);
				requestDriverUrl += "&muid=" + md5(imei);
			}
			url += "&event_type=1&adid=" + record.getAdid();
			requestClientUrl += "&event_type=1&adid=" + record.getAdid();
			requestDriverUrl += "&event_type=1&adid=" + record.getAdid();
			
			logger.info("toutiao call back url:" + url);
			String signature = null;
			if(record.getUserType().intValue()==1){
				String sign = DigestUtils.md5Hex(requestClientUrl+clientSecretKey);
				requestClientUrl +="&sign="+sign;
				byte[] inputData = requestClientUrl.getBytes();
				SecretKey secretKey = new SecretKeySpec(clientSecretKey.getBytes(), "HmacMD5");
		        Mac mac;
		        try {
		            mac = Mac.getInstance(secretKey.getAlgorithm());
		            mac.init(secretKey);
		            signature = byteArrayToHexString(mac.doFinal(inputData));
		        } catch (Exception e) {
		            logger.error("HmacMD5算法加密失败",e);
		        }
			}else if(record.getUserType().intValue()==2){
				String sign = DigestUtils.md5Hex(requestDriverUrl+driverSecretKey);
				requestDriverUrl +="&sign="+sign;
				byte[] inputData = requestDriverUrl.getBytes();
				SecretKey secretKey = new SecretKeySpec(driverSecretKey.getBytes(), "HmacMD5");
		        Mac mac;
		        try {
		            mac = Mac.getInstance(secretKey.getAlgorithm());
		            mac.init(secretKey);
		            signature = byteArrayToHexString(mac.doFinal(inputData));
		        } catch (Exception e) {
		            logger.error("HmacMD5算法加密失败",e);
		        }
			}
			logger.info("request url:"+url+"&signature="+signature);
			String result = HttpRequestUtil.get(url+"&signature="+signature);
			logger.info("result:" + result);
			
			record.setCallBackStatus("1");
			adConvertRecordMapper.updateAdConvertRecord(record);
		}
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
	
	private String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }
	
}
