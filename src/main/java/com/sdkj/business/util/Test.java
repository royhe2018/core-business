package com.sdkj.business.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdlh.common.JsonUtil;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testJson();
		 String token = "userId=14&userType=1";
		 
		 
		System.out.println(token.split("\\&")[1]);
	}
	
	private static void testJson() {
		try{
			List<Map<String,Object>> placeList = new ArrayList<Map<String,Object>>();
			Map<String,Object> place1 = new HashMap<String,Object>();
			place1.put("placeAddress", "三34234压顶夫");
			place1.put("placeName", "三桥服务站");
			place1.put("placePosition", "108.45854584,34.45481255");
			Map<String,Object> place2 = new HashMap<String,Object>();
			place2.put("placeAddress", "雁塔路");
			place2.put("placeName", "雁载服务站");
			place2.put("placePosition", "108.45854584,34.45481255");
			placeList.add(place1);
			placeList.add(place2);
			System.out.println(URLEncoder.encode(JsonUtil.convertObjectToJsonStr(placeList), "UTF-8"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
