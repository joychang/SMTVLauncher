package com.shenma.tvlauncher.vod.domain;
/**
 * @Descripyion 升级javabean
 * @author joychang
 *
 */
public class UpdateInfo {
	private String version;
	private String description;
	private String apkurl;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getApkurl() {
		return apkurl;
	}
	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}
}
