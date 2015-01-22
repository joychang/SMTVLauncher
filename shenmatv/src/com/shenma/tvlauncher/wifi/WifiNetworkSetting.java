package com.shenma.tvlauncher.wifi;

import java.net.InetAddress;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;

/**
 * 网络信息设置类
 * @author drowtram
 *
 */
public class WifiNetworkSetting {
	LinkProperties mLinkProperties = null;

	public WifiNetworkSetting() {
		mLinkProperties = new LinkProperties();
		mLinkProperties.clear();
	}

	
	public LinkProperties getmLinkProperties() {
		return mLinkProperties;
	}


	/**
	 * 设置ip
	 * 
	 * @param ipAddr
	 * @param networkPrefixLength
	 */
	public void setIP(String ipAddr, int networkPrefixLength) {
		InetAddress inetAddr = null;
		try {
			inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
			if (networkPrefixLength < 0 || networkPrefixLength > 32) {
				// return
				// R.string.wifi_ip_settings_invalid_network_prefix_length;
			}
		} catch (Exception e) {
			// return R.string.wifi_ip_settings_invalid_ip_address;
		}
		mLinkProperties.addLinkAddress(new LinkAddress(inetAddr,
				networkPrefixLength));
	}

	/**
	 * 设置 DNS
	 * 
	 * @param sDns
	 */
	public void setDns(String sDns) {
		InetAddress dnsAddr = null;
		try {
			dnsAddr = NetworkUtils.numericToInetAddress(sDns);
		} catch (Exception e) {
			// return R.string.wifi_ip_settings_invalid_dns;
		}
		mLinkProperties.addDns(dnsAddr);
	}
	
	/**
	 * 设置gateway
	 * @param gateway
	 */
	public void setGateway(String gateway) {
		InetAddress gatewayAddr = null;
		try {
			gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
		} catch (Exception e) {
			// return R.string.wifi_ip_settings_invalid_gateway;
		}
		mLinkProperties.addRoute(new RouteInfo(gatewayAddr));
	}
}
