package com.shenma.tvlauncher.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.shenma.tvlauncher.tvlive.network.LiveConstant;


public class Md5Encoder {
	
	public static String getIP() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement(); // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

	public static String encode(String password){
		try {
			if(null==password||"null".equals(password)){
				password = "wephd_live_new";
			}
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i= 0 ;i<result.length;i++){
				int number = result[i]&0xff;//
				String str = Integer.toHexString(number);
				if(str.length()==1){
					sb.append("0");
					sb.append(str);
				}else{
					sb.append(str);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			//CNA'T REACH;
			return "";
		}
	}
	
	public static String getEncode2Key(String key2){
		String ret = "";
		key2 = Integer.parseInt(key2)+9000+""; 
		String key1 = LiveConstant.LIVE_KEY1;
		String key3 = LiveConstant.LIVE_KEY3;
		String key4 = getIP();
		Logger.i("joychang", "key1="+key1+"---key2="+key2+"---key3="+key3+"---key4="+key4);
		ret = key2+"_"+encode(key1+","+key2+","+key3+","+"119.255.53.130");
		return ret;
	}
}
