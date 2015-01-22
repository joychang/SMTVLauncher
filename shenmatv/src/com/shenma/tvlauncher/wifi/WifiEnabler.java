package com.shenma.tvlauncher.wifi;

import com.shenma.tvlauncher.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;


public class WifiEnabler implements OnItemSelectedListener {
	private final Context mContext;
	private final Spinner mSpinner;
	private final WifiManager mWifiManager;
	private final IntentFilter mIntentFilter;
//	private ConnectivityManager mConnectivityManager;
	private boolean flag = true;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
			} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
				handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				handleStateChanged(((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState());
			}
		}
	};

	public WifiEnabler(Context context, Spinner spinner) {
		mContext = context;
		mSpinner = spinner;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public void resume() {
		// Wi-Fi state is sticky, so just let the receiver update UI
		try {
			mContext.registerReceiver(mReceiver, mIntentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSpinner.setOnItemSelectedListener(this);
	}

	public void pause() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSpinner.setOnItemSelectedListener(null);
	}

	private void handleWifiStateChanged(int state) {
		switch (state) {
		case WifiManager.WIFI_STATE_ENABLING:
			mSpinner.setEnabled(false);
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			mSpinner.setSelection(0);
			mSpinner.setEnabled(true);
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			mSpinner.setEnabled(false);
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			mSpinner.setSelection(1);
			mSpinner.setEnabled(true);
			break;
		default:
			mSpinner.setEnabled(true);
		}
	}

	private void handleStateChanged(NetworkInfo.DetailedState state) {
		// WifiInfo is valid if and only if Wi-Fi is enabled.
		// Here we use the state of the check box as an optimization.
//		if (state != null && mCheckBox.isChecked()) {
//
//			NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//			WifiInfo info = mWifiManager.getConnectionInfo();
//			if (info != null&& !info.getSSID().trim().equals("0x")) {
//				if (networkInfo.isConnected())
//					state = DetailedState.CONNECTED;
//				mCheckBox.setText2(get(mContext, AccessPoint.removeDoubleQuotes(info.getSSID()), state));
//			}else {
//				mCheckBox.setText2("");
//			}
//		}
	}

	public String get(Context context, String ssid, DetailedState state) {
		String[] formats = context.getResources().getStringArray((ssid == null) ? R.array.wifi_status : R.array.wifi_status_with_ssid);
		int index = state.ordinal();
		if (index >= formats.length || formats[index].length() == 0) {
			return "";
		}
		return String.format(formats[index], ssid);
	}

	public String get(Context context, DetailedState state) {
		return get(context, null, state);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(flag){
			//第一次不执行
			flag = false;
			return;
		}else {
			int wifiApState = mWifiManager.getWifiApState();
			if (position==0 && ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) || (wifiApState == WifiManager.WIFI_AP_STATE_DISABLING))) {
				mWifiManager.setWifiApEnabled(null, false);
			}
			if (mWifiManager.setWifiEnabled(position==0?true:false)) {
				System.out.println("wifi状态发生改变！");
			} else {
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
}
