package com.shenma.tvlauncher.tvlive.parsexml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.shenma.tvlauncher.tvlive.network.LiveConstant;
import com.shenma.tvlauncher.tvlive.network.DownLoadTools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class DonLodeParseXmlWifiTask extends AsyncTask<String, String, Integer> {

	private onConnectDoneListener callback;

	private Handler mHandler;

	private Context mContext;

	Object strResult;

	public DonLodeParseXmlWifiTask(onConnectDoneListener callback,
			Handler mHandler, Context mContext) {
		super();
		this.callback = callback;
		this.mHandler = mHandler;
		this.mContext = mContext;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = params[0];
		String localAdrrString = mContext.getFilesDir() + File.separator
				+ params[1];
		DownLoadTools mDownLoadTools = new DownLoadTools(mHandler);
		mDownLoadTools.getNetXml(localAdrrString, url,
				mContext.getApplicationContext(), 0);
		return 201;
	}

	private boolean isToday(File filePath) {
		long oneDaysTime = 12 * 3600;
		if (System.currentTimeMillis() - filePath.lastModified() >= oneDaysTime) {
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "resource", "unused" })
	private Bitmap fileToBitmap(File mfFile) {
		if (mfFile == null || !mfFile.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(mfFile);
			if (fis != null) {
				int len = fis.available();
				byte[] bytes = new byte[len];
				fis.read(bytes);

				if (bytes.length != 0) {
					return BitmapFactory
							.decodeByteArray(bytes, 0, bytes.length);
				} else {
					return null;
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void downLoadImg(String url, String localAdrrString) {
		try {
			URL urlImg = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlImg
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

			if (bitmap != null) {
				saveFile(bitmap, localAdrrString);
			}

			sendMessage(LiveConstant.DOWNLOAD_IMG_XML, bitmap);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void sendMessage(int what, Bitmap mBitmap) {
		if (mBitmap == null)
			return;
		Message msg = new Message();
		msg.what = LiveConstant.DOWNLOAD_ENTER_XML;
		msg.obj = mBitmap;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onPostExecute(Integer result) {
		callback.onConnectDone(result, strResult);
	}

	public void saveFile(Bitmap bm, String fileName) throws IOException {
		File CaptureFile = new File(fileName);
		if (!CaptureFile.exists()) {
			CaptureFile.createNewFile();
		} else {
			CaptureFile.delete();
			CaptureFile.createNewFile();
		}
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(CaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}
}
