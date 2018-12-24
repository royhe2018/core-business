package com.sdkj.business.dao.orderFeeItem;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.OrderFeeItem;

public interface OrderFeeItemMapper {

    int insert(OrderFeeItem record);

    List<OrderFeeItem> findOrderFeeItemList(Map<String,Object> param);

    int updateByPrimaryKey(OrderFeeItem record);
}