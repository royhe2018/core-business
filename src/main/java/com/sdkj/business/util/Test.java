package com.sdkj.business.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdlh.common.DateUtilLH;
import com.sdlh.common.JsonUtil;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//testJson();
<<<<<<< .mine
		 Date start = DateUtilLH.convertStr2Date("2019-04-27 00:00", "yyyy-MM-dd HH:mm");
		 Date end = DateUtilLH.convertStr2Date("2019-04-27 23:59", "yyyy-MM-dd HH:mm");
		System.out.println(start.getTime());
		System.out.println(end.getTime());
=======
		 Date now = new Date();
		 now.setTime(1554704779514l);
		System.out.println(DateUtilLH.convertDate2Str(now, "yyyy-MM-dd HH:mm:ss"));

>>>>>>> .theirs
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
