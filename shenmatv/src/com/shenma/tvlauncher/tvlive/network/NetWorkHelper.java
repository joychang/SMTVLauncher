package com.shenma.tvlauncher.tvlive.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.shenma.tvlauncher.tvlive.domain.PtoPCollection;

import android.widget.Toast;

/**
 * 服务器请求类
 * @author joychang
 *
 */
public class NetWorkHelper {
	
	public static boolean sendServerRequset(PtoPCollection ptopCollection,int position){
		HttpParams mHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mHttpParams, 5000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 5000);
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpGet mHttpGet = new HttpGet(ptopCollection.getServicePath(position));
		try {
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				//Toast.makeText(getApplicationContext(), "没有回应",Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (IOException e) {
			//Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		return true;
	}
	
	
	/**
	 * 在此杀死ptop的so链接库
	 */
	private void killPtoP() {

		HttpParams mHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mHttpParams, 5000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 5000);
		HttpClient mHttpClient = new DefaultHttpClient();
		// Log.e("url", "http://" + getLocalIpAddress()
		// + KILL_PTOP);
		HttpGet mHttpGet = new HttpGet("http://" + getLocalIpAddress()
				+ KILL_PTOP);
		try {
			HttpResponse response = mHttpClient.execute(mHttpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 获取ip地址
	 * 
	 * @return
	 */

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			// Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}
	
	private static final String TAG = "NetWorkHelper";
	public static final String KILL_PTOP = ":9906/api?func=stop_all_chan";

}

