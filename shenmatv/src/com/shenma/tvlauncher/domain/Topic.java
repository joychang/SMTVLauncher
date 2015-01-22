package com.shenma.tvlauncher.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题
 * @author joychang
 *
 */
public class Topic {

	private String code;
	private String msg;
	private List<TopicInfo> data = new ArrayList<TopicInfo>();

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

	public List<TopicInfo> getData() {
		return data;
	}

	public void setData(List<TopicInfo> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Topic [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}