package com.shenma.tvlauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.adapter.WallpaperAdapter;
import com.shenma.tvlauncher.domain.Wallpaper;
import com.shenma.tvlauncher.domain.WallpaperInfo;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class SettingWallpaperActivity extends BaseActivity {

	private RequestQueue mQueue;
	private GridView wallpaper_gv;
	private int position = -1;
	private List<WallpaperInfo> data;
	private Listener<Wallpaper> listener = new Listener<Wallpaper>() {

		@Override
		public void onResponse(Wallpaper response) {
			if(response != null && "200".equals(response.getCode())){
				data = response.getData();
				wallpaper_gv.setAdapter(new WallpaperAdapter(context, data));
			}
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			if (error instanceof TimeoutError) {
				Logger.e("zhouchuan", "请求超时");
			} else if (error instanceof AuthFailureError) {
				Logger.e("zhouchuan",
						"AuthFailureError=" + error.toString());
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_wallpaper);
		initView();
	}
	
	@Override
	protected void initView() {
		findViewById();
		setListener();
		initData();
	}

	private void initData() {
		mQueue = Volley.newRequestQueue(context, new HurlStack());
		GsonRequest<Wallpaper> mWallpaper = new GsonRequest<Wallpaper>(Method.GET, Constant.WALLPAPER, Wallpaper.class, listener, errorListener){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> hearders = new HashMap<String, String>();
				String base64 = new String(Base64.encode("admin:1234".getBytes(), Base64.DEFAULT));
				hearders.put("Authorization", "Basic " + base64);
				return hearders;
			}
		};
		mQueue.add(mWallpaper);
	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {
		wallpaper_gv = (GridView) findViewById(R.id.wallpaper_gv);
	}

	@Override
	protected void setListener() {
		LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.setbig2));
		lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
		lac.setDelay(0.5f);
		wallpaper_gv.setLayoutAnimation(lac);
//		wallpaper_gv.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				if(position > lastIndex){//向下按
//					if((position-parent.getFirstVisiblePosition()) > 14 && (position-lastIndex) == 5 && position < (parent.getCount()%5 == 0 ? parent.getCount()-5 : parent.getCount()-(parent.getCount()%5))){
//						wallpaper_gv.post(new Runnable() {
//							@Override
//							public void run() {
//								wallpaper_gv.smoothScrollBy(152, 500);
//							}
//						});
//					}
//				}else {//向上按
//					if((position-parent.getFirstVisiblePosition()) < 5 && (lastIndex-position) == 5 && parent.getFirstVisiblePosition() != 0){
//						wallpaper_gv.post(new Runnable() {
//							@Override
//							public void run() {
//								wallpaper_gv.smoothScrollBy(-152, 500);
//							}
//						});
//					}
//				}
//				lastIndex = position;
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {}
//		});
		wallpaper_gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SettingWallpaperActivity.this.position = position;
				Utils.showToast("设置成功,退出该界面才能看到效果哦",context, R.drawable.toast_smile);
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(position != -1) {
			String wallpaperFileName = data.get(position).getSkinpath().substring(data.get(position).getSkinpath().lastIndexOf("/") + 1);
			String wallpaperPath = data.get(position).getSkinpath();
			if(!startCheckLoaclFile(wallpaperFileName)) {
				startDownload(Constant.HEARD_URL+wallpaperPath);
			}
		}
		super.onDestroy();
	}
	
	/**
	 * 检测本地文件
	 * 
	 * @author drowtram
	 * @param fileName 壁纸图片文件名
	 */
	private boolean startCheckLoaclFile(String fileName) {
		File file = context.getFilesDir();
		try {
			if (file.exists() && file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					String fName = f.getName();
					if (fName.equals(fileName)) {
						//检测到本地有对应的资源图片，则拷贝到程序资源文件夹中替换主页背景 
						sendBroadcast(fileName);
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 下载文件
	 * @author drowtram
	 * @param context
	 * @param url
	 */
	private void startDownload(final String url){
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = context.getFilesDir();
				String fileName = url.substring(url.lastIndexOf("/")+1);
				Log.d("zhouchuan", "文件路径"+fileName);
				if(!file.exists()){
					file.mkdirs();
				}
				try {
					HttpGet hGet = new HttpGet(url.replaceAll(" ", "%20"));
					HttpResponse hResponse = new DefaultHttpClient().execute(hGet);
					if(hResponse.getStatusLine().getStatusCode() == 200){
						InputStream is = hResponse.getEntity().getContent();
//						FileOutputStream fos = new FileOutputStream(Constant.PUBLIC_DIR + fileName);
						FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) != -1) {
					     fos.write(buffer, 0, count);
					    }
						fos.close();
						is.close();
						//下载完成，则拷贝到程序资源文件夹中替换主页背景 
						sendBroadcast(fileName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void sendBroadcast(String fileName){
		Intent mIntent = new Intent();
		mIntent.setAction("com.shenma.changewallpaper");
		mIntent.putExtra("wallpaperFileName", fileName);
		sendBroadcast(mIntent);
	}
}
