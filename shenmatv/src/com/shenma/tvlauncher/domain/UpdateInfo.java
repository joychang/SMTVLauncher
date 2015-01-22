package com.shenma.tvlauncher.domain;

import java.io.Serializable;

/**
 * @Descripyion 升级javabean
 * @author joychang
 * 
 */
public class UpdateInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;//编号
	private String nowversion;// 当前版本号
	private String updateversion;// 升级版本号
	private String versionremark;// 描述
	private String apkurl;// apk升级地址
	private String type;// 升级选项。1---可选择更新。2---强制更新
	private String fromplat;//渠道名称
	private String devicetype;//设备类型
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNowversion() {
		return nowversion;
	}
	public void setNowversion(String nowversion) {
		this.nowversion = nowversion;
	}
	public String getUpdateversion() {
		return updateversion;
	}
	public void setUpdateversion(String updateversion) {
		this.updateversion = updateversion;
	}
	public String getVersionremark() {
		return versionremark;
	}
	public void setVersionremark(String versionremark) {
		this.versionremark = versionremark;
	}
	public String getApkurl() {
		return apkurl;
	}
	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFromplat() {
		return fromplat;
	}
	public void setFromplat(String fromplat) {
		this.fromplat = fromplat;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	@Override
	public String toString() {
		return "UpdateInfo [id=" + id + ", nowversion=" + nowversion
				+ ", updateversion=" + updateversion + ", versionremark="
				+ versionremark + ", apkurl=" + apkurl + ", type=" + type
				+ ", fromplat=" + fromplat + ", devicetype=" + devicetype + "]";
	}
	
}
