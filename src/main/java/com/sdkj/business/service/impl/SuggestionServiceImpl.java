package com.sdkj.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdkj.business.dao.suggestion.SuggestionMapper;
import com.sdkj.business.domain.po.Suggestion;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.SuggestionService;
import com.sdlh.common.DateUtilLH;

@Service
@Transactional
public class SuggestionServiceImpl implements SuggestionService {
	
	@Autowired
	private SuggestionMapper suggestionMapper;
	@Override
	public MobileResultVO submitSuggestion(Suggestion target) {
		MobileResultVO result = new MobileResultVO();
		target.setCreateTime(DateUtilLH.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		suggestionMapper.addSuggestion(target);
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("保存成功!");
		return result;
	}

}
