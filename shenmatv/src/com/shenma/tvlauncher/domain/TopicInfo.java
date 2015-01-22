package com.shenma.tvlauncher.domain;

/**
 * 专题详情
 * @author joychang
 *
 */
public class TopicInfo {

	private String id;
	private String ztname;
	private String bigpic;
	private String smallpic;
	private String ztdescribe;
	private String linkurl;
	private String videotype;
	private String status;
	private String expiretime;
	private String tjwei;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getZtname() {
		return ztname;
	}

	public void setZtname(String ztname) {
		this.ztname = ztname;
	}

	public String getBigpic() {
		return bigpic;
	}

	public void setBigpic(String bigpic) {
		this.bigpic = bigpic;
	}

	public String getSmallpic() {
		return smallpic;
	}

	public void setSmallpic(String smallpic) {
		this.smallpic = smallpic;
	}

	public String getZtdescribe() {
		return ztdescribe;
	}

	public void setZtdescribe(String ztdescribe) {
		this.ztdescribe = ztdescribe;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}

	public String getVideotype() {
		return videotype;
	}

	public void setVideotype(String videotype) {
		this.videotype = videotype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
	}

	public String getTjwei() {
		return tjwei;
	}

	public void setTjwei(String tjwei) {
		this.tjwei = tjwei;
	}

	@Override
	public String toString() {
		return "TopicInfo [id=" + id + ", ztname=" + ztname + ", bigpic="
				+ bigpic + ", smallpic=" + smallpic + ", ztdescribe="
				+ ztdescribe + ", linkurl=" + linkurl + ", videotype="
				+ videotype + ", status=" + status + ", expiretime="
				+ expiretime + ", tjwei=" + tjwei + "]";
	}
}