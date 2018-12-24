package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.utils.StringUtils;
import com.sdkj.business.dao.orderImage.OrderImageMapper;
import com.sdkj.business.domain.po.OrderImage;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderImageService;
import com.sdkj.business.util.Constant;

@Service
@Transactional
public class OrderImageServiceImpl implements OrderImageService {
	Logger logger = LoggerFactory.getLogger(OrderImageServiceImpl.class);
	@Autowired
	private OrderImageMapper orderImageMapper;
	
	@Override
	public MobileResultVO addOrderImage(OrderImage target) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		if(StringUtils.isNotEmpty(target.getImagePath())){
			logger.info("imagePath:"+target.getImagePath());
			String[] imagePathArr= target.getImagePath().split(",");
			if(imagePathArr!=null && imagePathArr.length>0){
				for(String imagePath:imagePathArr){
					OrderImage item = new OrderImage();
					item.setCreateTime(target.getCreateTime());
					item.setImagePath(imagePath);
					item.setImageType(target.getImageType());
					item.setOrderId(target.getOrderId());
					orderImageMapper.insert(item);
				}
			}
		}
		return result;
	}

	@Override
	public MobileResultVO findOrderImageInfo(Map<String, Object> params) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("操作成功");
		params.put("imageType", 1);
		Map<String,Object> imageInfo=new HashMap<String,Object>();
		List<OrderImage> thingsImage = orderImageMapper.findOrderImageList(params);
		if(thingsImage!=null && thingsImage.size()>0) {
			for(OrderImage image:thingsImage) {
				if(StringUtils.isNotEmpty(image.getImagePath())) {
					image.setImagePath(Constant.ALI_OSS_ACCESS_PREFIX+image.getImagePath());
				}
			}
		}
		imageInfo.put("thingsImage", thingsImage);
		params.put("imageType", 2);
		List<OrderImage> coverImage = orderImageMapper.findOrderImageList(params);
		if(coverImage!=null && coverImage.size()>0) {
			for(OrderImage image:coverImage) {
				if(StringUtils.isNotEmpty(image.getImagePath())) {
					image.setImagePath(Constant.ALI_OSS_ACCESS_PREFIX+image.getImagePath());
				}
			}
		}
		imageInfo.put("coverImage", coverImage);
		result.setData(imageInfo);
		return result;
	}

}
