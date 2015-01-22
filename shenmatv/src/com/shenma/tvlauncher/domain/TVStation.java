package com.shenma.tvlauncher.domain;

import java.util.List;

public class TVStation {

	private String code;
	private String msg;
	private List<TVStationInfo> data;
	
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
	public List<TVStationInfo> getData() {
		return data;
	}
	public void setData(List<TVStationInfo> data) {
		this.data = data;
	}
	
}
