package com.sdkj.business.dao.distributionSetting;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.DistributionSetting;

public interface DistributionSettingMapper {
	DistributionSetting findSingleDistributionSetting(Map<String,Object> param);
    List<DistributionSetting> findDistributionSettingList(Map<String,Object> param);

}