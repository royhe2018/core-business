package com.sdkj.business.util;

public class Constant {
	
	public static final String ALI_OSS_ACCESS_PREFIX="http://sdlh.oss-cn-qingdao.aliyuncs.com/";
	
	/**
	 * 1客户；2司机
	 */
	public static final int USER_TYPE_CUSTOMER=1;
	public static final int USER_TYPE_DRIVER=2;
	
	/**
	 * 余额变更方式 1 运送收入；2业绩提成；3提现
	 */
	public static final int BALANCE_CHANGE_TYPE_INCOME=1;
	public static final int BALANCE_CAHNGE_TYPE_PERFORMANCEDRAWING=2;
	public static final int BALANCE_CHANGE_TYPE_WITHDRAW=3;
	
	public static final String MQ_TAG_DISPATCH_ORDER = "DISPATCH";
	public static final String MQ_TAG_TAKED_ORDER = "TAKED";
	public static final String MQ_TAG_STOWAGE_ORDER = "STOWAGE";
	public static final String MQ_TAG_SIGN_RECEIVE_ORDER = "RECEIVE";
	public static final String MQ_TAG_CANCLE_ORDER = "CANCLE";
	public static final String MQ_TAG_CANCLE_TAKED_ORDER = "CANCLE_TAKED";
	public static final String MQ_TAG_ARRIVE_ROUTE_POINT = "ARRIVE_POINT";
	public static final String MQ_TAG_LEAVE_ROUTE_POINT = "LEAVE_POINT";
	/**
	 * 订单状态0未接单；1已接单；2司机到达装货点，3装货完成；4运途中；5终点到达；6已签收
	 */
	public static final int ORDER_STATUS_WEIJIEDAN=0;
	public static final int ORDER_STATUS_JIEDAN=1;
	public static final int ORDER_STATUS_SIJIDAODAZHUANGHUO=2;
	public static final int ORDER_STATUS_ZHUANGHUO=3;
	public static final int ORDER_STATUS_YUNTUZHONG=4;
	public static final int ORDER_STATUS_SIJIDAODASHOUHUO=5;
	public static final int ORDER_STATUS_YIQIANSHOU=6;
	public static final int ORDER_STATUS_FINISH=7;
	
	/**
	 * 订单取消状态0未取消；1已取消
	 */
	public static final int ORDER_CANCLE_STATUS_NORMAL=0;
	public static final int ORDER_CANCLE_STATUS_CANCLE=1;
	/**
	 * 途经点状态 0未到达；1已到达；2已离开
	 */
	public static final int ROUTE_POINT_STATUS_NOT_ARRIVE=0;
	public static final int ROUTE_POINT_STATUS_ARRIVED=1;
	public static final int ROUTE_POINT_STATUS_LEAVED=2;
	/**
	 * 角色类型；1：司机；2司机推荐人；3客户推荐人；4平台
	 */
	public static final int FEE_DISPATCH_ROLE_DRIVER=1;
	public static final int FEE_DISPATCH_ROLE_DRIVER_REFEREE=2;
	public static final int FEE_DISPATCH_ROLE_CLIENT_REFEREE=3;
	public static final int FEE_DISPATCH_ROLE_PLATFORM=4;
	
	/**
	 * 订单费用支付状态0未支付；1已支付
	 */
	public static final int FEE_ITEM_PAY_STATUS_NOPAY=0;
	public static final int FEE_ITEM_PAY_STATUS_PAIED=1;
}
