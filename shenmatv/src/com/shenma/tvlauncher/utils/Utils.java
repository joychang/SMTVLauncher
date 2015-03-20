package com.shenma.tvlauncher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.view.LiveLoadingDialog;
import com.shenma.tvlauncher.view.LoadingDialog;
import com.shenma.tvlauncher.vod.domain.VodUrl;
import com.wepower.live.parser.ILetv;
import com.wepower.live.parser.IPlay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Description 应用工具类
 * @author joychang
 * 
 */
public class Utils{
	
	/**
	 * 最省内存的方式读取本地图片
	 * @param context
	 * @param resId
	 * @return
	 */
    public static Bitmap readBitMap(Context context, int resId) {  
        BitmapFactory.Options opt = new BitmapFactory.Options();  
        opt.inPreferredConfig = Bitmap.Config.RGB_565;  
        opt.inPurgeable = true;  
        opt.inInputShareable = true;  
        // 获取资源图片  
        InputStream is = context.getResources().openRawResource(resId);  
        return BitmapFactory.decodeStream(is, null, opt);  
    }
	
	
	/**
	 * 读取配置文件 3.20
	 */
	public static String getFormInfo(Class c,int i) {
		String frominfo = "";
		Properties prop = new Properties();
		try {
			prop.load(c.getResourceAsStream("/assets/config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String from[] = ((String) prop.get("from")).split("[|]");
		frominfo = from[i];
		return frominfo;

	}
	
	IPlay iplay = new IPlay() {
		
		@Override
		public String returnPlayUrl(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String returnIP() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	/**
	 * @brief 显示函数。
	 * @author joychang
	 * @param[in] context 上下文。
	 * @param[in] message 内容文字。
	 * @note 显示加载对话框处理。
	 */
	public static void loadingShow(Context context, String message) {
		if (lDialog != null) {
			loadingClose();
			//return;
			// 关闭加载框
		}

		// 使用以下方式显示对话框，按返回键可关闭对话框
		lDialog = new LoadingDialog(context, message);
		lDialog.setMessage(message);
		// lDialog.setIndeterminate(true);
		lDialog.setCancelable(true);
		lDialog.setCanceledOnTouchOutside(false);
		lDialog.show();
	}

	/**
	 * @brief 关闭函数。
	 * @author joychang
	 * @note 关闭加载对话框处理。
	 */
	public static void loadingClose() {
		if (null != lDialog) {
			lDialog.dismiss();
			lDialog.cancel();
			lDialog = null;
		} else {
			Logger.w(TAG, "close(): LoadingDialog is not showing");
		}

		Log.d(TAG, "LoadingDialog close() end");
	}

	/**
	 * 获得网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			//showNetDialog(context);
			return false;
		}
		return true;
	}

	/**
	 * 安装一个apk文件
	 * 
	 * @param file
	 */
	public static void installApk(File file, Context context) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 获取当前应用版本号
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String getVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return 时间字符串 24小时制
	 * @author drowtram
	 */
	public static String getStringTime(String type) {
		Time t = new Time();
		t.setToNow(); // 取得系统时间。
		String hour = t.hour < 10 ? "0" + (t.hour) : t.hour + ""; // 默认24小时制
		String minute = t.minute < 10 ? "0" + (t.minute) : t.minute + "";
		return hour + type + minute;
	}
	
	
	/**
	 * 自定义Toast
	 * @param context
	 * @param text
	 * @param image
	 */
	public static void showToast(Context context,String text,int image){
		Long end = java.lang.System.currentTimeMillis();;
		Logger.d(TAG, "start="+start);
		Logger.d(TAG, "end="+end);
		if(end-start<1000 && text.equals(mtext)){
			return;
		}else if(end-start<2000 && text.equals(mtext)){
			View view = LayoutInflater.from(context).inflate(R.layout.tv_toast, null);
			TextView tv_toast = (TextView) view.findViewById(R.id.tv_smtv_toast);
			ImageView iv_toast = (ImageView) view.findViewById(R.id.iv_smtv_toast);
			tv_toast.setText("你也太无聊了吧...");
			iv_toast.setBackgroundResource(image);
			Toast toast = new Toast(context);
			toast.setView(view);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
			start = end;
		}else{
			start = end;
			View view = LayoutInflater.from(context).inflate(R.layout.tv_toast, null);
			TextView tv_toast = (TextView) view.findViewById(R.id.tv_smtv_toast);
			ImageView iv_toast = (ImageView) view.findViewById(R.id.iv_smtv_toast);
			tv_toast.setText(text);
			iv_toast.setBackgroundResource(image);
			Toast toast = new Toast(context);
			toast.setView(view);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		}
		mtext = text;
	}
	
	/**
	 * 显示土司
	 * fix the toast Repeat display by zhouchuan 
	 * @param context
	 * @param text
	 * @param image
	 */
	public static void showToast(String text,Context context,int image){
		View view = null;
		if(toast == null) {
			toast = new Toast(context);
			view = LayoutInflater.from(context).inflate(R.layout.tv_toast, null);
		} else {
			view = toast.getView();
		}
		TextView tv_toast = (TextView) view.findViewById(R.id.tv_smtv_toast);
		ImageView iv_toast = (ImageView) view.findViewById(R.id.iv_smtv_toast);
		tv_toast.setText(text);
		iv_toast.setBackgroundResource(image);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * 显示土司
	 * fix the toast Repeat display by zhouchuan 
	 * @param context
	 * @param text
	 * @param image
	 */
	public static void showToast(Context context,int text,int image){
		View view = null;
		if(toast == null) {
			toast = new Toast(context);
			view = LayoutInflater.from(context).inflate(R.layout.tv_toast, null);
		} else {
			view = toast.getView();
		}
		TextView tv_toast = (TextView) view.findViewById(R.id.tv_smtv_toast);
		ImageView iv_toast = (ImageView) view.findViewById(R.id.iv_smtv_toast);
		tv_toast.setText(text);
		iv_toast.setBackgroundResource(image);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	/**
	 * menu主界面目录
	 * @return 
	 * 
	 * @return
	 */
	public static ArrayList<String> getUserData(int type) {
		ArrayList<String> list = new ArrayList<String>();
		list.add("删除");
		list.add("全部清空");
//		if (type == REMOVE_FROM_FAV) {
//			list.add("从常用中删除");
//			list.add("卸载");
//		} else if (type == ADD_TO_FAV) {
//			list.add("添加到常用");
//			list.add("卸载");
//		}
		
		return list;
	}
	
	/**************************回看工具函数*******************************/
	
	
    /**
     * @brief  判断该链接是否为空
     * @author joychang
     * @param  strLink   链接地址
     * @return true      该地址无内容
     * @return false     该地址非空
     */
    public static boolean isValidLink(String strLink){
        Log.d("UiUtil", "isValidLink() start.");
        boolean result = false;
        if(strLink != null && strLink.length() > 0){
            URL url;
            try{
                url = new URL(strLink);
                HttpURLConnection connt = (HttpURLConnection)url.openConnection();
                connt.setConnectTimeout(5 * 1000);
                connt.setRequestMethod("HEAD");
                int code = connt.getResponseCode();
                if(code == 404){
                    result = true;
                }
                connt.disconnect();
            }
            catch(Exception e){
                result = true;
            }
        }
        else {
            result = true;
        }
        Log.d("UiUtil", "isValidLink() end.");
        return result;
    }
    
    
    /**
     * 获取加密字符
     * @param url
     * @return
     */
	public static String getMd5Pass(final String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpParams params = new BasicHttpParams();//
		params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000); // 连接超时
		HttpConnectionParams.setSoTimeout(params, 3000); // 响应超时
		get.setParams(params);
		// post.setHeader("Accept-Ranges", "bytes");
		Object obj = null;
		try {
			HttpResponse response = client.execute(get);// 包含响应的状态和返回的结果==
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String jsonStr = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				JSONObject jo = new JSONObject(jsonStr);
				jsonStr = jo.getString("stime");
				// TODO:
				Logger.d(TAG, jsonStr);
				return jsonStr;
			}
		} catch (ClientProtocolException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.e(TAG, e.getLocalizedMessage());
		} catch (Exception e) {
			Logger.e(TAG, e.getLocalizedMessage());
		}finally {
			
		}
		return "";
	}
    
    /**
     * @brief     中文字符串转换函数。
     * @author    joychang
     * @param[in] str 要转换的字符串。
     * @param[in] charset 字符串编码。
     * @return    UTF8形式字符串。
     * @note      将str中的中文字符转换为UTF8编码形式。
     * @throws    UnsupportedEncodingException 不支持的字符集
     */
    public static String encode(String str, String charset) throws UnsupportedEncodingException {
        Log.d(TAG, "_encode() start");

        String result = null;

        if ((str != null) && (charset != null)) {
            try {
                Pattern p = Pattern.compile(DEF_ZH_PATTERN, 0);
                Matcher m = p.matcher(str);

                StringBuffer b = new StringBuffer();
                while (m.find()) {
                    m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
                }

                m.appendTail(b);

                result = b.toString();
            }
            catch (PatternSyntaxException e) {
                e.printStackTrace();
            }
        }
        else {
            if (str == null) {
                Log.e(TAG, "encode(): str is null");
            }

            if (charset == null) {
                Log.e(TAG, "encode(): charset is null");
            }
        }

        Log.d(TAG, "encode() end");

        return result;
    }
    
    /**
     * @brief     显示函数。
     * @author    joychang
     * @param[in] context 上下文。
     * @param[in] title   标题文字。
     * @param[in] message 内容文字。
     * @note      显示加载对话框处理。
     */
    public static void loadingShow_tv(Context context,int message) {
        Log.d(TAG, "show() start");
		if (Loadingdialog != null && Loadingdialog.isShowing()) {
			Loadingdialog.dismiss();
			//Loadingdialog = null;
		}
        //使用以下方式显示对话框，按返回键可关闭对话框
		Loadingdialog = new LiveLoadingDialog(context);
		Loadingdialog.setLoadingMsg(message);
		Loadingdialog.setCanceledOnTouchOutside(false);
		Loadingdialog.show();
        Log.d(TAG, "show() end");
    }

    /**
     * @brief  关闭函数。
     * @author joychang
     * @note   关闭加载对话框处理。
     */
    public static void loadingClose_Tv() {
        Log.d(TAG, "close() start");
        if (Loadingdialog != null) {
        	Loadingdialog.cancel();
        	Loadingdialog = null;
        }
        else {
            Log.w(TAG, "close(): mDialog is not showing");
        }

        Log.d(TAG, "close() end");
    }

    /**
     * @brief  判断加载对话框是否显示函数。
     * @author joychang
     * @return true  对话框显示中。
     * @return false 对话框非显示中。
     */
    public static boolean isShowing() {
        Log.d(TAG, "isShowing() start");

        boolean result = false;

        if (Loadingdialog != null) {
            result = true;
        }

        Log.d(TAG, "isShowing() end");

        return result;
    }
    /**
     * 根据日期获取对应的星期
     * @param mdate
     * @return 星期
     */
    public static String getWeekToDate(String mdate){
    	String week = null;
    	mdate = mdate.replace("/", "-");
//    	mdate = mdate.replace("月", "-");
//    	mdate = mdate.replace("日", "");
//    	mdate = "2014-"+mdate;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	try {
			Date date = sdf.parse(mdate);
			week = getWeek(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return week;
    }
    public static String getWeek(Date date){   
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");  
        String week = sdf.format(date);  
        return week;  
    }
    /**
     * 根据节目Url获取节目的时间
     * @param src 节目地址
     * @return 时间
     */
    public static String getTimeToSrc(String src){
    	String time = null;
    	String stringarray[] = src.split("-");
    	String srcnew  = stringarray[0]; 
    	String timenew = srcnew.substring(srcnew.length()-4, srcnew.length());
    	String start = timenew.substring(0, 2);
    	String end = timenew.substring(timenew.length()-2, timenew.length());
    	time = start+":"+end;
    	return time;
    }
    
    /**
     * 根据label名字截取时间
     */
    public static String getTimeToLabel(String label){
    	String time = "";
    	if(label.length()>5){
        	time = label.substring(0,5);
    	}
    	return time; 
    }
    /**
     * 根据label名字截取名字
     * @param lable
     * @return
     */
    public static String getNameToLabel(String label){
    	String name = "";
    	if(label.length()>6){
        	name = label.substring(6, label.length());
    	}
    	return name;
    }
	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	public static String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour,
				minute, second);
	}
	
	private void updateTextViewWithTimeFormat(TextView view, int second){
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		if (0 != hh) {
			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			strTemp = String.format("%02d:%02d", mm, ss);
		}
		view.setText(strTemp);
	}
	
	/**
	 * 从Assets目录下拷贝文件到指定目录
	 * @author drowtram
	 * @param context 上下文对象
	 * @param fileName	Assets目录下的指定文件名
	 * @param path	要拷贝到的目录
	 * @return true 拷贝成功  false 拷贝失败
	 */
	public static boolean copyApkFromAssets(Context context, String fileName, String path) {
		boolean copyIsFinish = false;
		try {
			File f = new File(path);
			if(f.exists()){
				f.delete(); //如果存在这个文件，则删除重新拷贝
			}
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return copyIsFinish;
		}
	
	/**
	 * 获取一个路径的文件名
	 * 
	 * @param urlpath
	 * @return
	 */
	public static String getFilename(String urlpath) {
		return urlpath
				.substring(urlpath.lastIndexOf("/") + 1, urlpath.length());
	}
    
	
	/***************************点播工具***********************************/
	/**
	 * 获取底部的剧集数据
	 * 
	 * @param datas
	 * @param isListData
	 *            list或grid
	 * @return
	 */
	public static List<String> getVideoBottomDatas(List<VodUrl> datas,
			int index, Boolean isList) {
		List<String> gv_list = null;
		if (isList) {
			int j = index * 10;
			for (int i = 0; i < 10; i++) {
				gv_list.add("第" + (j + i + 1) + "集" + "-"
						+ datas.get(j + i).getTitle());
			}
		} else {
			gv_list = new ArrayList<String>();
			datas.size();
			int i = 0;
			int j = datas.size() / 10;
			while (true) {
				if (i < j) {
					gv_list.add(i * 10 + 1 + "-" + (i + 1) * 10);
					i++;
				} else {
					gv_list.add(i * 10 + 1 + "-" + datas.size());
					return gv_list;
				}
			}
		}
		return gv_list;
	}

	public static List<VodUrl> getVideolvDatas(List<VodUrl> datas, int index) {
		List<VodUrl> lv_list = new ArrayList<VodUrl>();
		int j = index * 10;
		if (null != datas && datas.size() - j >= 10) {
			for (int i = 0; i < 10; i++) {
				VodUrl vodurl = new VodUrl();
				vodurl.setTitle("第" + (j + i + 1) + "集" + "-"
						+ datas.get(j + i).getTitle());
				vodurl.setUrl(datas.get(j + i).getUrl());
				lv_list.add(vodurl);
			}
		} else {
			for (int i = 0; i < datas.size() - j; i++) {
				VodUrl vodurl = new VodUrl();
				vodurl.setTitle("第" + (j + i + 1) + "集" + "-"
						+ datas.get(j + i).getTitle());
				vodurl.setUrl(datas.get(j + i).getUrl());
				lv_list.add(vodurl);
			}
		}

		return lv_list;
	}

	public static List<String> getVideogvDatas(List<VodUrl> datas,
			Boolean isList) {
		List<String> gv_list = new ArrayList<String>();
		datas.size();
		int i = 0;
		int j = datas.size() / 10;
		while (true) {
			if (i < j) {
				gv_list.add(i * 10 + 1 + "-" + (i + 1) * 10);
				i++;
			} else if (i * 10 + 1 <= datas.size()) {
				gv_list.add(i * 10 + 1 + "-" + datas.size());
				return gv_list;
			} else {
				return gv_list;
			}
		}
	}
	
	
	/**
	 * <p>
	 * 将字符串使用base64加密
	 * </p>
	 * 
	 * @param url
	 *            路径
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64String(String url) throws Exception {
		return android.util.Base64.encodeToString(url.getBytes(),
				Base64.DEFAULT);
	}

	/**
	 * 筛选条件编码
	 * @param filter
	 * @return
	 */
	public static String getEcodString(String filter){
		String s= "";
		try {
			s = URLEncoder.encode(filter, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return s;
	}
	
	/**
	 * 检测本地apk文件
	 * @author drowtram
	 * @param fileName
	 */
	public static boolean startCheckLoaclApk(Context context,String fileName){
		File file = new File(Constant.PUBLIC_DIR);
		try {
			if(file.exists() && file.isDirectory()){
				File[] files = file.listFiles();
				for (File f : files) {
					String fName = f.getName();
					if (fName.equals(fileName)){
						installApk(context, Constant.PUBLIC_DIR + fName);
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// 改变亮度
	public static void SetLightness(Activity act, int value) {
		try {
			System.putInt(act.getContentResolver(), System.SCREEN_BRIGHTNESS,value);
			WindowManager.LayoutParams lp = act.getWindow().getAttributes();
			lp.screenBrightness = (value <= 0 ? 1 : value) / 255f;
			//lp.screenBrightness = value <= 0 ? 1 : value;
			Logger.d("doBrightnessTouch", "SetLightness===="+lp.screenBrightness);
			act.getWindow().setAttributes(lp);
		} catch (Exception e) {
			Toast.makeText(act, "无法改变亮度", Toast.LENGTH_SHORT).show();
		}
	}

	// 获取亮度
	public static float GetLightness(Activity act) {
		Logger.d("doBrightnessTouch", "GetLightness===="+System.getFloat(act.getContentResolver(),
				System.SCREEN_BRIGHTNESS, -1));
		return System.getFloat(act.getContentResolver(),
				System.SCREEN_BRIGHTNESS, -1)/255f;
//		return System.getInt(act.getContentResolver(),
//				System.SCREEN_BRIGHTNESS, -1);
	}

	// 停止自动亮度调节
	public static void stopAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	// 开启亮度自动调节
	public static void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}
	
	/**
	 * 删除指定目录下所有apk文件
	 * @author drowtram
	 * @param dir
	 */
	public static void deleteAppApks(String dir){
		File file = new File(dir);
		try {
			if(file.exists() && file.isDirectory()){
				File[] files = file.listFiles();
				for (File f : files) {
					String fileName = f.getName();
					if(f.isFile() && fileName.endsWith(".apk")){
						if(f.delete()){
							Log.d("zhouchuan","delete the "+fileName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 下载apk文件进行安装
	 * @author drowtram
	 * @param context
	 * @param mHandler 更新显示进度的handler
	 * @param url
	 */
	public static void startDownloadApk(final Context context, final String url, final Handler mHandler){
		Utils.showToast(context, "正在后台下载，完成后提示安装...", R.drawable.toast_smile);
		new Thread(new Runnable() {
			@Override
			public void run() {
//				File file = new File(Constant.PUBLIC_DIR);
				String apkName = url.substring(url.lastIndexOf("/")+1);
//				File file = new File(context.getCacheDir(),apkName);
				Log.d("zhouchuan", "文件路径"+apkName);
//				if(!file.exists()){
//					file.mkdirs();
//				}
				FileOutputStream fos = null;
				try {
					HttpGet hGet = new HttpGet(url.replaceAll(" ", "%20"));//替换掉空格字符串，不然下载不成功
					HttpResponse hResponse = new DefaultHttpClient().execute(hGet);
					if(hResponse.getStatusLine().getStatusCode() == 200){
						InputStream is = hResponse.getEntity().getContent();
						float downsize = 0;
						if(mHandler != null) {
							//获取下载的文件大小
							float size = hResponse.getEntity().getContentLength();
							mHandler.obtainMessage(1001, size).sendToTarget();//发消息给handler处理更新信息
						}
						fos = context.openFileOutput(apkName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) != -1) {
							if(mHandler != null) {
								downsize += count;
								mHandler.obtainMessage(1002, downsize).sendToTarget();//发消息给handler处理更新信息
							}
							fos.write(buffer, 0, count);
//							Log.d("zhouchuan", "下载进度"+(int)(downsize/size*100)+"%"+" downsize="+downsize+" size="+size);
					    }
						fos.close();
						is.close();
						installApk(context,"/data/data/com.shenma.tvlauncher/files/" + apkName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	
	/**
	 * 安装apk文件
	 * @author drowtram
	 * @param fileName
	 */
	public static void installApk(Context context, String fileName){
		if(getUninatllApkInfo(context, fileName)){
			File updateFile = new File(fileName);
			try {
				String[] args2 = { "chmod", "604", updateFile.getPath() };
				Runtime.getRuntime().exec(args2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*------------------------*/
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(updateFile),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
//			File file = new File(fileName);
//			Intent intent = new Intent();
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setAction(Intent.ACTION_VIEW);     //浏览网页的Action(动作)
//			String type = "application/vnd.android.package-archive";
//			intent.setDataAndType(Uri.fromFile(file), type);  //设置数据类型
//			context.startActivity(intent);
		}else {
			Toast.makeText(context, "文件还没下载完成，请耐心等待。", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 判断apk文件是否可以安装
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static boolean getUninatllApkInfo(Context context, String filePath) {
		boolean result = false;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(filePath,PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
			Log.e("zhouchuan","*****  解析未安装的 apk 出现异常 *****"+e.getMessage(),e);
		}
			return result;
	}
	
	/**
	 * 获取当前日期，包含星期几
	 * @return 日期字符串 xx月xx号 星期x
	 * @author drowtram
	 */
	public static String getStringData(){  
        final Calendar c = Calendar.getInstance();  
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份  
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码  
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
        if("1".equals(mWay)){  
            mWay ="日";  
        }else if("2".equals(mWay)){  
            mWay ="一";  
        }else if("3".equals(mWay)){  
            mWay ="二";  
        }else if("4".equals(mWay)){  
            mWay ="三";  
        }else if("5".equals(mWay)){  
            mWay ="四";  
        }else if("6".equals(mWay)){  
            mWay ="五";  
        }else if("7".equals(mWay)){  
            mWay ="六";  
        }  
        return mMonth + "月" + mDay + "日\n"+"星期"+mWay;  
    }
	
	/**
	 * 获取IP
	 * 
	 * @return
	 */
	public static String localipget() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {}
		return null;
	}
	
	/**
	 * 根据apk路径获取包名
	 * 
	 * @param context
	 * @param strFile
	 *            apk路径
	 * @return apk包名
	 */
	public static String getPackageName(Context context, String strFile) {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(strFile, PackageManager.GET_ACTIVITIES);
		String mPackageName = null;
		if (packageInfo != null) {
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			mPackageName = applicationInfo.packageName;
		}
		return mPackageName;
	}
	
	/**
	 * 根据包名获取apk名称
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return apk名称
	 */
	public static String getApkName(Context context, String packageName) {
		String apkName = null;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			if (packageInfo != null) {
				ApplicationInfo applicationInfo = packageInfo.applicationInfo;
				apkName = pm.getApplicationLabel(applicationInfo).toString();
				// int lable = applicationInfo.labelRes;
				// apkName = context.getResources().getString(lable);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return apkName;
	}
	
	/**
	 * 根据apk文件获取app应用名称
	 * @param context
	 * @param apkFilePath  apk文件路径
	 * @return
	 */
	public static String getAppNameByApkFile(Context context, String apkFilePath) {
		String apkName = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
		if (packageInfo != null) {
			apkName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
		}
		return apkName;
	}
	
	private static long start = 0;
	
	private final static String DEF_ZH_PATTERN = "[\u4e00-\u9fa5]+";
    /**
     * @brief 对话框。
     */
    private static LiveLoadingDialog Loadingdialog = null;

	/**
	 * @brief 小马加载。
	 */
	private static LoadingDialog lDialog = null;

	/**
	 * @brief TAG
	 */
	private static final String TAG = "Utils";
	
	
	private static String mtext = "";

	private static Toast toast;
}
