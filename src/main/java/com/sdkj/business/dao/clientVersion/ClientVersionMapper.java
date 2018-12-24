package com.sdkj.business.dao.clientVersion;

import java.util.Map;

import com.sdkj.business.domain.po.ClientVersion;

public interface ClientVersionMapper {
	public ClientVersion findLastValidClientVersion (Map<String,Object> param);
}
