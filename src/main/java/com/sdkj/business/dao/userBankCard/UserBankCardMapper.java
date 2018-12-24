package com.sdkj.business.dao.userBankCard;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.UserBankCard;

public interface UserBankCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserBankCard record);
 
    UserBankCard findSingleCard(Map<String,Object> param);
    
    List<UserBankCard> findCardList(Map<String,Object> param);

    int updateById(UserBankCard record);
}