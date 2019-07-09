package com.sdkj.business.dao.adConvertRecord;

import java.util.Map;

import com.sdkj.business.domain.po.AdConvertRecord;

public interface AdConvertRecordMapper {
	public int addAdConvertRecord(AdConvertRecord target);
	public AdConvertRecord findSingleAdConvertRecord(Map<String,Object> param);
	public int updateAdConvertRecord(AdConvertRecord target);
}
