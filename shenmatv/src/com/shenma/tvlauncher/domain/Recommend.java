package com.shenma.tvlauncher.domain;

import java.io.Serializable;
import java.util.List;
/**
 * @Description 推荐位
 * @author joychang
 *
 */
public class Recommend implements Serializable  {
	private String code;
	private String msg;
	private List<RecommendInfo> data;
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
	public List<RecommendInfo> getData() {
		return data;
	}
	public void setData(List<RecommendInfo> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Recommend [code="
				+ code
				+ ", msg="
				+ msg
				+ ", data="
				+ (data != null ? data
						.subList(0, Math.min(data.size(), maxLen)) : null)
				+ "]";
	}


	
}
