package com.shenma.tvlauncher.domain;

import java.io.Serializable;

/**
 * @Descripyion 上传下载统计信息
 * 
 */
public class Upload implements Serializable {

	private String code;
	private String msg;

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

	@Override
	public String toString() {
		return "Update [code=" + code + ", msg=" + msg + "]";
	}

}
