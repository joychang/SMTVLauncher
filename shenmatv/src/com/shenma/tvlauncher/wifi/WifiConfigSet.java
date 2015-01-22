package com.shenma.tvlauncher.wifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.util.Log;

public class WifiConfigSet {
	private static WifiManager mWifiManager;

	public static void changeToDhcp(Context context) {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		Log.d("===SUNNIWELL====", existingConfigs.size()+"");
		if(existingConfigs.size()==0)
			return;
		mWifiManager.disconnect();
		for (WifiConfiguration existingConfig : existingConfigs) {
			existingConfig.ipAssignment = IpAssignment.DHCP;
			int a=mWifiManager.updateNetwork(existingConfig);
			Log.d("===SUNNIWELL====","updateNetwork  results="+a+"");
		}
		mWifiManager.reassociate();
		mWifiManager.reconnect();
	}

	public static void changeToStatic(Context context,WifiNetworkBean bean) {
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		Log.d("===SUNNIWELL====", existingConfigs.size()+"");
		if(existingConfigs.size()==0)
			return;
		mWifiManager.disconnect();
		for (WifiConfiguration existingConfig : existingConfigs) {
			existingConfig.ipAssignment = IpAssignment.STATIC;
			WifiNetworkSetting mNetwork = new WifiNetworkSetting();
			int iMaskPre = getNetmaskLength(bean.getMask());
			mNetwork.setIP(bean.getIp(), iMaskPre);
			mNetwork.setGateway(bean.getGateway());
			mNetwork.setDns(bean.getDns());
			mNetwork.setDns(bean.getDns2());
			existingConfig.linkProperties = mNetwork.getmLinkProperties();
			int b=mWifiManager.updateNetwork(existingConfig);
			Log.d("===SUNNIWELL====","updateNetwork  results="+b+"");
		}
		mWifiManager.reassociate();
		mWifiManager.reconnect();
	}
	private static int getNetmaskLength(String maskStr) {
		int mask = toInt(maskStr);
		int prefixLength = 0;
		for (int i = 1; i < Integer.SIZE; i++) {
			if (((mask >> i) & 1) == 1) {
				prefixLength++;
			}
		}
		return prefixLength;
	}
	private static int toInt(final String ip) {
		int[] intIP = changeToInt(ip);
		if (intIP != null) {
			int num = ((intIP[0] << 24) | (intIP[1] << 16) | (intIP[2] << 8) | intIP[3]);
			return num;
		}
		return 0;
	}
	private static int[] changeToInt(final String ip) {
		if (ip == null)
			return null;
		String[] str = ip.split("\\.");
		if (str.length != 4)
			return null;
		try {
			int[] intIP = new int[4];
			for (int i = 0; i < str.length; i++) {
				intIP[i] = Integer.parseInt(str[i]);
			}
			return intIP;
		} catch (Exception e) {
		}
		return null;
	}
}
