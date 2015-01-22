package com.shenma.tvlauncher.tvlive.network;

import com.shenma.tvlauncher.tvlive.TVLivePlayer;



public class ServerUtil {
	private String filmID = "";
	private String server = "";
	private String link = "";

	public String getFilmID() {
		return filmID;
	}

	public void setFilmID(String filmID) {
		this.filmID = filmID;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public static String getServerPath(String filmID, String server, String link) {
		return "http://127.0.0.1:9906/api?func=switch_chan&id=" + filmID
				+ "&link=" + link + "&userid=" + LiveConstant.MAC
				+ "&flag=&monitor=&path=&file=&server=" + server;
	}

}
