package com.sdkj.business.service;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.sdkj.business.dao.msgQueRecord.MsgQueRecordMapper;
import com.sdkj.business.domain.po.MsgQueRecord;
import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;

@Component
public class AliMQProducer {

	private Logger logger = LoggerFactory.getLogger(AliMQProducer.class);

	@Value("${ali.mq.producerId}")
	private String producerId;
	@Value("${ali.mq.accessKey}")
	private String accessKey;
	@Value("${ali.mq.secretKey}")
	private String secretKey;
	@Value("${ali.mq.sendMsgTimeoutMillis}")
	private String sendMsgTimeoutMillis;
	@Value("${ali.mq.onsaddr}")
	private String onsaddr;

	private Producer producer;
	@Autowired
	private MsgQueRecordMapper msgQueRecordMapper;
	
	@PostConstruct
	public void startProducer() {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.ProducerId, producerId);
		// AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.AccessKey, accessKey);
		// SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, secretKey);
		// 设置发送超时时间，单位毫秒
		properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, sendMsgTimeoutMillis);
		// 设置 TCP 接入域名（此处以公共云生产环境为例）
		properties.put(PropertyKeyConst.ONSAddr, onsaddr);
		producer = ONSFactory.createProducer(properties);
		// 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
		producer.start();
	}

	public int sendMessage(String topic, String tag, Map<String,Object> param) {
		int result = 0;
		try {
			Message message = new Message();
			message.setTopic(topic);
			message.setTag("OrderMessage");
			param.put("messageType", tag);
			String bodyStr = JsonUtil.convertObjectToJsonStr(param);
			message.setBody(bodyStr.getBytes("utf-8"));
			SendResult sendResult = null;
			for (int i = 0; i < 3; i++) {
				try {
					sendResult = producer.send(message);
					logger.info(message.getTopic() + ":" + sendResult.getMessageId());
					result = 1;
					break;
				}catch(Exception e) {
					logger.error("ali mq send exception", e);
				}
			}
			MsgQueRecord record = new MsgQueRecord();
			record.setCreateTime(DateUtilLH.getCurrentTime());
			record.setMessageId(sendResult.getMessageId());
			record.setMessageType(tag);
			record.setParam(bodyStr);
			msgQueRecordMapper.insert(record);
		} catch (Exception e) {
			logger.error("ali mq exception", e);
		}
		return result;
	}

	public void destroy() {
		if (producer != null) {
			producer.shutdown();
		}
	}
}