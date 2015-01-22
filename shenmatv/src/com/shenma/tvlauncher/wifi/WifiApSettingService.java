package com.shenma.tvlauncher.wifi;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
/**
 * wifi热点服务类
 * @author drowtram
 *
 */
public class WifiApSettingService {
	private Context mContext;
	private WifiManager mWifiManager;
	ConnectivityManager mConnectivityManager;
	private final IntentFilter mIntentFilter;
	public boolean isSetApEnableSucceed;

	public static final int SECURE_TYPE_WPA = 0;
	public static final int SECURE_TYPE_WPA2 = 1;
	public static final int SECURE_TYPE_OPEN = 2;


	public WifiApSettingService(Context mContext) {
		this.mContext = mContext;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		mIntentFilter = new IntentFilter(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
	}

	/**
	 * 设置是否打开wifi热点
	 * @param enable true 打开  false 关闭
	 */
	public void setSoftapEnabled(boolean enable) {
		int wifiState = mWifiManager.getWifiState();
		if (enable&& ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
			mWifiManager.setWifiEnabled(false);
		}
		mWifiManager.setWifiApEnabled(null, enable);
	}

	/**
	 * 保存热点名称密码 返回值：是否保存成功
	 */
	public boolean saveWifiApConfig(String ssid, String passwd,int secureType) {
		// check passwd
		if (secureType != 3 && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
			Utils.showToast(mContext, "请输入正确的密码6位以上数字或字母", R.drawable.toast_err);
			return false;
		}
		// check ssid
		if (TextUtils.isEmpty(ssid)) {
			Utils.showToast(mContext, "请输入热点名称", R.drawable.toast_err);
			return false;
		}

		WifiConfiguration config = setApConfig(ssid,passwd,secureType);
		if (config == null) {
			return false;
		}
		WifiManager wifiManager = mWifiManager;
		if (WifiManager.WIFI_AP_STATE_ENABLED == wifiManager.getWifiApState()) {
			// restart wifiap
			wifiManager.setWifiApEnabled(null, false);
			wifiManager.setWifiApEnabled(config, true);
		} else {
			wifiManager.setWifiApConfiguration(config);
		}
		return true;
	}

	/**
	 * 设置热点名称和密码
	 */
	private WifiConfiguration setApConfig(String ssid, String passwd,int secureType) {
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = ssid;
		// set secure type
		switch (secureType) {
		case SECURE_TYPE_WPA:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			 config.preSharedKey = passwd;
			break;
		case SECURE_TYPE_WPA2:
			config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			 config.preSharedKey = passwd;
			break;
		case SECURE_TYPE_OPEN:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;
		}
		return config;
		
	}
	
	/**
	 * @return 返回ap的状态
	 */
	public int getApState(){
		return mWifiManager.getWifiApState();
	}
	
	/**
	 * 返回系统预设的wifi ap信息
	 * @return
	 */
	public WifiConfiguration getApConfigInfo(){
		return mWifiManager.getWifiApConfiguration();
	}
	
	/**
	 * 获取wifi热点的加密类型
	 * @param config
	 * @return
	 */
	public int getWifiSecureType(WifiConfiguration config){
		if(config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)){
			return SECURE_TYPE_WPA;
		}else if (config.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return SECURE_TYPE_WPA2;
		}else {
			return SECURE_TYPE_OPEN;
		}
	}
}
