package com.sdkj.business.dao.refereeRecord;

import java.util.Map;

import com.sdkj.business.domain.po.RefereeRecord;

public interface RefereeRecordMapper {
	public int insert(RefereeRecord target);
	public RefereeRecord findLastRefereeRecord(Map<String,Object> param);
}
