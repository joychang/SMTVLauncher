package com.shenma.tvlauncher.domain;

import java.io.Serializable;

/**
 * @Descripyion 用户javabean
 * @author joychang
 * 
 */
public class UserStatement implements Serializable {

	private String code;
	private String msg;
	private String exitinfo;
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
	public String getExitinfo() {
		return exitinfo;
	}
	public void setExitinfo(String exitinfo) {
		this.exitinfo = exitinfo;
	}
	@Override
	public String toString() {
		return "UserStatement [code=" + code + ", msg=" + msg + ", exitinfo="
				+ exitinfo + "]";
	}

}