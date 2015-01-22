package com.shenma.tvlauncher.tvlive.network;

import com.shenma.tvlauncher.tvlive.parsexml.NetMediaCollection;

public class LiveConstant {
	public static NetMediaCollection liveDatas;
	public static int selectTotle = -1;
	public static final int CHANNEL_CLASS = 0;
	public static final int CHANNEL_TYPE = 1;
	public static final int TVLINK_TYPE = 2;
	public static final int DOWNLOAD_XML_DONE = 4;
	public static final int PLAYER_VIDEO = 5;
	public static final int APPLICATION_EXCEPTION = 6;
	public static final int SWICH_LINE = 7;
	public static final int HIDE_VIEW = 8;
	public static final int DISAPPEAR = 9;
	public static final int SELECT_CHANNE = 10;
	public static final int DO_NOT_CONNECT_NET = 0;
	public static final int DOWNLOAD_ENTER_XML = 11;
	public static final int DOWNLOAD_IMG_XML = 12;
	public static final int DOWNLOAD_NEWS_XML = 13;
	public static final int START_NEWS = 14;
	public static final int PLAY_NEXT = 15;
	public static final int PAUSE_NEWS = 16;
	public static final int EPG_VIEW = 17;
	public static final int REPLAY = 18;
	public static final int MINU_NUTE= 60000;
	public static final String ZBLIVE = "zblive";
	public static final String NEWS = "news";
	public static final String LOGO = "logo";
	public static final String EPG = "http://aps.lsott.com/egp/";
	public static final String LETV = "letv0http://live.gslb.letv.com/gslb";
	//public static final String LETV = "http://live.gslb.letv.com/gslb";
	public static final String GENERAL_TV = "http://wephd.live.cctv1949.com";
	public static final String LIVE_TIME = "http://api.letv.com/time";
	public static final String LIVE_KEY3 = "6r7b4e7d8bktfu8e";
	public static final String LIVE_KEY1 = "hdplive";
	
	public static String MAC;
	public static String SO_NAME = "libutp";
	
	
	//VIP节目
	public static final int PTOPCOLLECTION_SIZE=1;
	public static final int FAVORITE_SIZE=1;
	
	public static final int DOWNLOAD_PTOP_XML=20;
	public static final int START_PTOP=100;
	public static final int KILL_PTOP=101;
	public static String PTOP_SERVER=null;
	public static final int FAVORITE=102;
	
	//多屏操作
	public static final int PORT=16990;
	//public static final int PORT=6990;
	public static final String CONNECT_SUCCESS="success";
	public static final int START_PLAY=1000;
	public static final int START_THREAD=1001;
	public static final String CLOSE_MUILT="close";
	public static final int CONNECT_TIME=1002;
	
	public static final int CONNECT_SUCC=1003;
	public static final int CONNECT_FAIL=1004;
	
	//插件更新
	public static final int PLUGS_END=1005;
	public static final int PLUGS_START=1006;

}
