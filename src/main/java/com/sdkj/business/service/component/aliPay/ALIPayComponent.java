package com.sdkj.business.service.component.aliPay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.sdlh.common.JsonUtil;

@Component
public class ALIPayComponent {
	
	private Logger logger = LoggerFactory.getLogger(ALIPayComponent.class);
	
	@Value("${ali.pay.appid}")
	private String aliPayAppId;
	
	@Value("${ali.pay.public.key}")
	private String aliPayPublicKey;
	
	@Value("${app.private.key}")
	private String appPrivateKey;
	
	@Value("${ali.pay.notify.url}")
	private String aliNotifyUrl; // 支付回调地址
	public String generatorAliPayOrderInfo(String outTradeNo,String businessParams,float payMoney) {
		try {
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliPayAppId, appPrivateKey, "json", "UTF-8", aliPayPublicKey, "RSA2");
			//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
			AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
			//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody("订单费用支付");
			model.setSubject("顺道拉货");
			model.setOutTradeNo(outTradeNo);
			model.setTimeoutExpress("30m");
			model.setTotalAmount(payMoney+"");
			model.setProductCode("QUICK_MSECURITY_PAY");
			model.setBusinessParams(businessParams);
			request.setBizModel(model);
			request.setNotifyUrl(aliNotifyUrl);
	        //这里和普通的接口调用不同，使用的是sdkExecute
	        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
	        String result = response.getBody();//就是orderString 可以直接给客户端请求，无需再做处理。
	        logger.info(result);;
	        return result;
		}catch(Exception e) {
			logger.error("生成支付信息异常",e);
		}
		return null;
	}
	
	public Map<String,String> parseAliPayNotify(Map requestParams){
		Map<String,String> parseResult = new HashMap<String,String>();
		try {
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			    String name = (String) iter.next();
			    String[] values = (String[]) requestParams.get(name);
			    String valueStr = "";
			    for (int i = 0; i < values.length; i++) {
			        valueStr = (i == values.length - 1) ? valueStr + values[i]
			                    : valueStr + values[i] + ",";
			  	}
			    //乱码解决，这段代码在出现乱码时使用。
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			    parseResult.put(name, valueStr);
			}
			//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
			//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
			logger.info(JsonUtil.convertObjectToJsonStr(parseResult));
			boolean flag = AlipaySignature.rsaCheckV1(parseResult, aliPayPublicKey, "UTF-8","RSA2");
		}catch(Exception e) {
			logger.error("支付宝回调异常", e);
		}
		return parseResult;
	}
}
