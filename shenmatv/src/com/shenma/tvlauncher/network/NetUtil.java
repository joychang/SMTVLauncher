package com.shenma.tvlauncher.network;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.shenma.tvlauncher.utils.Logger;

/**
 * 
 * @author joychang
 * 
 */
public class NetUtil {
	public static BasicHeader[] headers = new BasicHeader[10];
	static {
		
		 headers[0] = new BasicHeader("Appkey", "12343"); 
		 headers[1] = new BasicHeader("Udid", "");
		 //手机串号
		 headers[2] = new BasicHeader("Os","android");
		 headers[3] = new BasicHeader("Osversion", "");
		 headers[4] = new BasicHeader("Appversion", "");
		 //1.0 
		 headers[5] = new BasicHeader("Sourceid", "");
		 headers[6] = new BasicHeader("Ver", ""); 
		 headers[7] = new BasicHeader("Userid", ""); 
		 headers[8] = new BasicHeader("Usersession", ""); 
		 headers[9] = new BasicHeader("Unique", "");
		 
	}
	
	/***
	 * @brief http post命令函数。
	 * @param vo
	 * @return
	 */
	public static String post(final String url, final List<NameValuePair> param) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		HttpParams params = new BasicHttpParams();//
		params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 8000); // 连接超时
		HttpConnectionParams.setSoTimeout(params, 5000); // 响应超时
		post.setParams(params);
		// post.setHeader("Accept-Ranges", "bytes");
		Object obj = null;
		try {
			if (null != param) {
				HttpEntity entity = new UrlEncodedFormEntity(param, "UTF-8");
				post.setEntity(entity);
			}
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String jsonStr = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				// TODO:
				Logger.d(TAG, jsonStr);
				return jsonStr;
			}
		} catch (ClientProtocolException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} finally {
		}
		return null;
	}
	
	/***
	 * @brief http post命令函数。
	 * @param vo
	 * @return
	 */
	public static String get(final String url) {
		String str;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpParams params = new BasicHttpParams();//
		params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 8000); // 连接超时
		HttpConnectionParams.setSoTimeout(params, 5000); // 响应超时
		get.setParams(params);
		get.setHeader("User-Agent", " Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko)");
		//post.setHeader("Accept-Ranges", "bytes");
		Object obj = null;
		try {
			HttpResponse response = client.execute(get);// 包含响应的状态和返回的结果==
			int code =  response.getStatusLine().getStatusCode();
			if ( code == HttpStatus.SC_OK) {
				String jsonStr = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				// TODO:
				Logger.d(TAG, jsonStr);
				return jsonStr;
			}
		} catch (ClientProtocolException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} catch (Exception e){
			Logger.e(TAG, e.getLocalizedMessage());
		}finally {
			str =  null;
		}
		return null;
	}

	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {  
        // 自定义响应处理  
        public String handleResponse(HttpResponse response)  
                throws ClientProtocolException, IOException {  
            HttpEntity entity = response.getEntity();  
            if (entity != null) {  
                String charset = EntityUtils.getContentCharSet(entity) == null ? CHARSET_ENCODING  
                        : EntityUtils.getContentCharSet(entity);  
                return new String(EntityUtils.toByteArray(entity), charset);  
            } else {  
                return null;  
            }  
        }  
    };  

	/**
	 * @brief 日志标记。
	 */
	private static String TAG = NetUtil.class.getSimpleName();
	
	public static String CHARSET_ENCODING = "UTF-8";
}
