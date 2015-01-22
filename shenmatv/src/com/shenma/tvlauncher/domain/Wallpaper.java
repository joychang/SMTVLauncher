package com.shenma.tvlauncher.domain;

import java.util.List;

public class Wallpaper {

	private String code;
	private String msg;
	private List<WallpaperInfo> data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<WallpaperInfo> getData() {
		return data;
	}
	public void setData(List<WallpaperInfo> data) {
		this.data = data;
	}
	
}
