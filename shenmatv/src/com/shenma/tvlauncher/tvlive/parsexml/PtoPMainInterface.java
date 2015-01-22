package com.shenma.tvlauncher.tvlive.parsexml;

import java.util.ArrayList;

public class PtoPMainInterface {

	@Override
	public String toString() {
		return "PtoPMainInterface [mainItems=" + mainItems + "]";
	}

	private ArrayList<MainItem> mainItems;

	public PtoPMainInterface() {
		mainItems = new ArrayList<MainItem>();
	}

	public ArrayList<MainItem> getMainItems() {
		return mainItems;
	}

	public void setMainItems(ArrayList<MainItem> mainItems) {
		this.mainItems = mainItems;
	}

}
