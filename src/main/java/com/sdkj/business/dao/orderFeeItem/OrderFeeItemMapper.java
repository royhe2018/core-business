package com.sdkj.business.dao.orderFeeItem;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderFeeItem;

public interface OrderFeeItemMapper {

    int insert(OrderFeeItem record);
    
    OrderFeeItem findSingleOrderFeeItem(Map<String,Object> param);
    
    List<OrderFeeItem> findOrderFeeItemList(Map<String,Object> param);

    int updateByPrimaryKey(OrderFeeItem record);
    
    Map<String,Object> findOrderFeeDistribute(Map<String,Object> param);
    
    List<Map<String,Object>> findOrderFeeByPayStatus(Map<String,Object> param);
}