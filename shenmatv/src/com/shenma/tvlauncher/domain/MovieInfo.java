package com.shenma.tvlauncher.domain;

import java.io.Serializable;

/**
 * @Description影视分类
 * @author joychang
 *
 */
public class MovieInfo implements Serializable{
	
	private String ztname;//专题名称
	private String zttype;//专题类型
	private String ztwei;//专题位
	private String ztpicurl;//专题海报图
	private String zturl;//专题链接路径
	public String getZtname() {
		return ztname;
	}
	public void setZtname(String ztname) {
		this.ztname = ztname;
	}
	public String getZttype() {
		return zttype;
	}
	public void setZttype(String zttype) {
		this.zttype = zttype;
	}
	public String getZtwei() {
		return ztwei;
	}
	public void setZtwei(String ztwei) {
		this.ztwei = ztwei;
	}
	public String getZtpicurl() {
		return ztpicurl;
	}
	public void setZtpicurl(String ztpicurl) {
		this.ztpicurl = ztpicurl;
	}
	public String getZturl() {
		return zturl;
	}
	public void setZturl(String zturl) {
		this.zturl = zturl;
	}
	@Override
	public String toString() {
		return "MovieInfo [ztname=" + ztname + ", zttype=" + zttype
				+ ", ztwei=" + ztwei + ", ztpicurl=" + ztpicurl + ", zturl="
				+ zturl + "]";
	}

}
