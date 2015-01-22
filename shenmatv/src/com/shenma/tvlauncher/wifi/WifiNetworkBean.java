package com.shenma.tvlauncher.wifi;

/**
* 网络信息封装类
* @author drowtram
*
*/
public class WifiNetworkBean {
	private String ip;
	private String gateway;
	private String mask;
	private String dns;
	private String dns2;
	private String ssid ;
	private String bssid ;
	private int linkspeed ;//" Mbps"
	private String MACAddr ;
	private int mRssi ;
	
	
	public int getmRssi() {
		return mRssi;
	}

	public void setmRssi(int mRssi) {
		this.mRssi = mRssi;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public void setLinkspeed(int linkspeed) {
		this.linkspeed = linkspeed;
	}

	public void setMACAddr(String mACAddr) {
		MACAddr = mACAddr;
	}

	public String getSsid() {
		return ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public int getLinkspeed() {
		return linkspeed;
	}

	public String getMACAddr() {
		return MACAddr;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getDns2() {
		return dns2;
	}

	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}
}