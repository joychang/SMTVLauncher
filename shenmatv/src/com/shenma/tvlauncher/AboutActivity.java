package com.shenma.tvlauncher;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.domain.Update;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.LruCacheUtils;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.HomeDialog;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * 关于界面
 * @author drowtram
 *
 */
public class AboutActivity extends BaseActivity {
	
	public RequestQueue mQueue;
	private LruCacheUtils cacheUtils;
	private Listener<Update> mSucListener = new Listener<Update>() {

		@Override
		public void onResponse(Update response) {
        	if(null!=response && "200".equals(response.getCode())){
        		if(null!=response.getData()){
        			String type = response.getData().getType();
    				// 是否提示更新版本
    				if (null != type && type.equals("1")) {
    					showUpdateDialog(response.getData().getVersionremark(),Constant.HEARD_URL+response.getData().getApkurl());// 版本不一致提示升级
    				}
        		}
        	}else if (null!=response && "10001".equals(response.getCode())) {
				Utils.showToast(context, "赞哦，当前是最新版本哦", R.drawable.toast_smile);
        		Logger.d("zhouchuan", response.getMsg());
			}
		}
	};
	private ErrorListener mErrListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			if(error instanceof TimeoutError){
        		Logger.e("zhouchuan", "请求超时");
        	}else if(error instanceof AuthFailureError){
        		Logger.e("zhouchuan", "AuthFailureError="+error.toString());
            }
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_about);
		cacheUtils = LruCacheUtils.getInstance();
		findViewById(R.id.setting_about).setBackgroundResource(R.drawable.video_details_bg);
		mQueue = Volley.newRequestQueue(this, new HurlStack());
		((ImageView)findViewById(R.id.about_iv)).setImageBitmap(getBitMap());
		
	}
	
	public Bitmap getBitMap() {
		//先从图片缓存中取图
		Bitmap bitmap = cacheUtils.getBitmapFromMemCache(String.valueOf(R.drawable.about_sm));
		if(bitmap != null) {
			return bitmap;
		}else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
			options.inPurgeable = true;
			options.inInputShareable = true;
			try {
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.about_sm, options);
			} catch (OutOfMemoryError e) {
				cacheUtils.clearAllImageCache();
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.about_sm, options);
			}
			cacheUtils.addBitmapToMemoryCache(String.valueOf(R.drawable.about_sm), bitmap);//把图片加入到图片缓存中
			return bitmap;
		}
	}
	
	public void startCheckUpdate(View v) {
		Utils.showToast(context, R.string.version_updata, R.drawable.toast_smile);
		Logger.d("zhouchuan", Constant.UPDATE_URL+params);
		GsonRequest<Update> mUpdate = new GsonRequest<Update>(Method.GET, Constant.UPDATE_URL+params,Update.class,mSucListener,mErrListener){
			@Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String base64 = new String(android.util.Base64.encode("admin:1234".getBytes(), android.util.Base64.DEFAULT));
				headers.put("Authorization", "Basic " + base64);
	        	return headers;
	        }};
       mQueue.add(mUpdate);     //     执行    
	}
	
	/**
	 * 显示升级提示的对话框
	 */
	private void showUpdateDialog(String remark,final String updateurl) {
		Logger.d("zhouchuan", remark);
		HomeDialog.Builder builder = new HomeDialog.Builder(context);
		builder.setTitle("版本升级");
		String [] remarks = remark.split(";");
		String str = "";
		for(int i=0;i<remarks.length;i++){
			if(i==remarks.length-1){
				str = str+remarks[i];
				continue;
			}else{
				str = str+remarks[i]+"\n";
			}
		}
		builder.setMessage(str);
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Utils.showToast(context, R.string.version_updata_downlond, R.drawable.toast_smile);
				Utils.startDownloadApk(context, updateurl);
				dialog.dismiss();
			}
		});
		builder.setNeutralButton("稍后提醒", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void initView() {
		
	}

	@Override
	protected void loadViewLayout() {
		
	}

	@Override
	protected void findViewById() {
		
	}

	@Override
	protected void setListener() {
		
	}
}
