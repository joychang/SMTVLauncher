package com.shenma.tvlauncher.dao.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	// 应用图标
	private Drawable appicon;
	// 应用大小
	private String appsize;
	// 安装时间
	private String appdata;
	// 应用名称
	private String appname;
	//应用包名
	private String apppack;
	//是否常用
	private boolean isLove;
	
	public AppInfo(Drawable appicon, String appsize, String appdata, String appname, String apppack) {
		this.appicon = appicon;
		this.appsize = appsize;
		this.appdata = appdata;
		this.appname = appname;
		this.apppack = apppack;
	}

	public AppInfo() {
	}

	public Drawable getAppicon() {
		return appicon;
	}

	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}

	public String getAppsize() {
		return appsize;
	}

	public void setAppsize(String appsize) {
		this.appsize = appsize;
	}

	public String getAppdata() {
		return appdata;
	}

	public void setAppdata(String appdata) {
		this.appdata = appdata;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getApppack() {
		return apppack;
	}

	public void setApppack(String apppack) {
		this.apppack = apppack;
	}
	
	public boolean isLove() {
		return isLove;
	}

	public void setLove(boolean isLove) {
		this.isLove = isLove;
	}

}
