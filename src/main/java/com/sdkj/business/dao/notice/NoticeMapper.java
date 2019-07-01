package com.sdkj.business.dao.notice;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.Notice;

public interface NoticeMapper {
	List<Notice> findNoticeList(Map<String,Object> param);
}
