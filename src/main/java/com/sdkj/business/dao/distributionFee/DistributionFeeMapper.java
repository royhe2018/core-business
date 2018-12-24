package com.sdkj.business.dao.distributionFee;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.DistributionFee;

public interface DistributionFeeMapper {
    int insert(DistributionFee record);
    int deleteByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(DistributionFee record);
    DistributionFee findSingleDistributionFee(Map<String,Object> param);
    List<DistributionFee> findDistributionFeeList(Map<String,Object> param);
    

}