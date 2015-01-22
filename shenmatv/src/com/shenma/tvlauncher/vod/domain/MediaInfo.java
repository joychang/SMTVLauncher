package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;

/***
 * @Description 媒体数据info
 * @author joychang
 * 
 */
public class MediaInfo implements Serializable {
	public String getMediaurl() {
		return mediaurl;
	}

	public void setMediaurl(String mediaurl) {
		this.mediaurl = mediaurl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String mediaurl;
	private String name;
	private String type;

	@Override
	public String toString() {
		return "MediaInfo [mediaurl=" + mediaurl + ", name=" + name + ", type="
				+ type + "]";
	}

}
