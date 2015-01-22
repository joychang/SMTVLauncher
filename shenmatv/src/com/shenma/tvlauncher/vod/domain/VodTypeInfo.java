package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;
import java.util.List;

public class VodTypeInfo implements Serializable{
	private int pageindex;
	private int videonum;
	private int totalpage;
	private List<VodDataInfo> data;

	public int getPageindex() {
		return pageindex;
	}



	public void setPageindex(int pageindex) {
		this.pageindex = pageindex;
	}



	public int getVideonum() {
		return videonum;
	}



	public void setVideonum(int videonum) {
		this.videonum = videonum;
	}



	public int getTotalpage() {
		return totalpage;
	}



	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}



	public List<VodDataInfo> getData() {
		return data;
	}



	public void setData(List<VodDataInfo> data) {
		this.data = data;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VodTypeInfo [pageindex=" + pageindex + ", videonum=" + videonum
				+ ", totalpage=" + totalpage + ", data=" + data + "]";
	}
	
}

