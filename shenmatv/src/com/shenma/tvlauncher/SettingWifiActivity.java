package com.shenma.tvlauncher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.WiFiDialog;
import com.shenma.tvlauncher.wifi.AccessPoint;
import com.shenma.tvlauncher.wifi.IPUtil;
import com.shenma.tvlauncher.wifi.SWifiManager;
import com.shenma.tvlauncher.wifi.WifiAdapter;
import com.shenma.tvlauncher.wifi.WifiApSettingService;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingWifiActivity extends BaseActivity {

	private TextView wifi_ap_tv;
	private ListView wifi_setting_list;
	private WifiManager mWifiManager;
	private SWifiManager mSWifiManager;
	private Scanner mScanner;
	private List<AccessPoint> mList;
	private WifiAdapter mWifiAdapter;
	private IntentFilter mFilter;
	private BroadcastReceiver mReceiver;
	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private AtomicBoolean mConnected = new AtomicBoolean(false);
	private boolean saved, connected, haspassword;
	private String wifi_ap_pass;
	private String wifi_ap_secure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_wifi);
		findViewById(R.id.setting_wifi).setBackgroundResource(R.drawable.video_details_bg);
		initData();
		initView();
	}
	
	private void initData() {
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mScanner = new Scanner();
		mSWifiManager = SWifiManager.getInstance(this);
		mList = new ArrayList<AccessPoint>();//mSWifiManager.getWifiList();
		mWifiAdapter = new WifiAdapter(this, mList);
		mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction(WifiManager.ERROR_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };
	}
	
	@Override
	protected void initView() {
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {
		wifi_ap_tv = (TextView) findViewById(R.id.wifi_ap_tv);
		wifi_setting_list = (ListView) findViewById(R.id.wifi_setting_list);
	}

	@Override
	protected void setListener() {
		wifi_ap_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showWifiApConfigDialog();
			}
		});
		wifi_ap_tv.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus){
					wifi_ap_tv.setTextColor(0xFF000000);
				}else{
					wifi_ap_tv.setTextColor(0xFFFFFFFF);
				}
			}
		});
		wifi_setting_list.setAdapter(mWifiAdapter);
		wifi_setting_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startConnectWifi(mList.get(position));
			}
		});
	}
	
	/**
	 * 显示wifi热点配置弹框
	 */
	private void showWifiApConfigDialog(){
		WiFiDialog.Builder builder = new WiFiDialog.Builder(this);
		View mView = View.inflate(this,R.layout.wifi_ap_config, null);
		//findViewById
		final EditText wifi_ap_name_et = (EditText) mView.findViewById(R.id.wifi_ap_name_et);
		final EditText wifi_ap_pass_et = (EditText) mView.findViewById(R.id.wifi_ap_pass_et);
		RelativeLayout wifi_ap_secure_rl = (RelativeLayout) mView.findViewById(R.id.wifi_ap_secure_rl);
		final TextView wifi_ap_secure_decode_text = (TextView) mView.findViewById(R.id.wifi_ap_secure_decode_text);
		final ImageButton wifi_ap_secure_left_arrows = (ImageButton) mView.findViewById(R.id.wifi_ap_secure_left_arrows);
		final ImageButton wifi_ap_secure_right_arrows = (ImageButton) mView.findViewById(R.id.wifi_ap_secure_right_arrows);
		final CheckBox wifi_ap_showpass_cb = (CheckBox) mView.findViewById(R.id.wifi_ap_showpass_cb);
		//setData
		final WifiApSettingService mApSettingService = new WifiApSettingService(context);
		final String[] secureTypes = getResources().getStringArray(R.array.wifi_ap_secure_types);
		wifi_ap_secure = sp.getString("wifi_ap_secure", secureTypes[0]);
		WifiConfiguration mConfiguration = mApSettingService.getApConfigInfo();
		wifi_ap_name_et.setText(mConfiguration.SSID);
		wifi_ap_pass = mConfiguration.preSharedKey;
		wifi_ap_secure_decode_text.setText(wifi_ap_secure);
		int mSecureType = mApSettingService.getWifiSecureType(mConfiguration);
		switch (mSecureType) {
		case WifiApSettingService.SECURE_TYPE_WPA2:
			wifi_ap_secure_decode_text.setText("WPA2 PSK");
			break;
		case WifiApSettingService.SECURE_TYPE_WPA:
			wifi_ap_secure_decode_text.setText("WPA PSK");
			break;
		case WifiApSettingService.SECURE_TYPE_OPEN:
			wifi_ap_secure_decode_text.setText("Open");
			break;
		}
		setPassInputEnable(wifi_ap_secure, wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
		//setListener
		wifi_ap_secure_left_arrows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String wifi_ap_secure = wifi_ap_secure_decode_text.getText().toString();
				int index = 0;
				for (int i = 0; i < secureTypes.length; i++) {
					if(wifi_ap_secure != null && wifi_ap_secure.equals(secureTypes[i])){
						index = i;
					}
				}
				if (index == 0){
					wifi_ap_secure_decode_text.setText(secureTypes[secureTypes.length - 1]);
					setPassInputEnable(secureTypes[secureTypes.length - 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
				} else {
					wifi_ap_secure_decode_text.setText(secureTypes[index - 1]);
					setPassInputEnable(secureTypes[index - 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
				}
			}
		});
		wifi_ap_secure_right_arrows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String wifi_ap_secure = wifi_ap_secure_decode_text.getText().toString();
				int index = 0;
				for (int i = 0; i < secureTypes.length; i++) {
					if(wifi_ap_secure != null && wifi_ap_secure.equals(secureTypes[i])){
						index = i;
					}
				}
				if (index == secureTypes.length - 1){
					wifi_ap_secure_decode_text.setText(secureTypes[0]);
					setPassInputEnable(secureTypes[0], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
				} else {
					wifi_ap_secure_decode_text.setText(secureTypes[index + 1]);
					setPassInputEnable(secureTypes[index + 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
				}
			}
		});
		wifi_ap_secure_rl.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				String wifi_ap_secure = wifi_ap_secure_decode_text.getText().toString();
				int index = 0;
				if (event.getAction() == KeyEvent.ACTION_DOWN){
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						for (int i = 0; i < secureTypes.length; i++) {
							if(wifi_ap_secure != null && wifi_ap_secure.equals(secureTypes[i])){
								index = i;
							}
						}
						if (index == 0) {
							wifi_ap_secure_decode_text.setText(secureTypes[secureTypes.length - 1]);
							setPassInputEnable(secureTypes[secureTypes.length - 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
						}else {
							wifi_ap_secure_decode_text.setText(secureTypes[index - 1]);
							setPassInputEnable(secureTypes[index - 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
						}
						wifi_ap_secure_left_arrows.setImageResource(R.drawable.select_left_arrows_f);
						break;

					case KeyEvent.KEYCODE_DPAD_RIGHT:
						for (int i = 0; i < secureTypes.length; i++) {
							if(wifi_ap_secure != null && wifi_ap_secure.equals(secureTypes[i])){
								index = i;
							}
						}
						if (index == secureTypes.length - 1) {
							wifi_ap_secure_decode_text.setText(secureTypes[0]);
							setPassInputEnable(secureTypes[0], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
						}else {
							wifi_ap_secure_decode_text.setText(secureTypes[index + 1]);
							setPassInputEnable(secureTypes[index + 1], wifi_ap_pass, wifi_ap_pass_et, wifi_ap_showpass_cb);
						}
						wifi_ap_secure_right_arrows.setImageResource(R.drawable.select_right_arrows_f);
						break;
					}
				}
				else if(event.getAction() == KeyEvent.ACTION_UP){
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_LEFT:
						wifi_ap_secure_left_arrows.setImageResource(R.drawable.select_left_arrows_n);
						break;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						wifi_ap_secure_right_arrows.setImageResource(R.drawable.select_right_arrows_n);
						break;
					}
				}
				return false;
			}
		});
		wifi_ap_showpass_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					wifi_ap_pass_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					wifi_ap_pass_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				wifi_ap_pass_et.postInvalidate();
			}
		});
		builder.setContentView(mView);
		builder.setPositiveButton("开启",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String wifi_ap_name = wifi_ap_name_et.getText().toString().trim();
						String wifi_ap_pass = wifi_ap_pass_et.getText().toString().trim();
						String wifi_ap_secureType = wifi_ap_secure_decode_text.getText().toString();
						int secureType = WifiApSettingService.SECURE_TYPE_WPA2;
						if("WPA2 PSK".equals(wifi_ap_secureType)){
							secureType = WifiApSettingService.SECURE_TYPE_WPA2;
						}else if ("WPA PSK".equals(wifi_ap_secureType)) {
							secureType = WifiApSettingService.SECURE_TYPE_WPA;
						}else {
							secureType = WifiApSettingService.SECURE_TYPE_OPEN;
						}
						if(mApSettingService.saveWifiApConfig(wifi_ap_name, wifi_ap_pass,secureType)){
							setWifiApEnable(mApSettingService);
						}else {
							Utils.showToast(context, "wifi热点保存失败", R.drawable.toast_shut);
						}
						dialog.cancel();
					}
				});
		builder.setNeutralButton("关闭",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//关闭热点，打开wifi
						mApSettingService.setSoftapEnabled(false);
						mSWifiManager.openWifi();
						Utils.showToast(context, "wifi热点关闭，wifi正在打开", R.drawable.toast_smile);
						dialog.cancel();
					}
				});
