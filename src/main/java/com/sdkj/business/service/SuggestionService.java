package com.sdkj.business.service;

import com.sdkj.business.domain.po.Suggestion;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface SuggestionService {
	public MobileResultVO submitSuggestion(Suggestion target);
}
