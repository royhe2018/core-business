package com.sdkj.business.dao.user;

import java.util.Map;

import com.sdkj.business.domain.po.User;

public interface UserMapper {
    int deleteById(Long id);

    int insert(User record);

    User findSingleUser(Map<String,Object> param);

    int updateById(User record);
    
    public int addUserBalance(Map<String,Object> param);
    public int deductUserBalance(Map<String,Object> param);

}