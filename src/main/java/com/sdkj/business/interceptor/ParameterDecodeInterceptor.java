package com.sdkj.business.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.pagehelper.StringUtil;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.RSAUtil;

public class ParameterDecodeInterceptor extends HandlerInterceptorAdapter {
	private Logger logger = LoggerFactory.getLogger(ParameterDecodeInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestUri = request.getRequestURI();
		String token = request.getParameter("token");
		String param = request.getParameter("param");
		logger.info("token:"+token);
		logger.info("param:"+param);
		logger.info("requestUri:"+requestUri);
		Map<String,String> paramMap =null;
		if(StringUtil.isNotEmpty(param)){
			String decodeParam = RSAUtil.decryptByPrivateKey(param);
			paramMap = JsonUtil.convertStrToMap(decodeParam);
		}
		if(StringUtil.isNotEmpty(token)){
			token =new String(Base64.decodeBase64(token));
			logger.info("decode token:"+token);
			if(StringUtil.isNotEmpty(token)){
				String[] tokenArr = token.split("\\&");
				for(String tokenItem:tokenArr){
					if( tokenItem.indexOf("=")!=-1){
						String[] tokenParamArr=tokenItem.split("=");
						if(tokenParamArr!=null && tokenParamArr.length>1){
							paramMap.put(tokenParamArr[0], tokenParamArr[1]);
						}
					}
				}
			}
		}
		request.setAttribute("paramMap", paramMap);
		return true;
	}
}
