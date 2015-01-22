package com.shenma.tvlauncher.tvlive.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.shenma.tvlauncher.utils.Logger;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

/**
 * @brief 此方法判断服务器资源包是否更新,并操作对应更新内容
 * @author zengwenman
 */

public class DownloadResourse {
	static private String TAG = "DownloadResourse";
	private Context context = null;
	private UpdateList updateList = null;
	private FileName fileName = null;

	/****************/
	public static final int UPDATE = 0x00;

	private Handler mHandler=null;

	public DownloadResourse(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler=mHandler;
		updateList = new UpdateList();
		fileName = new FileName();
	}

	public DownloadResourse() {

	}

	private boolean checkNetStatus() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (netInfo != null) {
			return true;
		} else {
			return false;
		}
	}

	public void isUpdate() {
		if (checkNetStatus()) {
			new LoadXmlThread().start();
		} else {
			Toast.makeText(context, "net link erro", Toast.LENGTH_LONG).show();
		}
	}

	class LoadXmlThread extends Thread {
		@Override
		public void run() {
			super.run();
			updateList.version = getVersion();
			try {
				File fi=new File(context.getFilesDir()+File.separator,FileName.ZIP_VERSION);
				if(!fi.exists()){
					fi.createNewFile();
				}
				FileInputStream fis=new FileInputStream(fi);
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
				String res = EncodingUtils.getString(buffer, HTTP.UTF_8);
				// downloadZip();
				if (res.equals("") || res == null) {
					// 更新在线资源包
					mHandler.sendEmptyMessage(LiveConstant.PLUGS_START);
					downloadZip();
				} else {
					if (Float.parseFloat(res) < updateList.version) {
						// 更新在线资源包
						mHandler.sendEmptyMessage(LiveConstant.PLUGS_START);
						downloadZip();
					} else {
						// 不更新包
						//通知插件更新结束
						mHandler.sendEmptyMessage(LiveConstant.PLUGS_END);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 加载在线资源包
	 */
	private void downloadZip() {
		try {
			FileOutputStream fos=context.openFileOutput(FileName.ZIP_VERSION, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);

			Logger.d(TAG, "fileName.zipVersionFile->"
					+ fileName.zipVersionFile + "\n" + "updateList.version->"
					+ updateList.version);

			byte[] buffer = new String(updateList.version + "").getBytes();
			fos.write(buffer);
			fos.close();
			new Thread() {
				@Override
				public void run() {
					super.run();
					try {
						// 解压zip文件
						tarZip(getStreamFromZip(updateList.link));
						//通知插件更新结束
						mHandler.sendEmptyMessage(LiveConstant.PLUGS_END);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压文件内容
	 * 
	 * @param is
	 *            压缩包文件流
	 * @throws IOException
	 */
	private void tarZip(InputStream is) throws IOException {
		ZipInputStream zis = new ZipInputStream(is);
		try {
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					continue;
				}
				String filename = ze.getName();
				String[] t = filename.split("/");
				filename = t[t.length - 1];
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1 * 1024];
				int count = -1;
				while ((count = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, count);
				}
				byte[] bytes = bos.toByteArray();
				bos.close();
				FileOutputStream fos=context.openFileOutput(filename,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
				fos.write(bytes);
				if (filename.contains("libutp")
						|| filename.contains("libletvp2p")
						|| filename.contains("so")) {
					File dir = context.getDir("libs", Context.MODE_PRIVATE);
					File soFile = new File(dir + File.separator, filename);
					FileOutputStream libFos = new FileOutputStream(soFile);
					libFos.write(bytes);
					libFos.close();
					SharedPreferences sp = context.getSharedPreferences("shenma", context.MODE_PRIVATE);
					sp.edit().putString(LiveConstant.SO_NAME, filename);

				}
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			zis.close();
		}
	}

	
	
	/**
	 * 获得在线资源包版本号
	 * 
	 * @return
	 */
	public float getVersion() {
		InputStream in = null;
		Document document;
		try {
			//加载更新插件地址
			//in = getStreamFromUrl("http://192.168.1.37/wepower/wepower.xml");
			in = getStreamFromUrl("http://live.lsott.com/wepower/plugs.xml");
			document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(in);
			NodeList nodeList = document.getElementsByTagName("url");
			updateList.link = nodeList.item(0).getAttributes()
					.getNamedItem("link").getTextContent();
			updateList.version = Float.valueOf(nodeList.item(0).getAttributes()
					.getNamedItem("version").getTextContent());
			updateList.description = nodeList.item(0).getAttributes()
					.getNamedItem("description").getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateList.version;
	}

	/**
	 * 获取服务器的xml文件
	 * 
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	public InputStream getStreamFromUrl(String strUrl) throws Exception {
		ByteArrayOutputStream bos = null;

		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(10 * 1000);
			InputStream is = conn.getInputStream();
			// return is;
			bos = new ByteArrayOutputStream();
			int data = -1;
			while ((data = is.read()) != -1)
				bos.write(data);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		InputStream inStream = new ByteArrayInputStream(bos.toString()
				.getBytes(HTTP.UTF_8));

		return inStream;
	}

	/**
	 * 服务器获取zip压缩包
	 * 
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	public InputStream getStreamFromZip(String strUrl) throws Exception {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(10 * 1000);
			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @brief 服务器获取更新资源数据
	 * @author zengwenman
	 */
	class UpdateList {
		String link = null;
		float version = 0.0f;
		String description = null;
	}

	/**
	 * @brief 本地文件名
	 * @author zengwenman
	 * 
	 */
	class FileName {
		File rootFile = null;
		File zipVersionFile = null;
		File zipFile = null;
		File zipContentFile = null;
		File letvSo = null;
		File playJarFile = null;
		File tempFile = null;
		File wepowerFile = null;
		static final String PATH_ROOT = "sdcard";
		static final String PATH_WEPOWER = "wepower";
		static final String WEPOWER_SO = "so";
		static final String ZIP_VERSION = "version.txt";
		static final String ZIP_NAME = "wepower.zip";
		static final String LETV_SO = "libutp.so";
		static final String PLAY_JAR = "play.jar";
		static final String WEIBAO = "weibao";
	}

}
