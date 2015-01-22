package com.forcetech.android;

import android.util.Log;

/**
 * @brief 主接口地址获取
 * @author zengwenamn
 *
 */

public class MainUrl {
	
	static{
		System.loadLibrary("Url");
	}
	
	public String interfa(){
		Log.d("joychang", "getUrl()="+getUrl());
		return getUrl();
	}
	
	private static native String getUrl();

}
