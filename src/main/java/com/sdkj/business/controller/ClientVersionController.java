package com.sdkj.business.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.ClientVersionService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.StringUtilLH;

@Controller
public class ClientVersionController {
	
	private Logger logger = LoggerFactory.getLogger(ClientVersionController.class);
	
	@Autowired
	private ClientVersionService clientVersionService;
	
	@RequestMapping(value="/find/upgrade/info",method=RequestMethod.POST)
	@ResponseBody
	@SysLog(description="查询更新信息",optCode="findUpgradeInfo")
	public MobileResultVO findUpgradeInfo(HttpServletRequest req) {
    	MobileResultVO result = null;
		try {  
			String clientType = req.getParameter("clientType");
			String versionNum = req.getParameter("versionNum");
			if(StringUtilLH.isEmpty(clientType) || StringUtilLH.isEmpty(versionNum)){
				result = new MobileResultVO();
				result.setCode(MobileResultVO.CODE_FAIL);
				result.setMessage("信息不完整");
			}else{
				result = this.clientVersionService.findValidClientVersion(clientType, versionNum);
			}
		}catch(Exception e) {
			logger.error("查询更新信息异常", e);
			result = new MobileResultVO();
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
		}
		return result;
	}
}
