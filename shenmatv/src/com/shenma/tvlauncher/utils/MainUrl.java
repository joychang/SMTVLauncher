package com.shenma.tvlauncher.utils;

public class MainUrl {
	//1.定义一个native的方法
	public native String urlFromC();
	
	public native String url_from_c();
	
	static{
		System.loadLibrary("Url");
	}
	
	public String getHeardUrl(){
		return urlFromC();
	}
	
	public String getMd5Url(){
		return url_from_c();
	}
}
