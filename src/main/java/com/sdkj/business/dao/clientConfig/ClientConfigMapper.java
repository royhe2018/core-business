package com.sdkj.business.dao.clientConfig;

import java.util.List;
import java.util.Map;

import com.sdkj.business.domain.po.ClientConfig;

public interface ClientConfigMapper {
	public List<ClientConfig> findClientConfigList(Map<String,Object> param);
}
