package com.sdkj.business.service.component.optlog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdkj.business.domain.po.SysLogEntity;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.service.SysLogService;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;
 

@Component
@Aspect
public class SysLogAspect {

	Logger logger = LoggerFactory.getLogger(SysLogAspect.class);
	
	@Autowired
	private SysLogService sysLogService;
	
	@Pointcut("execution(* com.sdkj.business.controller.*.*(..))")
	public void pointCut() {
		
	}

	@Around(value = "pointCut() && @annotation(sysLog)", argNames = "joinPoint,sysLog")
	public Object aroundMethod(ProceedingJoinPoint joinPoint, SysLog sysLog) {
		SysLogEntity log = new SysLogEntity();
		Date startTime = new Date();
		Object resutl = null;
		try {
			Object[] args = joinPoint.getArgs();
			Map<String, Object> param = new HashMap<String, Object>();
			String requestHost = null;
			for (Object para : args) {
				if (para != null) {
					if (para instanceof HttpServletRequest) {
						HttpServletRequest req = (HttpServletRequest) para;
						Map<String, String[]> reqMap = req.getParameterMap();
						if (reqMap != null && !reqMap.isEmpty()) {
							for (String key : reqMap.keySet()) {
								String[] values = reqMap.get(key);
								StringBuffer paraValue = new StringBuffer();
								for (String value : values) {
									paraValue.append(value);
								}
								param.put(key, paraValue.toString());
								if("userId".equals(key)){
									log.setUserId(Long.valueOf(paraValue.toString()));
								}
							}
						}
						requestHost =getRemortIP(req);
					}
				}
			}
			log.setClientIp(requestHost);
			log.setOptPara(JsonUtil.convertObjectToJsonStr(param));
			log.setOptDesc(sysLog.description());
			log.setOptCode(sysLog.optCode());
		} catch (Exception e) {
			logger.error("记录日志异常", e);
		}
		try {
			resutl = joinPoint.proceed();
		}catch(Throwable e) {
			throw new RuntimeException(e);
		}
		try {
			Date endTime = new Date();
			log.setReqTime(DateUtilLH.convertDate2Str(startTime, "yyyy-MM-dd HH:mm:ss"));
			log.setResTime(DateUtilLH.convertDate2Str(endTime, "yyyy-MM-dd HH:mm:ss"));
			log.setRetInfo(JsonUtil.convertObjectToJsonStr(resutl));
			log.setUsedTime((int)(endTime.getTime()-startTime.getTime()));
			sysLogService.saveSysLog(log);
		}catch(Exception e) {
			logger.error("添加系统日志异常", e);
		}
		return resutl;
	}
	
	private String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
}
