package com.forcetech.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Log;

public class ForceTV {
	private boolean p2pIsStart = false;

	public void initForceClient() {
		try {
			Process process = Runtime.getRuntime().exec("netstat");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()), 1024);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("0.0.0.0:9906"))
					p2pIsStart = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!p2pIsStart)
			Log.d("jni", String.valueOf(start(9906, 20 * 1024 * 1024)));
	}
	public native int start(int port, int size);

	public native int stop();

	static {
		System.loadLibrary("forcetv");
	}
}
