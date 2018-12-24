package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.clientVersion.ClientVersionMapper;
import com.sdkj.business.domain.po.ClientVersion;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.ClientVersionService;
import com.sdkj.business.util.Constant;

@Service
@Transactional
public class ClientVersionServiceImpl implements ClientVersionService {
	
	@Autowired
	private ClientVersionMapper clientVersionMapper;
	@Override
	public MobileResultVO findValidClientVersion(String clientType,String versionNum) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage("未找到更新版本!");
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("clientType", clientType);
		ClientVersion version = clientVersionMapper.findLastValidClientVersion(queryMap);
		if(version!=null && !versionNum.equals(version.getVersion())) {
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setMessage("查询成功!");
			version.setFilePath(Constant.ALI_OSS_ACCESS_PREFIX+version.getFilePath());
			result.setData(version);
		}
		return result;
	}

}
