package com.shenma.tvlauncher.tvlive.parsexml;

import java.util.ArrayList;

public class NetMediaCollection {

	private ArrayList<NetMedia> netMediaList = null;

	public NetMediaCollection() {
		this.netMediaList = new ArrayList<NetMedia>();
	}

	public ArrayList<NetMedia> getNetMediaList() {
		return netMediaList;
	}

	public void setNetMediaList(ArrayList<NetMedia> netMediaList) {
		this.netMediaList = netMediaList;
	}
	
	@Override
	public String toString() {
		StringBuffer sbBuffer = new StringBuffer();
		for (int i = 0; i < netMediaList.size(); i++) {
			sbBuffer.append(netMediaList.get(i).toString());
		}
		return sbBuffer.toString();
	}
}
