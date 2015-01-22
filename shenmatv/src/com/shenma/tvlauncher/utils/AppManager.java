package com.shenma.tvlauncher.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;

public class AppManager {
	private PackageManager pm;
	private Context mContext;
	
	public AppManager(Context context) {
		super();
		this.mContext = context;
		pm = mContext.getPackageManager();
	}
	
	/**
	 * 静默安装apk应用
	 * @param strFile 文件路径
	 * @param callback 返回状态
	 */
	public void install(String strFile, final StatusCallBack callback) {
		Uri mPackageURI = Uri.fromFile(new File(strFile));
		int installFlags = PackageManager.INSTALL_REPLACE_EXISTING;
		IPackageInstallObserver installObserver = new IPackageInstallObserver.Stub() {

			@Override
			public void packageInstalled(String packageName, int returnCode) throws RemoteException {
				if (callback != null) {
					callback.onResult(returnCode == PackageManager.INSTALL_SUCCEEDED, packageName);
				}
			}
		};
		String mPackageName = Utils.getPackageName(mContext, strFile);
		pm.installPackage(mPackageURI, installObserver, installFlags, mPackageName);
	}
	
	/**
	 * 调用Android原声的安装接口,安装apk
	 * 
	 * @param uri 文件路径
	 */
	public void install(String uri) {
//		File f = new File(uri);
//		Intent mIntent = new Intent();
//		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		mIntent.setAction(android.content.Intent.ACTION_VIEW);
//		mIntent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
//		mContext.startActivity(mIntent);
		File updateFile = new File(uri);
		try {
			String[] strs = { "chmod", "604", updateFile.getPath() };
			Runtime.getRuntime().exec(strs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*------------------------*/
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(updateFile),"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
