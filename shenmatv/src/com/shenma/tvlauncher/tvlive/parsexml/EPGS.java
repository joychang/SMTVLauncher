package com.shenma.tvlauncher.tvlive.parsexml;

import java.util.ArrayList;

public class EPGS {
	private ArrayList<EPG> epgs;

	public EPGS() {
		super();
		this.epgs = new ArrayList<EPG>();
	}

	public ArrayList<EPG> getEpgs() {
		return epgs;
	}

	public void setEpgs(ArrayList<EPG> epgs) {
		this.epgs = epgs;
	}
}
