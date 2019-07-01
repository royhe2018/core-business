package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.sdkj.business.dao.area.AreaMapper;
import com.sdkj.business.dao.notice.NoticeMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.domain.po.Area;
import com.sdkj.business.domain.po.Notice;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.NoticeService;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {
	@Autowired
	private NoticeMapper noticeMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private AreaMapper areaMapper;
	@Override
	public MobileResultVO findNoticeList(int pageNum, int pageSize, Map<String, Object> param) {
		String userId = ""+param.get("userId");
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("id", userId);
		User user = userMapper.findSingleUser(queryMap);
		String cityName = user.getRegisterCity();
		queryMap.clear();
		queryMap.put("areaName", cityName);
		Area city = areaMapper.findSingleArea(queryMap);
		if(city!=null && city.getParentId()!=null && city.getParentId().intValue()!=-1){
			param.put("city",city.getAreaName());
			queryMap.clear();
			queryMap.put("areaId", city.getParentId());
			Area province = areaMapper.findSingleArea(queryMap);
			if(province!=null){
				param.put("province",province.getAreaName());
			}
		}
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		PageHelper.startPage(pageNum, pageSize, false);
		List<Notice> noticeList = noticeMapper.findNoticeList(param);
		result.setData(noticeList);
		return result;
	}

}
