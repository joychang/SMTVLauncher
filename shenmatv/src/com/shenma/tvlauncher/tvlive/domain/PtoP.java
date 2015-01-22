package com.shenma.tvlauncher.tvlive.domain;

/**
 * VIP节目
 * @author joychang
 *
 */
public class PtoP {

	@Override
	public String toString() {
		return "PtoP [filmId=" + filmId + ", serviceId=" + serviceId
				+ ", filmname=" + filmname + ", linkid=" + linkid + ", domain="
				+ domain + "]";
	}

	private String filmId = null;
	private String serviceId = null;
	private String filmname = null;
	private String linkid = null;
	private String domain = null;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getLinkid() {
		return linkid;
	}

	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}

	public String getFilmname() {
		return filmname;
	}

	public void setFilmname(String filmname) {
		this.filmname = filmname;
	}

	public String getFilmId() {
		return filmId;
	}

	public void setFilmId(String filmId) {
		this.filmId = filmId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

}
