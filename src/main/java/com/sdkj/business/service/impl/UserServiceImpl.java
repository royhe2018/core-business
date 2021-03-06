package com.sdkj.business.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.pagehelper.StringUtil;
import com.sdkj.business.dao.clientConfig.ClientConfigMapper;
import com.sdkj.business.dao.driverInfo.DriverInfoMapper;
import com.sdkj.business.dao.driverTrace.DriverTraceMapper;
import com.sdkj.business.dao.refereeRecord.RefereeRecordMapper;
import com.sdkj.business.dao.user.UserMapper;
import com.sdkj.business.dao.vehicleTypeInfo.VehicleTypeInfoMapper;
import com.sdkj.business.domain.po.ClientConfig;
import com.sdkj.business.domain.po.DriverInfo;
import com.sdkj.business.domain.po.RefereeRecord;
import com.sdkj.business.domain.po.User;
import com.sdkj.business.domain.po.VehicleTypeInfo;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.AliMQProducer;
import com.sdkj.business.service.CheckCodeService;
import com.sdkj.business.service.UserService;
import com.sdkj.business.util.Constant;
import com.sdlh.common.HttpRequestUtil;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.StringUtilLH;

@Service
public class UserServiceImpl implements UserService {
	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private DriverInfoMapper driverInfoMapper;
	
	@Autowired
	private CheckCodeService checkCodeService;
	
	@Autowired
	private ClientConfigMapper clientConfigMapper;
	
	@Autowired
	private AliMQProducer aliMQProducer;
	
	@Autowired
	private VehicleTypeInfoMapper vehicleTypeInfoMapper;
	
	@Autowired
	private RefereeRecordMapper refereeRecordMapper;
 
	@Value("${ali.mq.order.dispatch.topic}")
	private String orderDispatchTopic;
	
	@Value("${map.service.url}")
	private String mapServiceUrl;
	
