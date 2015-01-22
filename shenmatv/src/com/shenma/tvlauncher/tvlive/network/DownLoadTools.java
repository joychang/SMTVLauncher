package com.shenma.tvlauncher.tvlive.network;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

public class DownLoadTools {
	public Handler mHandler = null;

	public DownLoadTools(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public synchronized void getNetXml(String filePath, String url,
			Context context,int msg) {
		File dataFile = new File(filePath);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (isNetConnected(context)) {
			String dataXmls = "";
			try {
				dataXmls = getNetXmlContent(url, context,msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!dataXmls.equals("")) {
				writeDataToXml(dataFile, dataXmls, context, msg);
				break;
			}
		} else {
			sendException(mHandler,
					"The network is not connected, please connect to the network!");
		}
	}

	public static boolean isNetConnected(Context context) {
		ConnectivityManager conManger = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManger.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getNetXmlContent(String url, Context context,int msg)
			throws IOException {
		URL temUrl;
		String html = "";
		temUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) temUrl.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(10 * 1000);
		InputStream inStream = conn.getInputStream();
		byte[] data = readFromInput(inStream, context);
		html = new String(data, "UTF-8");

		return html;

	}

	public static byte[] readFromInput(InputStream inStream, Context context)
			throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		outStream.close();

		return outStream.toByteArray();
	}

	public void writeDataToXml(File file, String data, Context context,int msg) {

		BufferedWriter bfWriter;
		try {
			bfWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			try {
				bfWriter.write(data);
				bfWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
				sendException(mHandler, "Read or Writer Exception !");
				return;
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			sendException(mHandler, "Unsupported Encoding !");
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sendException(mHandler, "File Not Find!");
			return;

		}

		mHandler.sendEmptyMessage(msg);

	}

	private void sendException(Handler handle, String exceptionType) {
		Message exceptionMsg = new Message();
		exceptionMsg.what = LiveConstant.APPLICATION_EXCEPTION;
		exceptionMsg.obj = exceptionType;
		handle.sendMessage(exceptionMsg);

	}
}
