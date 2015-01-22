package com.shenma.tvlauncher.domain;

import java.io.Serializable;
import java.util.List;
/**
 * @Description 影视分类位
 * @author joychang
 *
 */
public class MovieClassify implements Serializable  {
	private String code;
	private String msg;
	private List<MovieInfo> data;
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
	public List<MovieInfo> getData() {
		return data;
	}
	public void setData(List<MovieInfo> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "MovieClassify [code="
				+ code
				+ ", msg="
				+ msg
				+ ", data="
				+ (data != null ? data
						.subList(0, Math.min(data.size(), maxLen)) : null)
				+ "]";
	}
}
