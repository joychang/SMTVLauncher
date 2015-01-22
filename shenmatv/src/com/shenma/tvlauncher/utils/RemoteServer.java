package com.shenma.tvlauncher.utils;

import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.json.JSONException;
import org.json.JSONObject;

import com.shenma.tvlauncher.R;

import net.sunniwell.android.httpserver.HttpServer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class RemoteServer {

	public static final int REMOTE_INSTALLATION_START_INSTALL = 1001;
	public static final int REMOTE_INSTALLATION_INSTALL_RESULT = 1002;
	public String mPackName = null;
	private Context mContext;
	private HttpServer server;
	private UploadFileHandler upLoadFileHandler;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String apkName = null;
			String packageName = null;
			switch (msg.what) {
				case REMOTE_INSTALLATION_START_INSTALL:
					String uri = (String) msg.obj;
					startInstallAPK(uri);
					// -----将开始安装的信息显示给用户----------
					mPackName = Utils.getPackageName(mContext, uri);
					apkName = Utils.getAppNameByApkFile(mContext, uri);
					showPromptMessage(REMOTE_INSTALLATION_START_INSTALL, apkName, true);
					break;
				case REMOTE_INSTALLATION_INSTALL_RESULT:
					boolean result = false;
					if (msg.arg1 == 1)
						result = true;
					packageName = (String) msg.obj;
					// --------------------------------------------
					apkName = Utils.getApkName(mContext, packageName);
					showPromptMessage(REMOTE_INSTALLATION_INSTALL_RESULT, apkName, result);
					// --------------------------------------------
					JSONObject json = new JSONObject();
					if (result) {
						//安装成功
						try {
							json.put("installation", true);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else {
						//安装失败
						try {
							json.put("installation", false);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					try {
						upLoadFileHandler.writeToHTML(json.toString());
						UploadFileHandler.isInstall = false;
					} catch (Exception e) {
						
					}
					break;
			}
		};
	};
	
	public void setSuccessResult(int result,String packageName) {
		Message msg = mHandler.obtainMessage();
		msg.arg1 = result;
		msg.obj = packageName;
		msg.what = REMOTE_INSTALLATION_INSTALL_RESULT;
		mHandler.sendMessageDelayed(msg, 1000);
	}
	
	public RemoteServer(Context context) {
		super();
		this.mContext = context;
	}
	
	/**
	 * 开始httpserver服务
	 * @param port 端口号
	 */
	public void start(int port) {
		try {
			server = new HttpServer(port);
			HttpRequestHandlerRegistry registry = server.getHttpRequestHandlerRegistry();
			upLoadFileHandler = new UploadFileHandler(mHandler,mContext);
			registry.register("/upload.action", upLoadFileHandler);
			registry.register("*", new HttpFileHandler(mContext));
			server.start();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 关闭httpserver服务
	 */
	public void stop() {
		server.stop();
	}
	
	private void startInstallAPK(final String uri) {
		Thread installThread = new Thread() {
			@Override
			public void run() {
				super.run();
				AppManager appManager = new AppManager(mContext);
				appManager.install(uri);//调用系统的安装程序
				//使用静默安装程序
//				appManager.install(uri, new StatusCallBack() {
//
//					@Override
//					public void onResult(boolean result, String packageName) {
//						Message message = mHandler.obtainMessage();
//						message.what = REMOTE_INSTALLATION_INSTALL_RESULT;
//						if (result) {
//							message.arg1 = 1;
//						} else {
//							message.arg1 = 0;
//						}
//						message.obj = packageName;
//						mHandler.sendMessage(message);
//						Logger.d("zhouchuan","------安装完成的apk的包名--------------------------packageName=" + packageName);
//					}
//				});
			}
		};
		installThread.start();
	}
	
	/**
	 * 安装apk开始和结束时给用户提示
	 * @param state 开始或结束
	 * @param apkName apk的名称
	 * @param result 安装结束成功或失败
	 */
	private void showPromptMessage(int state, String apkName, boolean result) {
		switch (state) {
			case REMOTE_INSTALLATION_START_INSTALL:
				Utils.showToast(mContext, apkName+"开始安装", R.drawable.toast_smile);
				break;
			case REMOTE_INSTALLATION_INSTALL_RESULT:
				if(result){
					Utils.showToast(mContext, apkName+"安装成功", R.drawable.toast_smile);
				}else {
					Utils.showToast(mContext, apkName+"安装失败", R.drawable.toast_err);
				}
				break;
		}
	}
}
