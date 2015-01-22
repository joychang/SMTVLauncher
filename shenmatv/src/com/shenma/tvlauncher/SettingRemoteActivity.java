package com.shenma.tvlauncher;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.RemoteServer;
import com.shenma.tvlauncher.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class SettingRemoteActivity extends BaseActivity{
	
	private TextView remote_installation_content_url_tv;
	private int port = 10101;
	private RemoteServer server;
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getData().getSchemeSpecificPart();
			String mPackName = server.mPackName;
			Logger.d("zhouchuan", "packageName="+packageName+"\t mPackName="+mPackName);
			if(packageName.equals(mPackName)) {
				server.setSuccessResult(1, packageName);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_remote);
		findViewById(R.id.setting_remote).setBackgroundResource(R.drawable.video_details_bg);
		initView();
	}

	private void registerPackageReceiver(){
		//注册广播
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mFilter.addDataScheme("package");
		registerReceiver(mReceiver, mFilter);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Logger.d("zhouchuan", "打开httpserver服务");
		server.start(port);
		registerPackageReceiver();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Logger.d("zhouchuan", "关闭httpserver服务");
		server.stop();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void initView() {
		findViewById();
		initData();
	}

	private void initData(){
		String str = getResources().getString(R.string.remote_installation_help_info);
		String ip = Utils.localipget();
		String helpInfo = str + "\thttp://" + ip + ":" + port;
		remote_installation_content_url_tv.setText(helpInfo);
		server = new RemoteServer(context);
		
	}
	
	@Override
	protected void loadViewLayout() {
		
	}

	@Override
	protected void findViewById() {
		remote_installation_content_url_tv = (TextView) findViewById(R.id.remote_installation_content_url_tv);
	}

	@Override
	protected void setListener() {
		
	}

	
	
}
