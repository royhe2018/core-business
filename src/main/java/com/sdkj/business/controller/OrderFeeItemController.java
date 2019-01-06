package com.sdkj.business.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sdkj.business.domain.po.OrderFeeItem;
import com.sdkj.business.domain.vo.MobileResultVO;
import com.sdkj.business.service.OrderFeeItemService;
import com.sdkj.business.service.component.optlog.SysLog;
import com.sdlh.common.JsonUtil;

@Controller
public class OrderFeeItemController {
	
	Logger logger = LoggerFactory.getLogger(OrderFeeItemController.class);
	
	@Autowired
	private OrderFeeItemService orderFeeItemService;
	
    @RequestMapping(value="/add/order/fee/item",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="添加额外费用",optCode="addOrderFeeItem")
   	public MobileResultVO addOrderFeeItem(HttpServletRequest req) {
    	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String driverId = req.getParameter("driverId");
   			String feeItemListStr = req.getParameter("feeItemList");
   			ArrayNode feeItemList = JsonUtil.convertStrToJsonArray(feeItemListStr);
   			List<OrderFeeItem> feeList = new ArrayList<OrderFeeItem>();
			if(feeItemList!=null && feeItemList.size()>0) {
				for(int i=0;i<feeItemList.size();i++) {
					JsonNode feeNode = feeItemList.get(i);
					float feeAmount = Float.valueOf(feeNode.get("feeAmount").asText());
					if(feeAmount>0){
						OrderFeeItem item = new OrderFeeItem();
						item.setOrderId(Long.valueOf(orderId));
						item.setFeeName(feeNode.get("feeName").asText());
						item.setFeeAmount(Float.valueOf(feeNode.get("feeAmount").asText()));
						feeList.add(item);
					}
				}
			}
			if(feeList.size()>0){
				result = orderFeeItemService.addFeeItem(feeList);
			}else{
				result = new MobileResultVO();
	   			result.setCode(MobileResultVO.CODE_FAIL);
	   			result.setMessage("所有费用项为零");
			}
   		}catch(Exception e) {
   			logger.error("添加额外费用异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
    }
    
    @RequestMapping(value="/find/over/time/fee",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询订单超时费",optCode="findOverTimeFee")
   	public MobileResultVO findOverTimeFee(HttpServletRequest req) {
    	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
			result = orderFeeItemService.findOrderOverTimeFee(orderId);
   		}catch(Exception e) {
   			logger.error("添加额外费用异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
    }
    
    @RequestMapping(value="/find/order/fee/list",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="查询订单费用明细",optCode="findOrderFeeItemList")
   	public MobileResultVO findOrderFeeList(HttpServletRequest req) {
    	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String driverId = req.getParameter("driverId");
   			result = orderFeeItemService.findOrderFeeItemList(orderId);
   		}catch(Exception e) {
   			logger.error("添加额外费用异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
    }
    
    
    @RequestMapping(value="/pay/order/fee/item",method=RequestMethod.POST)
   	@ResponseBody
   	@SysLog(description="添加额外费用",optCode="addOrderFeeItem")
   	public MobileResultVO payOrderFeeItem(HttpServletRequest req) {
    	MobileResultVO result = null;
   		try {
   			String orderId = req.getParameter("orderId");
   			String driverId = req.getParameter("driverId");
   			String feeItemIds = req.getParameter("feeItemIds");
   			feeItemIds = ","+feeItemIds+",";
   			result = orderFeeItemService.payFeeItem(feeItemIds, orderId);
   		}catch(Exception e) {
   			logger.error("添加额外费用异常", e);
   			result = new MobileResultVO();
   			result.setCode(MobileResultVO.CODE_FAIL);
   			result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
   		}
   		return result;
    }
    
    @ResponseBody
    @RequestMapping(value = "/weapp/submit/prepay",method=RequestMethod.POST)
    @SysLog(description="微信统一下单接口",optCode="wxUnifiedOrder")
    public MobileResultVO wxUnifiedOrder(HttpServletRequest req) throws Exception {
    	MobileResultVO result = null;
    	try{
    		String orderId = req.getParameter("orderId");
   			String driverId = req.getParameter("driverId");
   			String feeItemIds = req.getParameter("feeItemIds");
            logger.info("orderId:"+orderId);
            logger.info("feeItemIds:"+feeItemIds);
            result = orderFeeItemService.getWXPrePayInfo(orderId,feeItemIds);
    	}catch(Exception e){
    		logger.error("微信支付统一下单异常", e);
    		result =new MobileResultVO();
    		result.setCode(MobileResultVO.CODE_FAIL);
    		result.setMessage(MobileResultVO.CHECKCODE_FAIL_MESSAGE);
    	}
        return result;
    }
    
    @RequestMapping(value="/weapp/wxPayNotify",method=RequestMethod.POST)
    @ResponseBody
    @SysLog(description="微信支付回调",optCode="wxPayOrderCallBack")
    public String  payNotifybyWeChat(HttpServletRequest request, HttpServletResponse resp) throws Exception{
    	logger.info("进入支付回调");
        Map<String,String> notifyResult = new HashMap<String,String>();
    	notifyResult.put("code", "FAIL");
	    notifyResult.put("msg", "支付通知异常");
        try{
        	BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            //解析并给微信发回收到通知确认
            String xmlStr = sb.toString();
            logger.info(xmlStr);
            MobileResultVO payResult = orderFeeItemService.wxPayNotify(xmlStr);
            if(payResult.getCode()==MobileResultVO.CODE_SUCCESS){
            	notifyResult.put("code", "SUCCESS");
        	    notifyResult.put("msg", "支付通知成功");
            }
        }catch(Exception e){
        	logger.error("支付回调异常", e);
        }
        resp.setCharacterEncoding("UTF-8");
        String xml="<xml>"
                +"<return_code>"+notifyResult.get("code")+"</return_code>"
                +"<return_msg>"+notifyResult.get("msg")+"</return_msg>"
                +"</xml>";
        logger.info("return xml:"+xml);
        return xml;
    }
    
    
}
