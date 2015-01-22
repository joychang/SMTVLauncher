/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shenma.tvlauncher.wifi;


import com.shenma.tvlauncher.R;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;

public class AccessPoint extends Preference {

	private static final String KEY_DETAILEDSTATE = "key_detailedstate";
	private static final String KEY_WIFIINFO = "key_wifiinfo";
	private static final String KEY_SCANRESULT = "key_scanresult";
	private static final String KEY_CONFIG = "key_config";

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;

	public enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	String ssid;
	String bssid;
	int security;
	int networkId;
	boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	private WifiConfiguration mConfig;
	/* package */ScanResult mScanResult;

	private int mRssi;
	private WifiInfo mInfo;
	private DetailedState mState;

	public static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	private static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public String getSecurityString(boolean concise) {
		Context context = getContext();
		switch (security) {
		case SECURITY_EAP:
			return concise ? context.getString(R.string.wifi_security_short_eap) : context.getString(R.string.wifi_security_eap);
		case SECURITY_PSK:
			switch (pskType) {
			case WPA:
				return concise ? context.getString(R.string.wifi_security_short_wpa) : context.getString(R.string.wifi_security_wpa);
			case WPA2:
				return concise ? context.getString(R.string.wifi_security_short_wpa2) : context.getString(R.string.wifi_security_wpa2);
			case WPA_WPA2:
				return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context.getString(R.string.wifi_security_wpa_wpa2);
			case UNKNOWN:
			default:
				return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context.getString(R.string.wifi_security_psk_generic);
			}
		case SECURITY_WEP:
			return concise ? context.getString(R.string.wifi_security_short_wep) : context.getString(R.string.wifi_security_wep);
		case SECURITY_NONE:
		default:
			return concise ? context.getString(R.string.wifi_security_none) : context.getString(R.string.wifi_security_none);
		}
	}

