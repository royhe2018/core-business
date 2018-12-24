package com.sdkj.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdlh.common.JsonUtil;

public class RoutePointJsonTest {
	public static void main(String[] args){
		List<Map<String,String>> placeList = new ArrayList<Map<String,String>>();
		Map<String,String> place1 = new HashMap<String,String>();
		place1.put("placeAddress", "电子二路3号");
		place1.put("placeName", "华润万家");
		place1.put("contactName", "roy1");
		place1.put("contactPhone", "15129222933");
		place1.put("placePosition", "89.548984545,101.254984");
		placeList.add(place1);
		
		Map<String,String> place2 = new HashMap<String,String>();
		place2.put("placeAddress", "电子二路5号");
		place2.put("placeName", "人人乐");
		place2.put("contactName", "roy2");
		place2.put("contactPhone", "15129222934");
		place2.put("placePosition", "88.548984545,102.254984");
		placeList.add(place2);
		
		Map<String,String> place3 = new HashMap<String,String>();
		place3.put("placeAddress", "电子二路4号");
		place3.put("placeName", "易初连花");
		place3.put("contactName", "roy3");
		place3.put("contactPhone", "15129222935");
		place3.put("placePosition", "87.548984545,101.254984");
		placeList.add(place3);
		System.out.println(JsonUtil.convertObjectToJsonStr(placeList));
	}
}
