package com.sdkj.business.dao.leaseTruckOrder;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.LeaseTruckOrder;

public interface LeaseTruckOrderMapper {

    int insert(LeaseTruckOrder record);

    LeaseTruckOrder findSingleLeaseTruckOrder(Map<String,Object> param);

    List<LeaseTruckOrder> findLeaseTruckOrderList(Map<String,Object> param);
    
    int updateByPrimaryKey(LeaseTruckOrder record);
}