	private static PskType getPskType(ScanResult result) {
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa) {
			return PskType.WPA_WPA2;
		} else if (wpa2) {
			return PskType.WPA2;
		} else if (wpa) {
			return PskType.WPA;
		} else {
			return PskType.UNKNOWN;
		}
	}

	public AccessPoint(Context context, WifiConfiguration config) {
		super(context);
		loadConfig(config);
		refresh();
	}

	public AccessPoint(Context context, ScanResult result) {
		super(context);
		loadResult(result);
		refresh();
	}

	public AccessPoint(Context context, Bundle savedState) {
		super(context);

		mConfig = savedState.getParcelable(KEY_CONFIG);
		if (mConfig != null) {
			loadConfig(mConfig);
		}
		mScanResult = (ScanResult) savedState.getParcelable(KEY_SCANRESULT);
		if (mScanResult != null) {
			loadResult(mScanResult);
		}
		mInfo = (WifiInfo) savedState.getParcelable(KEY_WIFIINFO);
		if (savedState.containsKey(KEY_DETAILEDSTATE)) {
			mState = DetailedState.valueOf(savedState.getString(KEY_DETAILEDSTATE));
		}
		update(mInfo, mState);
	}

	public void saveWifiState(Bundle savedState) {
		savedState.putParcelable(KEY_CONFIG, mConfig);
		savedState.putParcelable(KEY_SCANRESULT, mScanResult);
		savedState.putParcelable(KEY_WIFIINFO, mInfo);
		if (mState != null) {
			savedState.putString(KEY_DETAILEDSTATE, mState.toString());
		}
	}

	private void loadConfig(WifiConfiguration config) {
		ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
		bssid = config.BSSID;
		security = getSecurity(config);
		networkId = config.networkId;
		mRssi = Integer.MAX_VALUE;
		mConfig = config;
	}

	private void loadResult(ScanResult result) {
		ssid = result.SSID;
		bssid = result.BSSID;
		security = getSecurity(result);
		wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
		if (security == SECURITY_PSK)
			pskType = getPskType(result);
		networkId = -1;
		mRssi = result.level;
		mScanResult = result;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
	}

	@Override
	public int compareTo(Preference preference) {
		if (!(preference instanceof AccessPoint)) {
			return 1;
		}
		AccessPoint other = (AccessPoint) preference;
		// Active one goes first.
		if (mInfo != other.mInfo) {
			return (mInfo != null) ? -1 : 1;
		}
		// Reachable one goes before unreachable one.
		if ((mRssi ^ other.mRssi) < 0) {
			return (mRssi != Integer.MAX_VALUE) ? -1 : 1;
		}
		// Configured one goes before unconfigured one.
		if ((networkId ^ other.networkId) < 0) {
			return (networkId != -1) ? -1 : 1;
		}
		// Sort by signal strength.
		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
		if (difference != 0) {
			return difference;
		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

	public boolean update(ScanResult result) {
		if (ssid.equals(result.SSID) && security == getSecurity(result)) {
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
				int oldLevel = getLevel();
				mRssi = result.level;
				if (getLevel() != oldLevel) {
					notifyChanged();
				}
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == SECURITY_PSK) {
				pskType = getPskType(result);
			}
			refresh();
			return true;
		}
		return false;
	}

	public void update(WifiInfo info, DetailedState state) {
		// log.d("state = " + state);
		boolean reorder = false;
		if (info != null && networkId != WifiConfiguration.INVALID_NETWORK_ID && networkId == info.getNetworkId()) {
			reorder = (mInfo == null);
			mRssi = info.getRssi();
			mInfo = info;
			mState = state;
			refresh();
		} else if (mInfo != null) {
			reorder = true;
			mInfo = null;
			mState = null;
			refresh();
		}
		if (reorder) {
			notifyHierarchyChanged();
		}
	}

	public void update(int networkId, int reason) {
		// log.d("networkId = " + networkId + ",this.networkId = " +
		// this.networkId + ",reason="+ reason);
		if (networkId != WifiConfiguration.INVALID_NETWORK_ID && this.networkId == networkId) {
			if (mConfig != null) {
				mConfig.disableReason = reason;
			}
			refresh();
		}
	}

	public void update(AccessPoint ap) {
		if (ap.getSsid().equals(ssid)) {
			if (bssid == null || ap.getBssid() == null || bssid.equals(ap.getBssid())) {
				networkId = ap.getNetworkId();
				bssid = ap.getBssid();
				mRssi = ap.getmRssi();
				mState = ap.getmState();
				mConfig = ap.getConfig();
				mInfo = ap.getInfo();
				security = ap.getSecurity();
				wpsAvailable = ap.isWpsAvailable();
				refresh();
			}

		}
	}

	public final boolean isWpsAvailable() {
		return wpsAvailable;
	}

	public int getLevel() {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 5);
		//return calculateSignalLevel(mRssi);
	}
	
	public int getLevelOnAct() {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}
	
	public String getLevelString() {
		if (mRssi == Integer.MAX_VALUE) {
			return "";
		}
		String result = null;
		int flag = WifiManager.calculateSignalLevel(mRssi, 5);
		switch (flag) {
		case 5:
			result = "非常强";
			break;
		case 4:
			result = "强";
			break;
		case 3:
			result = "一般";
			break;
		case 2:
			result = "弱";
			break;
		case 1:
			result = "非常弱";
			break;
		case 0:
			result = "无";
			break;
		}
		return result;
	}

	private int calculateSignalLevel(int rssi) {
		if (rssi > 0)
			return -1;
		if (rssi <= 0 && rssi > -50)
			return 3;
		if (rssi <= -50 && rssi > -60)
			return 2;
		if (rssi <= -60 && rssi > -70)
			return 1;
		return 0;
	}

	public WifiConfiguration getConfig() {
		return mConfig;
	}

	public WifiInfo getInfo() {
		return mInfo;
	}

	public DetailedState getState() {
		return mState;
	}

	public static String removeDoubleQuotes(String string) {
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	public static String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

	/** Updates the title and summary; may indirectly call notifyChanged() */
	private void refresh() {
		setTitle(ssid);
		Context context = getContext();
		if (mState != null) { // This is the active connection
			setSummary(get(context, mState));
		} else if (mRssi == Integer.MAX_VALUE) { // Wifi out of range
			setSummary(context.getString(R.string.wifi_not_in_range));
		} else if (mConfig != null && mConfig.status == WifiConfiguration.Status.DISABLED) {
			switch (mConfig.disableReason) {
			case WifiConfiguration.DISABLED_AUTH_FAILURE:
				setSummary(context.getString(R.string.wifi_disabled_password_failure));
				break;
			case WifiConfiguration.DISABLED_DHCP_FAILURE:
			case WifiConfiguration.DISABLED_DNS_FAILURE:
				setSummary(context.getString(R.string.wifi_disabled_network_failure));
				break;
			case WifiConfiguration.DISABLED_UNKNOWN_REASON:
				setSummary(context.getString(R.string.wifi_disabled_generic));
			}
		} else { // In range, not disabled.
			StringBuilder summary = new StringBuilder();
			if (mConfig != null) { // Is saved network
				summary.append(context.getString(R.string.wifi_remembered));
			}

			if (security != SECURITY_NONE) {
				String securityStrFormat;
				if (summary.length() == 0) {
					securityStrFormat = context.getString(R.string.wifi_secured_first_item);
				} else {
					securityStrFormat = context.getString(R.string.wifi_secured_second_item);
				}
				summary.append(String.format(securityStrFormat, getSecurityString(true)));
			} else {
				if (summary.length() > 0) {
					summary.append(",\u0020\u0020");
				}
				summary.append(getSecurityString(true));
			}

			// if (mConfig == null && wpsAvailable) { // Only list WPS available
			// // for unsaved networks
			// if (summary.length() == 0) {
			// summary.append(context
			// .getString(R.string.wifi_wps_available_first_item));
			// } else {
			// summary.append(context
			// .getString(R.string.wifi_wps_available_second_item));
			// }
			// }
			setSummary(summary.toString());
		}
	}

	public String getSsid() {
		return ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public int getSecurity() {
		return security;
	}

	public int getNetworkId() {
		return networkId;
	}

	public PskType getPskType() {
		return pskType;
	}

	public int getmRssi() {
		return mRssi;
	}

	public DetailedState getmState() {
		return mState;
	}

	/**
	 * Generate and save a default wifiConfiguration with common values. Can
	 * only be called for unsecured networks.
	 * 
	 * @hide
	 */
	protected void generateOpenNetworkConfig() {
		if (security != SECURITY_NONE)
			throw new IllegalStateException();
		if (mConfig != null)
			return;
		mConfig = new WifiConfiguration();
		mConfig.SSID = AccessPoint.convertToQuotedString(ssid);
		mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
	}

	private String get(Context context, String ssid, DetailedState state) {
		String[] formats = context.getResources().getStringArray((ssid == null) ? R.array.wifi_status : R.array.wifi_status_with_ssid);
		int index = state.ordinal();

		if (index >= formats.length || formats[index].length() == 0) {
			return "";
		}
		return String.format(formats[index], ssid);
	}

	private String get(Context context, DetailedState state) {
		return get(context, null, state);
	}
}