//		WiFiDialog dialog = builder.create();
//		dialog.getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.sm_458), getResources().getDimensionPixelSize(R.dimen.sm_259));
//		dialog.show();
		builder.create().show();
	}
	
	/**
	 * 打开wifi热点
	 */
	private void setWifiApEnable(WifiApSettingService mApSettingService){
		// 如果要求打开ap并且当前ap已经打开，则不做任何操作
		int apState = mApSettingService.getApState();
		if (!(apState == 10)) { // WifiManager.WIFI_AP_STATE_ENABLED
								// 获取的状态码不对，状态码10为连接上，11为断开
			mApSettingService.setSoftapEnabled(true);
			Utils.showToast(context, "wifi热点已启用", R.drawable.toast_smile);
		}
	}
	
	/**
	 * 设置wifi热点密码输入框是否可输入
	 * @param secureType
	 * @param defaultPass
	 * @param wifi_ap_pass_et
	 * @param wifi_ap_showpass_cb
	 */
	private void setPassInputEnable(String secureType,String defaultPass, EditText wifi_ap_pass_et, CheckBox wifi_ap_showpass_cb){
		if("Open".equals(secureType)){
			wifi_ap_pass_et.setEnabled(false);
			wifi_ap_pass_et.setText("");
			wifi_ap_pass_et.setFocusable(false);
			wifi_ap_showpass_cb.setEnabled(false);
			wifi_ap_showpass_cb.setFocusable(false);
		}else {
			wifi_ap_pass_et.setEnabled(true);
			wifi_ap_pass_et.setText(defaultPass);
			wifi_ap_pass_et.setFocusable(true);
			wifi_ap_showpass_cb.setEnabled(true);
			wifi_ap_showpass_cb.setFocusable(true);
		}
	}
	
	//wifi扫描
	public class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			//if (mWifiManager.startScanActive()) 强制扫描 在部分设备上执行会出现NoSuchMethodError异常
			if (mWifiManager.startScan()) { //非强制性扫描
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(SettingWifiActivity.this, "扫描不到网络。", Toast.LENGTH_LONG).show();
				return;
			}
			sendEmptyMessageDelayed(0, 6000);
		}
	}
	private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
        	//更新wifi状态
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action) ||
                WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action) ||
                WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
        		//更新wifi列表
                updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
        	updateConnectionInfo(intent.hasExtra(WifiManager.EXTRA_SUPPLICANT_ERROR), intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1));
            if (!mConnected.get()) {
            	//更新连接状态
                updateConnectionState(WifiInfo.getDetailedStateOf((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
            }

        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateAccessPoints();
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            updateConnectionState(null);
        } else if (WifiManager.ERROR_ACTION.equals(action)) {
            int errorCode = intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0);
            switch (errorCode) {
                case WifiManager.WPS_OVERLAP_ERROR:
                    Toast.makeText(context, "检测到另一个wifi保存对话，请等几分钟后再试。",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
	private void updateWifiState(int state) {
	    if(state == WifiManager.WIFI_STATE_ENABLED){
//	    	wiFi_title.setText("Wi-Fi已打开。");
	        mScanner.resume();
			updateAccessPoints();
	     }// not break, to avoid the call to pause() below
	    else{
	    	// Start
				if (state == WifiManager.WIFI_STATE_ENABLING) {
//					wiFi_title.setText("正在打开Wi-Fi...");
				} else if(state == WifiManager.WIFI_STATE_DISABLING){
//					wiFi_title.setText("正在关闭Wi-Fi...");
				}else if (state == WifiManager.WIFI_STATE_DISABLED) {
//					wiFi_title.setText("Wi-Fi已关闭。");
				}
				// End
				mScanner.pause();
				mList.clear();
				mWifiAdapter.notifyDataSetChanged();
				wifi_setting_list.requestFocus();
	//            case WifiManager.WIFI_STATE_ENABLING:
	//                wiFi_title.setText("正在打开Wi-Fi...");
	//                break;
	//
	//            case WifiManager.WIFI_STATE_DISABLED:
	//            	wiFi_title.setText("Wi-Fi已关闭。");
	//            	WifiAdapter wifiAdapter = (WifiAdapter)wiFi_lv.getAdapter();
	//                wifiAdapter.setData(null);
	//                wifiAdapter.notifyDataSetChanged();
	//                break;
	    }
	//        mLastInfo = null;
	//        mLastState = null;
	//        mScanner.pause();
	 }
	
	/**
     * Shows the latest access points available with supplimental information like
     * the strength of network and the security for it.
     */
    private void updateAccessPoints() {
        final int wifiState = mWifiManager.getWifiState();

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                // AccessPoints are automatically sorted with TreeSet.
            	final Collection<AccessPoint> accessPoints = constructAccessPoints();
    			mList.clear();
    			mList.addAll(accessPoints);
                break;

            case WifiManager.WIFI_STATE_ENABLING:
            	mList.clear();
//            	wiFi_title.setText("正在打开Wi-Fi...");
                break;

            case WifiManager.WIFI_STATE_DISABLING:
//            	wiFi_title.setText("正在关闭Wi-Fi...");
                break;

            case WifiManager.WIFI_STATE_DISABLED:
            	mList.clear();
//            	wiFi_title.setText("Wi-Fi已关闭。");
                break;
        }
        mWifiAdapter.notifyDataSetChanged();
    }
    
    private void updateConnectionState(DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {//正在获取ip地址
            mScanner.pause();
        } else {
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }
        for (int i = mList.size() - 1; i >= 0 ; --i) {
			// Maybe there's a WifiConfigPreference
        	final AccessPoint accessPoint = mList.get(i);
        	accessPoint.update(mLastInfo,mLastState);
		}
        Collections.sort(mList);
        mWifiAdapter.notifyDataSetChanged();
    }
    
    /**
	 * 密码输入错误，不保存网络 再重新连接新的已保存网络
	 * @param hasError
	 * @param error
	 */
	private void updateConnectionInfo(boolean hasError, int error) {
		if (hasError) {
			String mesg = "";
			if (error == WifiManager.ERROR_AUTHENTICATING)
				mesg = "认证失败!";
			if (!mesg.equals("")) {
				Message msg = new Message();
				msg.what = SWifiManager.SEND_MSG_TO_STATUSBAR;
				msg.obj = mesg;
				SWifiManager.getInstance(this).sendMsg(msg);
			}
			mWifiManager.forgetNetwork(SWifiManager.getInstance(this).getmCurrentNetworkId());
			mWifiManager.saveConfiguration();
			
			
			WifiInfo info = mWifiManager.getConnectionInfo();
			AccessPoint reConnectAp = null;
			final List<ScanResult> results = mWifiManager.getScanResults();
			if (results != null) {
				for (ScanResult result : results) {
					// Ignore hidden and ad-hoc networks.
					if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
						continue;
					}
					 if (info.getSSID() == null)
					 {
						 if (result.BSSID != null && result.BSSID.equals(info.getBSSID())) {
								reConnectAp = new AccessPoint(this,result);
								break;
							}
					 }else if (result.BSSID.equals(AccessPoint.removeDoubleQuotes((info.getSSID())))) {
							reConnectAp = new AccessPoint(this,result);
							break;
						}
				}
			}

			if (reConnectAp != null)
			{
				startConnectWifi(reConnectAp);
			}
		}
	}
    
    /**
     * Returns sorted list of access points 
     * @return
     */
	private List<AccessPoint> constructAccessPoints() {
		ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		/**
		 * Lookup table to more quickly update AccessPoints by only considering
		 * objects with the correct SSID. Maps SSID -> List of AccessPoints with
		 * the given SSID.
		 */
		Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

		final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		if (configs != null) {
			for (WifiConfiguration config : configs) {
				if(config.status==Status.DISABLED)
					config.status = Status.CURRENT;
				AccessPoint accessPoint = new AccessPoint(this, config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoints.add(accessPoint);
				apMap.put(accessPoint.getSsid(), accessPoint);
			}
		}

		final List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
					if (accessPoint.update(result))
						found = true;
				}
				if (!found) {
					AccessPoint accessPoint = new AccessPoint(this, result);
					accessPoints.add(accessPoint);
					apMap.put(accessPoint.getSsid(), accessPoint);
				}
			}
		}

		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);
		return accessPoints;
	}
	
	/** A restricted multimap for use in constructAccessPoints */
	private class Multimap<K, V> {
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key) {
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}
	
	
	private void startConnectWifi(final AccessPoint ap) {
		if (ap == null) {
			return;
		}
		saved = (ap.getNetworkId() != -1);
		String current = "";
		try {
			ConnectivityManager mConnManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWifi.isConnected()) {
				WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
				current = wifiInfo.getSSID();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		connected = ap.getSsid().equals(AccessPoint.removeDoubleQuotes(current));
		haspassword = !(ap.getSecurity() == AccessPoint.SECURITY_NONE);
		WiFiDialog.Builder builder = new WiFiDialog.Builder(this);
		if (connected && saved) {// 已连接
			View mView = View.inflate(this,R.layout.wifi_content_conn, null);
			TextView wifi_conn_ssid_tv = (TextView) mView.findViewById(R.id.wifi_conn_ssid_tv);
			TextView wifi_conn_ip_tv = (TextView) mView.findViewById(R.id.wifi_conn_ip_tv);
			wifi_conn_ssid_tv.setText(ap.getSsid());
			wifi_conn_ip_tv.setText(IPUtil.intToIp(mWifiManager.getConnectionInfo().getIpAddress()));
			builder.setContentView(mView);
			builder.setPositiveButton("断开",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSWifiManager.disconnectWifi();
							dialog.cancel();
						}
					});
			builder.setNeutralButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.create().show();
		} else if (!connected && saved) {// 已保存
			View mView = View.inflate(this, R.layout.wifi_content_save,null);
			TextView wifi_save_ssid_tv = (TextView) mView.findViewById(R.id.wifi_save_ssid_tv);
			wifi_save_ssid_tv.setText(ap.getSsid());
			builder.setContentView(mView);
			builder.setPositiveButton("连接",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSWifiManager.connectWifi(ap.getSsid());
							dialog.cancel();
						}
					});
			builder.setNeutralButton("忘记",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mWifiManager.removeNetwork(ap.getNetworkId());
							mWifiManager.saveConfiguration();
							dialog.cancel();
						}
					});
			builder.create().show();
		} else if (!saved) {//未保存
			if(!haspassword){//如果是开放网络,则直接进行连接
				mSWifiManager.clear();// 连接之前，先清空一下之前的配置信息
				if (mSWifiManager.setSSID(ap.getSsid())) {
					mSWifiManager.setMode("dhcp");
					// 忘记ssid相同并且不在信号范围内的
					final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
					if (configs != null) {
						for (WifiConfiguration config : configs) {
							if (config.SSID != null&& AccessPoint.removeDoubleQuotes(config.SSID).equals(ap.getSsid())) {
								mWifiManager.forgetNetwork(config.networkId);
							}
						}
					}
					mSWifiManager.connectWifi(ap.getSsid());
				}
			}else{
				View mView = View.inflate(this,R.layout.wifi_content_unconn, null);
				TextView wifi_unconn_ssid_tv = (TextView) mView.findViewById(R.id.wifi_unconn_ssid_tv);
				final EditText wifi_unconn_pass_et = (EditText) mView.findViewById(R.id.wifi_unconn_pass_et);
				CheckBox wifi_unconn_showpass_cb = (CheckBox) mView.findViewById(R.id.wifi_unconn_showpass_cb);
				wifi_unconn_pass_et.setTransformationMethod(PasswordTransformationMethod.getInstance());// 默认密码显示为密文
				wifi_unconn_ssid_tv.setText(ap.getSsid());
				wifi_unconn_showpass_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView,
									boolean isChecked) {
								if (isChecked) {
									wifi_unconn_pass_et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
								} else {
									wifi_unconn_pass_et.setTransformationMethod(PasswordTransformationMethod.getInstance());
								}
								wifi_unconn_pass_et.postInvalidate();
							}
						});
				builder.setContentView(mView);
				builder.setPositiveButton("连接",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mSWifiManager.clear();// 连接之前，先清空一下之前的配置信息
									if (haspassword) {
										String pass = wifi_unconn_pass_et.getText().toString();
										if (checkPassword(pass, ap)) {
											mSWifiManager.setPassword(pass);
										} else {
											Message msg = new Message();
											msg.what = SWifiManager.SEND_MSG_TO_STATUSBAR;
											// "The digits of the password is wrong,please check!";
											msg.obj = "您的密码输入不合法。";
											mSWifiManager.sendMsg(msg);
											return;
										}
									}
									if (mSWifiManager.setSSID(ap.getSsid())) {
										String mod = "dhcp";
										mSWifiManager.setMode(mod);
										// 忘记ssid相同并且不在信号范围内的
										final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
										if (configs != null) {
											for (WifiConfiguration config : configs) {
												if (config.SSID != null && AccessPoint.removeDoubleQuotes(config.SSID).equals(ap.getSsid())) {
													mWifiManager.forgetNetwork(config.networkId);
												}
											}
										}
										mSWifiManager.connectWifi(ap.getSsid());
									}
								dialog.cancel();
							}
						});
				builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
				WiFiDialog dialog = builder.create();
				dialog.show();
			}
		}
	}
	
	private boolean checkPassword(String pass, AccessPoint ap) {
		boolean isCorrected = false;
		int length = pass.length();
		if (pass != null && !pass.equals("")) {
			switch (ap.getSecurity()) {
			case AccessPoint.SECURITY_WEP:
				if (length == 5 || length == 10 || length == 13 || length == 26
						|| length == 16 || length == 32) {
					isCorrected = true;
				}
				break;
			case AccessPoint.SECURITY_PSK:
				if (length >= 8 && length <= 64) {
					isCorrected = true;
				}
				break;
			default:
				isCorrected = true;
			}
		}
		return isCorrected;
	}
	
	@Override
	protected void onResume() {
		registerReceiver(mReceiver, mFilter);
		super.onResume();
	}
	@Override
	protected void onPause() {
		unregisterReceiver(mReceiver);
		super.onPause();
	}
}
