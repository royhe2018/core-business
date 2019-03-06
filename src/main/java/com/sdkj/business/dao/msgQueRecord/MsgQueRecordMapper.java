package com.sdkj.business.dao.msgQueRecord;

import java.util.Map;

import com.sdkj.business.domain.po.MsgQueRecord;

public interface MsgQueRecordMapper {

    int insert(MsgQueRecord record);

    MsgQueRecord findMsgQueRecord(Map<String,Object> param);
    
    int updateByPrimaryKey(MsgQueRecord record);
    
}