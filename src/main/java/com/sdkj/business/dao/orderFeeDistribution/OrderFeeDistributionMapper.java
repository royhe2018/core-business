package com.sdkj.business.dao.orderFeeDistribution;

import com.sdkj.business.domain.po.OrderFeeDistribution;

public interface OrderFeeDistributionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderFeeDistribution record);

    int insertSelective(OrderFeeDistribution record);

    OrderFeeDistribution selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderFeeDistribution record);

    int updateByPrimaryKey(OrderFeeDistribution record);
}