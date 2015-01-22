package com.shenma.tvlauncher.domain;

import java.io.Serializable;

/**
 * @Descripyion 升级javabean
 * @author joychang
 * 
 */
public class Update implements Serializable {

	private String code;
	private String msg;
	private UpdateInfo data;

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

	public UpdateInfo getData() {
		return data;
	}

	public void setData(UpdateInfo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Update [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}

}
