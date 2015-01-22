package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;
import java.util.List;

public class AboutInfo implements Serializable{
	private List<VodDataInfo> similary;
	private List<VodDataInfo> actor;
	public List<VodDataInfo> getSimilary() {
		return similary;
	}
	public void setSimilary(List<VodDataInfo> similary) {
		this.similary = similary;
	}
	public List<VodDataInfo> getActor() {
		return actor;
	}
	public void setActor(List<VodDataInfo> actor) {
		this.actor = actor;
	}
	@Override
	public String toString() {
		return "AboutInfo [similary=" + similary + ", actor=" + actor + "]";
	}

}
