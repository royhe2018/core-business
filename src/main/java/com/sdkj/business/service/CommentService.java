package com.sdkj.business.service;

import java.util.Map;

import com.sdkj.business.domain.po.Comment;
import com.sdkj.business.domain.vo.MobileResultVO;

public interface CommentService {
	public MobileResultVO addComment(Comment target);
	public MobileResultVO findCommentList(int pageNum,int pageSize,Map<String,Object> param);
}
