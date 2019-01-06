package com.sdkj.business.dao.serviceQuestion;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.ServiceQuestion;

public interface ServiceQuestionMapper {
	
    List<ServiceQuestion> findServiceQuestionList(Map<String,Object> param);
}