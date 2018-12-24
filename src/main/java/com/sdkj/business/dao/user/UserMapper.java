package com.sdkj.business.dao.user;

import java.util.Map;

import com.sdkj.business.domain.po.User;

public interface UserMapper {
    int deleteById(Long id);

    int insert(User record);

    User findSingleUser(Map<String,Object> param);

    int updateById(User record);
}