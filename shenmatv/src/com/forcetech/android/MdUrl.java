package com.forcetech.android;



/**
 * @brief 加密地址获取
 * @author zengwenman
 *
 */
public class MdUrl {
	static{
		System.loadLibrary("md");
	}
	public String getUrl(){
		return getMdUrl();
	}
	private static native String getMdUrl();
}
