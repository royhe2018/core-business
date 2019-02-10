package com.sdkj.business.dao.orderInfo;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderInfo;

public interface OrderInfoMapper {
    int deleteById(Long id);
    
    int insert(OrderInfo record);
    
    public OrderInfo findSingleOrder(Map<String,Object> param);

    public List<OrderInfo> findOrderList(Map<String,Object> param);
    
    List<Map<String,Object>> findOrderInfoListDisplay(Map<String,Object> param);
    
    Map<String,Object> findSingleOrderInfoDisplay(Map<String,Object> param);
    
    int updateById(OrderInfo record);
}