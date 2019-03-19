package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdkj.business.dao.userBankCard.UserBankCardMapper;
import com.sdkj.business.domain.po.UserBankCard;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.CheckCodeService;
import com.sdkj.business.service.UserBackCardService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.AlipayBankNoCheck;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.StringUtilLH;

@Service
@Transactional
public class UserBackCardServiceImpl implements UserBackCardService {
	
	Logger logger = LoggerFactory.getLogger(UserBackCardServiceImpl.class);
	
	@Autowired
	private UserBankCardMapper userBankCardMapper;
	
	@Autowired
	private CheckCodeService checkCodeService;
	@Override
	public MobileResultVO bindUserBackCard(UserBankCard bindCard,String checkCode) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage("绑定失败");
		if(checkCodeService.validCheckCode(bindCard.getReservePhone(), checkCode)){
			String checkResultStr = AlipayBankNoCheck.checkUserBankCardInfo(bindCard.getCardNum(), bindCard.getOwnerCardNum(), bindCard.getOwnerName(), bindCard.getReservePhone());
			logger.info("checkResultStr:"+checkResultStr);
			if(StringUtilLH.isNotEmpty(checkResultStr)){
				JsonNode checkResult = JsonUtil.convertStrToJson(checkResultStr);
				if(checkResult!=null){
					String respCode = checkResult.get("respCode").asText();
					String respMessage = checkResult.get("respMessage").asText();
					if("0000".equals(respCode)){
						String bankName = checkResult.get("bankName").asText();
						String bankType = checkResult.get("bankType").asText();
						bindCard.setBankName(bankName);
						bindCard.setCardType(bankType);
						bindCard.setCreateTime(DateUtilLH.getCurrentTime());
						bindCard.setStatus(1);
						bindCard.setCardBackImg("bg_lan.png");
						userBankCardMapper.insert(bindCard);
						result.setCode(MobileResultVO.CODE_SUCCESS);
						result.setMessage("绑定成功");
					}else{
						result.setMessage(respMessage);
					}
				}
			}
		}else{
			result.setMessage("验证码错误!");
		}
		return result;
	}

	@Override
	public MobileResultVO findUserCardList(String userId) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("userId", userId);
		queryMap.put("status", 1);
		List<UserBankCard> cardList = userBankCardMapper.findCardList(queryMap); 
		if(cardList!=null && cardList.size()>0){
			for(UserBankCard card:cardList){
				String last4Num = card.getCardNum().substring(card.getCardNum().length()-4, card.getCardNum().length());
				card.setLast4gigNum(last4Num);
				card.setCardNum("**** **** **** "+last4Num);
				if(StringUtilLH.isNotEmpty(card.getCardBackImg())){
					card.setCardBackImg(Constant.ALI_OSS_ACCESS_PREFIX+card.getCardBackImg());
				}
			}
		}
		result.setData(cardList);
		result.setMessage("查询成功");
		return result;
	}

	@Override
	public MobileResultVO unBindUserCard(String userId, String cardId) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage("解绑失败");
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("userId", userId);
		queryMap.put("id", cardId);
		UserBankCard card = userBankCardMapper.findSingleCard(queryMap); 
		if(card!=null){
			card.setStatus(2);
			userBankCardMapper.updateById(card);
			result.setCode(MobileResultVO.CODE_SUCCESS);
			result.setMessage("解绑成功");
		}
		return result;
	}

}
