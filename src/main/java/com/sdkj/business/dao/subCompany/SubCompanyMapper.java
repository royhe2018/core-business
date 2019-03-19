package com.sdkj.business.dao.subCompany;

import java.util.Map;

import com.sdkj.business.domain.po.SubCompany;

public interface SubCompanyMapper {
	
	public SubCompany findSingleSubCompany(Map<String,Object> param);
	
	public int addCompanyBalance(Map<String,Object> param);
	
	public int deductCompanyBalance(Map<String,Object> param);
}