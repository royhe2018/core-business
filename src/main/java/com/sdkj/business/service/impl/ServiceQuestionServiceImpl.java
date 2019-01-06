package com.sdkj.business.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.serviceQuestion.ServiceQuestionMapper;
import com.sdkj.business.domain.po.ServiceQuestion;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.ServiceQuestionService;

@Service
@Transactional
public class ServiceQuestionServiceImpl implements ServiceQuestionService {
	
	@Autowired
	private ServiceQuestionMapper serviceQuestionMapper;
	@Override
	public MobileResultVO findServiceQuestionList(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
		List<ServiceQuestion> questionList = serviceQuestionMapper.findServiceQuestionList(param);
		result.setData(questionList);
		return result;
	}

}
