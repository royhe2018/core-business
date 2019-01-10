package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.sdkj.business.dao.comment.CommentMapper;
import com.sdkj.business.dao.orderInfo.OrderInfoMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.Comment;
import com.sdkj.business.domain.po.OrderInfo;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CommentService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
	
	@Autowired
	private CommentMapper commentMapper;
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private UserMapper userMapper;
	@Override
	public MobileResultVO addComment(Comment target) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		int scoreCount = 0;
		float totalScore =0;
		if(target.getVehicleScore() != null) {
			scoreCount +=1;
			totalScore += target.getVehicleScore();
		}
		
		if(target.getMannerScore() != null) {
			scoreCount +=1;
			totalScore += target.getMannerScore();
		}
		if(scoreCount==0) {
			target.setAverageScore(5f);
		}else {
			target.setAverageScore(totalScore/scoreCount);
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", target.getOrderId());
		OrderInfo order  = orderInfoMapper.findSingleOrder(param);
		if(order!=null) {
			target.setDriverId(order.getDriverId());
		}
		commentMapper.insertComment(target);
		param.clear();
		param.put("driverId", target.getDriverId());
		Float aveScore = commentMapper.findDriverAveScore(param);
		param.clear();
		param.put("id", target.getDriverId());
		User driver = userMapper.findSingleUser(param);
		driver.setCommentScore(aveScore);
		userMapper.updateById(driver);
		return result;
	}

	@Override
	public MobileResultVO findCommentList(int pageNum, int pageSize, Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		PageHelper.startPage(pageNum, pageSize, false);
		List<Comment> commentList = commentMapper.findCommentList(param);
		result.setData(commentList);
		return result;
	}

}
