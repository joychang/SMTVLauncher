package com.shenma.tvlauncher.dao.bean;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name="tvscollect")
public class TVSCollect {
	
	@Id(column="id")
	private int id;					//id
	private String channelpic;		//tv台标路径
	private String channelname;		//tv台标名
	private int tvindex;			//在展示界面的坐标
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChannelpic() {
		return channelpic;
	}
	public void setChannelpic(String channelpic) {
		this.channelpic = channelpic;
	}
	public String getChannelname() {
		return channelname;
	}
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	public int getTvindex() {
		return tvindex;
	}
	public void setTvindex(int tvindex) {
		this.tvindex = tvindex;
	}
	
	
	
}
