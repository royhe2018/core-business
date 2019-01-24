package com.sdkj.business.service.component.wxPay;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sdlh.common.HttpRequestUtil;
import com.sdlh.common.JsonUtil;
import com.sdlh.common.StringUtilLH;

@Component
public class WXPayComponent {

	private Logger logger = LoggerFactory.getLogger(WXPayComponent.class);

	@Value("${wx.pay.appid}")
	private String appID; // 移动应用APPID
	@Value("${wx.pay.driver.appid}")
	private String driverAppID;// 移动应用司机端APPID
	@Value("${wx.pay.unified.order.url}")
	private String unifiedOrderUrl; // 微信统一下单地址
	@Value("${wx.pay.apikey}")
	private String apiKey;// 微信支付账户秘钥
	@Value("${wx.pay.mchid}")
	private String mchId; // 商户号
	@Value("${wx.pay.notify.url}")
	private String notifyUrl; // 支付回调地址
	@Value("${wx.pay.server.internet.ip}")
	private String serverInternetIP;//服务器公网IP

	@Value("${wx.pay.driver.apikey}")
	private String driverApiKey;// 微信支付账户秘钥
	@Value("${wx.pay.driver.mchid}")
	private String driverMchId; // 商户号
	/**
	 * 统一下单
	 * 
	 * @param orderid
	 *            商户端订单id
	 * @param openId
	 *            appid
	 * @param orderNo
	 *            微信订单编号
	 * @param money
	 *            商品金额 （分）
	 * @param describe
	 *            商品描述
	 * @param detail
	 *            商品详情
	 * @return
	 * @throws Exception
	 */
	public WxappPayDto prePay(String attach, String orderNo, int money,
			String describe, String detail) throws Exception {
		WxappPayDto dto = new WxappPayDto();
		try {
			String nonceStr = getNonceStr();
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appID);
			packageParams.put("attach", attach);// 附加数据
			packageParams.put("body", describe);// 商品描述
			packageParams.put("detail", detail);
			packageParams.put("mch_id", mchId);// 商户号
			packageParams.put("nonce_str", nonceStr);// 随机数
			packageParams.put("notify_url", notifyUrl);
			packageParams.put("out_trade_no", orderNo);// 商户订单号
			packageParams.put("spbill_create_ip", serverInternetIP);// 订单生成的机器
			packageParams.put("total_fee", money+"");// 总金额
			packageParams.put("trade_type", "APP");
			String sign = createSign(packageParams, apiKey);
			String xml = "<xml>" + 
							"<appid>" + appID + "</appid>" + 
							"<attach>"+ attach + "</attach>" + 
							"<body><![CDATA[" + describe+ "]]></body>" + 
							"<detail><![CDATA[" + detail+ "]]></detail>" + 
							"<mch_id>" + mchId + "</mch_id>"+ 
							"<nonce_str>" + nonceStr + "</nonce_str>" + 
							"<notify_url>" + notifyUrl+ "</notify_url>" + 
							"<out_trade_no>" + orderNo+ "</out_trade_no>" + 
							"<spbill_create_ip>"+ serverInternetIP + "</spbill_create_ip>" + 
							"<total_fee>"+ money + "</total_fee>" + 
							"<trade_type>APP</trade_type>"+ 
							"<sign>"+ sign + "</sign>" 
					+ "</xml>";
			logger.info("unified order xml:"+xml);
			//xml = new String(xml.toString().getBytes(), "utf-8");
			String prepayId = getPayNo(unifiedOrderUrl, xml);
			if (StringUtilLH.isNotEmpty(prepayId)) {
				SortedMap<String, String> finalpackage = new TreeMap<String, String>();
				String timestamp = getTimeStamp();
				finalpackage.put("appid", appID);
				finalpackage.put("noncestr", nonceStr);
				finalpackage.put("package", "Sign=WXPay");
				finalpackage.put("partnerid", mchId);
				finalpackage.put("prepayid", prepayId);
				//finalpackage.put("signType", "MD5");
				finalpackage.put("timestamp", timestamp);
				String finalsign = createSign(finalpackage, apiKey);

				dto.setNonceStr(nonceStr);
				dto.setPrePayId(prepayId);
				dto.setPaySign(finalsign);
				dto.setSignType("MD5");
				dto.setTimeStamp(timestamp);
				dto.setAppId(appID);
				dto.setPackageStr("Sign=WXPay");
				dto.setPartnerId(mchId);
			}
		} catch (Exception e1) {
			logger.error("统一下单接口异常", e1);
		}
		return dto;
	}

	
	public WxappPayDto preDriverPay(String attach, String orderNo, int money,
			String describe, String detail) throws Exception {
		WxappPayDto dto = new WxappPayDto();
		try {
			String nonceStr = getNonceStr();
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", driverAppID);
			packageParams.put("attach", attach);// 附加数据
			packageParams.put("body", describe);// 商品描述
			packageParams.put("detail", detail);
			packageParams.put("mch_id", driverMchId);// 商户号
			packageParams.put("nonce_str", nonceStr);// 随机数
			packageParams.put("notify_url", notifyUrl);
			packageParams.put("out_trade_no", orderNo);// 商户订单号
			packageParams.put("spbill_create_ip", serverInternetIP);// 订单生成的机器
			packageParams.put("total_fee", money+"");// 总金额
			packageParams.put("trade_type", "APP");
			String sign = createSign(packageParams, driverApiKey);
			String xml = "<xml>" + 
							"<appid>" + driverAppID + "</appid>" + 
							"<attach>"+ attach + "</attach>" + 
							"<body><![CDATA[" + describe+ "]]></body>" + 
							"<detail><![CDATA[" + detail+ "]]></detail>" + 
							"<mch_id>" + driverMchId + "</mch_id>"+ 
							"<nonce_str>" + nonceStr + "</nonce_str>" + 
							"<notify_url>" + notifyUrl+ "</notify_url>" + 
							"<out_trade_no>" + orderNo+ "</out_trade_no>" + 
							"<spbill_create_ip>"+ serverInternetIP + "</spbill_create_ip>" + 
							"<total_fee>"+ money + "</total_fee>" + 
							"<trade_type>APP</trade_type>"+ 
							"<sign>"+ sign + "</sign>" 
					+ "</xml>";
			logger.info("unified order xml:"+xml);
			//xml = new String(xml.toString().getBytes(), "utf-8");
			String prepayId = getPayNo(unifiedOrderUrl, xml);
			if (StringUtilLH.isNotEmpty(prepayId)) {
				SortedMap<String, String> finalpackage = new TreeMap<String, String>();
				String timestamp = getTimeStamp();
				finalpackage.put("appid", driverAppID);
				finalpackage.put("noncestr", nonceStr);
				finalpackage.put("package", "Sign=WXPay");
				finalpackage.put("partnerid", driverMchId);
				finalpackage.put("prepayid", prepayId);
				//finalpackage.put("signType", "MD5");
				finalpackage.put("timestamp", timestamp);
				String finalsign = createSign(finalpackage, driverApiKey);

				dto.setNonceStr(nonceStr);
				dto.setPrePayId(prepayId);
				dto.setPaySign(finalsign);
				dto.setSignType("MD5");
				dto.setTimeStamp(timestamp);
				dto.setAppId(driverAppID);
				dto.setPackageStr("Sign=WXPay");
				dto.setPartnerId(driverMchId);
			}
		} catch (Exception e1) {
			logger.error("统一下单接口异常", e1);
		}
		return dto;
	}
	
	/**
	 * 获取从1970年开始到现在的秒数
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeStamp() {
		long seconds = System.currentTimeMillis() / 1000;
		return String.valueOf(seconds);
	}

	/**
	 * 获得预支付订单号
	 * 
	 * @param url
	 * @param xmlParam
	 * @return
	 */
	public String getPayNo(String url, String xmlParam) {
		String prepay_id = "";
		try {
			String xmlStr = HttpRequestUtil.post(url, xmlParam);
			logger.info("jsonStr=" + xmlStr);
			if (xmlStr.indexOf("FAIL") == -1) {
				Map<String, String> map = parseXmlToMap(xmlStr);
				prepay_id = map.get("prepay_id");
				logger.info("prepay_id:" + prepay_id);
			}
		} catch (Exception e) {
			logger.error("获取预支付订单号异常", e);
		}
		return prepay_id;
	}
   

	/**
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * 
	 * @throws Exception
	 */
	private String createSign(SortedMap<String, String> packageParams,
			String key) throws Exception {
		StringBuffer sb = new StringBuffer();
		Set<Map.Entry<String, String>> es = packageParams.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + key);
		logger.info("md5:" + sb.toString());
		String packageSign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
		logger.info("packge签名:" + packageSign);
		return packageSign;

	}

	// 生成随机号，防重发
	private String getNonceStr() {
		Random random = new Random();
		return DigestUtils.md5Hex(String.valueOf(random.nextInt(10000)));
	}
	
	public Map<String,String> parseXmlToMap(String xmlStr){
		Map<String,String> mapResult = new HashMap<String,String>();
		try{
			Document doc = WXPayXmlUtil.newDocumentBuilder().parse(new ByteArrayInputStream(xmlStr.getBytes()));
			Node root = doc.getFirstChild();
			NodeList childList =root.getChildNodes();
			if(childList!=null && childList.getLength()>0){
				for(int i=0;i<childList.getLength();i++){
					Node child =childList.item(i);
					mapResult.put(child.getNodeName(), child.getTextContent());
				}
			}
		}catch(Exception e){
			logger.error("解析XML异常", e);
		}
		return mapResult;
	}
	
	public static void main(String[] args){
		//String encodeStr=DigestUtils.md5Hex("appid=wxe66e7282f6b72ef4&body=顺道拉货&mch_id=1516258201&nonce_str=e44fea3bec53bcea3b7513ccef5857ac&notify_url=http://api.sdlh.com:8080/wx/pay/notify&out_trade_no=wx3_1539092896925&spbill_create_ip=111.19.32.118&total_fee=3859&trade_type=APP&key=951QAZwsx874edcRFV632tgbyhnUJM36");
		//System.out.println(encodeStr);
		//69EF9BD0180396583A7A20988152DB99
		String xml = "<xml><appid><![CDATA[wxe66e7282f6b72ef4]]></appid><attach><![CDATA[39]]></attach><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[5]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1516258201]]></mch_id><nonce_str><![CDATA[b3f61131b6eceeb2b14835fa648a48ff]]></nonce_str><openid><![CDATA[oenQH55_Q6LRVAZ-USwfhmnwlK4M]]></openid><out_trade_no><![CDATA[wx39_1539401089224]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[32A6F6478B65031345FD25E5B20F66E9]]></sign><time_end><![CDATA[20181013112454]]></time_end><total_fee>5</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4200000193201810137451375211]]></transaction_id></xml>";
		WXPayComponent test = new WXPayComponent();
		Map<String,String> resutl = test.parseXmlToMap(xml);
		System.out.println(JsonUtil.convertObjectToJsonStr(resutl));
		
		float a=0.02f;
		float b=0.03f;
		float c=(a*100+b*100)/100;
		System.out.println(c);
		
		List<Map<String,String>> feeList = new ArrayList<Map<String,String>>();
		Map<String,String> item = new HashMap<String,String>();
		item.put("feeName", "配送费1");
		item.put("feeAmount", "12.6");
		feeList.add(item);
		Map<String,String> item1 = new HashMap<String,String>();
		item1.put("feeName", "配送费");
		item1.put("feeAmount", "10");
		feeList.add(item1);
		System.out.println(JsonUtil.convertObjectToJsonStr(feeList));
	}
}