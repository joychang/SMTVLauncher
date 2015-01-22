package com.shenma.tvlauncher.tvlive.parsexml;

public class MainItem {
	@Override
	public String toString() {
		return "MainItem [list_name=" + list_name + ", list_src=" + list_src
				+ "]";
	}

	private String list_name;
	private String list_src;

	public String getList_name() {
		return list_name;
	}

	public void setList_name(String list_name) {
		this.list_name = list_name;
	}

	public String getList_src() {
		return list_src;
	}

	public void setList_src(String list_src) {
		this.list_src = list_src;
	}

}
