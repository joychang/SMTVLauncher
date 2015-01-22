package com.shenma.tvlauncher.tvlive.parsexml;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class PrintLog {
	Process mLogcatProc = null;
	BufferedReader reader = null;
	DataOutputStream os1 = null;
	public boolean isExit = false;

	public void printLog() {// {"logcat", "-d",
							// "AndroidRuntime:E [ÄãµÄÓŠÓÃTAG±êÊŸ·û]:V *:S" });
		submit();

	}

	private void submit() {
		try {
//			String[] commands = { "logcat -d > /mnt/sda/log.txt","dumpstate > /mnt/sda/log1.txt" };
//			String[] commands = { "logcat -d -v time > /mnt/sdcard/log.txt","dumpstate > /mnt/sdcard/log1.txt" };
			 String[] commands = { "logcat -d -v time > /mnt/sda/error.txt" };
			Process p = Runtime.getRuntime().exec("/system/bin/sh -");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			for (String tmpCmd : commands) {
				os.writeBytes(tmpCmd + "\n");
			}
		} catch (IOException e) {
			Log.e("feibing","error="+e.toString());
		}
	}

}
