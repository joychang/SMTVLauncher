package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;

public	class VodUrl implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -27350910593881993L;
	private String title;
	private String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "VodUrl [title=" + title + ", url=" + url + "]";
	}
	
	
}
