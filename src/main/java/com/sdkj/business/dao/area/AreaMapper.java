package com.sdkj.business.dao.area;

import java.util.Map;

import com.sdkj.business.domain.po.Area;

public interface AreaMapper {
	public Area findSingleArea(Map<String,Object> param);
}
