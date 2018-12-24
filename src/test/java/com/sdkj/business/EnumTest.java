package com.sdkj.business;

import java.net.URLDecoder;
import java.net.URLEncoder;


public class EnumTest {
	 
	public static void main(String[] args){
		try {
			String rightStr = URLEncoder.encode("[{\"contactPhone\":\"15129222933\",\"placeAddress\":\"电子二路3号\",\"contactName\":\"roy1\",\"placePosition\":\"89.548984545,101.254984\",\"placeName\":\"华润万家\"},{\"contactPhone\":\"15129222934\",\"placeAddress\":\"电子二路5号\",\"contactName\":\"roy2\",\"placePosition\":\"88.548984545,102.254984\",\"placeName\":\"人人乐\"},{\"contactPhone\":\"15129222935\",\"placeAddress\":\"电子二路4号\",\"contactName\":\"roy3\",\"placePosition\":\"87.548984545,101.254984\",\"placeName\":\"易初连花\"}]", "UTF-8");
			System.out.println(rightStr);
			System.out.println(URLDecoder.decode(rightStr, "UTF-8"));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
