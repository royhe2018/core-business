package com.sdkj.business.dao.checkCode;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.CheckCode;

public interface CheckCodeMapper {
	public void insert(CheckCode target);
	
	public List<CheckCode> findCheckCodeList(Map<String,Object> param);
}
