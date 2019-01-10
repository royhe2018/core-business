package com.sdkj.business.dao.comment;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.Comment;

public interface CommentMapper {
	
	public void insertComment(Comment target);
	
	public List<Comment> findCommentList(Map<String,Object> param);
	
	public Float findDriverAveScore(Map<String,Object> param);
}