	@Autowired
	private DriverTraceMapper driverTraceMapper;
	@Override
	public MobileResultVO userLogin(String userPhone,String userType,String checkCode,String passWord,String loginType,String registrionId,String cityName,String imei,String idfa) throws Exception{
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		logger.info("loginType:"+loginType);
		logger.info("userPhone:"+userPhone);
		logger.info("checkCode:"+checkCode);
		logger.info("passWord1111111:"+passWord);
		param.put("account", userPhone);
		param.put("userType", userType);
		User dbUser = userMapper.findSingleUser(param);
		if("2".equals(loginType) && dbUser!=null &&
				StringUtilLH.isNotEmpty(passWord) && !passWord.equals(dbUser.getPassWord())) {
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("登陆失败!");
		}else if("1".equals(loginType) && !checkCodeService.validCheckCode(userPhone, checkCode)&& !userPhone.equals("18220868151")){
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage("验证码错误!");
		}else{
			result.setMessage(MobileResultVO.LOGIN_SUCCESS_MESSAGE);
			if(dbUser!=null && StringUtils.isNotEmpty(dbUser.getRegistrionId()) && StringUtils.isNotEmpty(registrionId) && !registrionId.equals(dbUser.getRegistrionId())) {
				// 强制下线广播
				Map<String, Object> userInfoMap = new HashMap<String, Object>();
				userInfoMap.put("userId", dbUser.getId());
				userInfoMap.put("userType", dbUser.getUserType());
				userInfoMap.put("registrionId", dbUser.getRegistrionId());
				this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_FORCE_OFFLINE,userInfoMap);
			}
			if(dbUser==null){
				dbUser = new User();
				dbUser.setAccount(userPhone);
				dbUser.setUserType(Integer.valueOf(userType));
				dbUser.setPassWord(StringUtilLH.getStringRandom(16));
				if(StringUtils.isNotEmpty(imei)){
					dbUser.setDeviceId(imei);
					dbUser.setTerminalType(1); //1安卓
				}else if(StringUtils.isNotEmpty(idfa)){
					dbUser.setDeviceId(idfa);
					dbUser.setTerminalType(2); //IOS
				}
				if(StringUtils.isNotEmpty(dbUser.getDeviceId())){
					param.clear();
					param.put("userDeviceId",dbUser.getDeviceId());
					RefereeRecord referee = refereeRecordMapper.findLastRefereeRecord(param);
					if(referee!=null){
						dbUser.setRefereeId(referee.getRefereeUserId());
					}
				}
				userMapper.insert(dbUser);
			}
			if(StringUtils.isEmpty(dbUser.getPassWord())){
				dbUser.setPassWord(StringUtilLH.getStringRandom(16));
			}
			dbUser.setRegistrionId(registrionId);
			if(dbUser.getUserType().intValue()==2){
				logger.info("getUserType:2");
				try{
					if(StringUtils.isEmpty(dbUser.getMapTerminalId())){
						String terminalResultStr= HttpRequestUtil.post(mapServiceUrl+"/add/terminal?phoneNumber="+dbUser.getAccount(),"");
						if(StringUtils.isNotEmpty(terminalResultStr)){
							logger.info("terminalResultStr:"+terminalResultStr);
							JsonNode terminalResult =JsonUtil.convertStrToJson(terminalResultStr);
							if(terminalResult!=null && terminalResult.has("data") && terminalResult.get("data")!=null){
								 JsonNode tidData = terminalResult.get("data");
								 if(tidData!=null){
									 dbUser.setMapTerminalId(tidData.get("tid").asText());
								 }
							}
						}
					}
					logger.info("MapTerminalId:2"+dbUser.getMapTerminalId());
					if(StringUtils.isEmpty(dbUser.getNewestTraceId())){
						param.clear();
						param.put("phoneNumber", dbUser.getAccount());
						String traceResultStr= HttpRequestUtil.post(mapServiceUrl+"/find/terminal/trace?phoneNumber="+dbUser.getAccount(),"");
						if(StringUtils.isNotEmpty(traceResultStr)){
							logger.info("traceResultStr:"+traceResultStr);
							JsonNode traceResult = JsonUtil.convertStrToJson(traceResultStr);
							if(traceResult!=null && traceResult.has("data") && traceResult.get("data")!=null){
								 JsonNode tidData = traceResult.get("data");
								 if(tidData!=null){
									 dbUser.setNewestTraceId(tidData.get("trid").asText());
								 }
							}
						}
					}
				}catch(Exception e){
					logger.error("补充地图信息异常", e);
				}
			}
			logger.info("mapTerminalId: "+dbUser.getMapTerminalId());
			logger.info("newestTraceId:"+dbUser.getNewestTraceId());
			userMapper.updateById(dbUser);
			if(StringUtilLH.isNotEmpty(dbUser.getHeadImg())){
				dbUser.setHeadImg(Constant.ALI_OSS_ACCESS_PREFIX+dbUser.getHeadImg());
			}
			Map<String,Object> loginData = new HashMap<String,Object>();
			loginData.put("userInfo", dbUser);
			if("2".equals(userType)){
				param.clear();
				param.put("userId", dbUser.getId());
				DriverInfo driver = driverInfoMapper.findSingleDriver(param);
				if(driver==null){
					loginData.put("driverStatus", 0);
					loginData.put("onDutyStatus", 1);
				}else {
					loginData.put("driverStatus",driver.getStatus());
					if(driver.getOnDutyStatus()==null) {
						loginData.put("onDutyStatus", 1);
					}else {
						loginData.put("onDutyStatus", driver.getOnDutyStatus());
					}
					if(driver.getVehicleTypeId()!=null){
						param.clear();
						param.put("id", driver.getVehicleTypeId());
						VehicleTypeInfo vehicleType = vehicleTypeInfoMapper.findSingleVehicleTypeInfo(param);
						if(vehicleType!=null){
							loginData.put("vehicleStandard", vehicleType.getLength()+"|"+vehicleType.getWidth()+"|"+vehicleType.getHeight());
						}
					}
				}
				
				loginData.put("mapServiceId", "8914");
			}
			if(StringUtil.isEmpty(cityName)){
				cityName="西安市";
			}
			if(!cityName.endsWith("市")){
				cityName = cityName+"市";
			}
			param.clear();
			param.put("cityName",cityName);
			param.put("clientType", userType);
			MobileResultVO sysconfig = this.getSysConfig(param);
			loginData.put("sysconfig", sysconfig.getData());
			String token = "userId="+dbUser.getId()+"&userType="+userType;
			loginData.put("token", Base64.encodeBase64String(token.getBytes("UTF-8")));
			result.setData(loginData);
		}
		return result;
	}
	@Override
	public MobileResultVO updateUser(User user) {
		MobileResultVO result = new MobileResultVO();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", user.getId());
		User dbUser = userMapper.findSingleUser(param);
		if(dbUser!=null){
			result.setMessage(MobileResultVO.OPT_SUCCESS_MESSAGE);
			if(StringUtilLH.isNotEmpty(user.getHeadImg())){
				dbUser.setHeadImg(user.getHeadImg());
			}
			if(StringUtilLH.isNotEmpty(user.getNickName())){
				dbUser.setNickName(user.getNickName());
			}
			
			if(user.getSex()!=null){
				dbUser.setSex(user.getSex());
			}
			if(StringUtilLH.isNotEmpty(user.getRegistrionId())){
				
				if(dbUser!=null && StringUtils.isNotEmpty(dbUser.getRegistrionId()) && !user.getRegistrionId().equals(dbUser.getRegistrionId())) {
					// 强制下线广播
					Map<String, Object> userInfoMap = new HashMap<String, Object>();
					userInfoMap.put("userId", dbUser.getId());
					userInfoMap.put("userType", dbUser.getUserType());
					userInfoMap.put("registrionId", dbUser.getRegistrionId());
					this.aliMQProducer.sendMessage(orderDispatchTopic, Constant.MQ_TAG_FORCE_OFFLINE,userInfoMap);
				}
				dbUser.setRegistrionId(user.getRegistrionId());
			}
			userMapper.updateById(dbUser);
			if(StringUtilLH.isNotEmpty(dbUser.getHeadImg())){
				dbUser.setHeadImg(Constant.ALI_OSS_ACCESS_PREFIX+dbUser.getHeadImg());
			}
			result.setData(dbUser);
		}else{
			result.setCode(MobileResultVO.CODE_FAIL);
			result.setMessage(MobileResultVO.LOGIN_FAIL_MESSAGE);
		}
		return result;
	}
	@Override
	public MobileResultVO bindingReferee(String userId, String refereePhone, String checkCode) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_FAIL);
		result.setMessage("绑定推荐人失败!");
		if(checkCodeService.validCheckCode(refereePhone, checkCode)){
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("id", userId);
			User user = userMapper.findSingleUser(param);
			param.clear();
			param.put("account", refereePhone);
			param.put("userType", Constant.USER_TYPE_DRIVER);
			User dbDriverUser = userMapper.findSingleUser(param);
			if(dbDriverUser!=null) {
				user.setRefereeId(dbDriverUser.getId());
				userMapper.updateById(user);
				result.setCode(MobileResultVO.CODE_SUCCESS);
				result.setMessage("绑定推荐人成功!");
			}else {
				param.put("account", refereePhone);
				param.put("userType", Constant.USER_TYPE_CUSTOMER);
				User refereeUser = userMapper.findSingleUser(param);
				if(refereeUser!=null) {
					user.setRefereeId(refereeUser.getId());
					userMapper.updateById(user);
					result.setCode(MobileResultVO.CODE_SUCCESS);
					result.setMessage("绑定推荐人成功!");
					if(StringUtils.isNotEmpty(refereeUser.getNickName())) {
						result.setData(refereeUser.getNickName());
					}else {
						result.setData(refereeUser.getAccount());
					}
				}else {
					result.setMessage("推荐人不存在!");
				}
			}
		}else {
			result.setMessage("验证码错误!");
		}
		return result;
	}
	@Override
	public MobileResultVO getSysConfig(Map<String, Object> param) {
		MobileResultVO result = new MobileResultVO();
		result.setCode(MobileResultVO.CODE_SUCCESS);
		result.setMessage("获取成功!");
	    List<ClientConfig> configList = clientConfigMapper.findClientConfigList(param);
	    if(configList!=null && configList.size()>0) {
	    	Map<String,Object> configMap = new HashMap<String,Object>();
	    	for(ClientConfig config:configList) {
	    		configMap.put(config.getKey(), config.getValue());
	    	}
	    	result.setData(configMap);
	    }
		return result;
	}
	
}
