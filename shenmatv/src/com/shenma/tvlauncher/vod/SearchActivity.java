package com.shenma.tvlauncher.vod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.vod.adapter.SearchTypeAdapter;
import com.shenma.tvlauncher.vod.domain.RequestVo;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodTypeInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mv_search_new);
		context = SearchActivity.this;
		initIntent();
		initView();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}

	private void initView() {
		findViewById();
		loadViewLayout();
		setListener();
	}

	private void findViewById() {
		sb = new StringBuilder();
		search_keybord_input = (EditText) findViewById(R.id.search_keybord_input);
		tv_search = (TextView) findViewById(R.id.search_keybord_hint);
		tv_search_empty_text = (TextView) findViewById(R.id.search_empty_text);
		search_keybord_full_layout = (LinearLayout) findViewById(R.id.search_keybord_full_layout);
		gv_search_result = (GridView) findViewById(R.id.search_result);
		gv_search_result.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	private void loadViewLayout() {

	}

	private void setListener() {
		gv_search_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchActivity.this,VodDetailsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if(type.equals("ALL")){
					intent.putExtra("vodtype", vodDatas.get(position).getType());
				}else{
					intent.putExtra("vodtype", type);
				}
				intent.putExtra("nextlink", vodDatas.get(position)
						.getNextlink());
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}

		});
		gv_search_result.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int i = totalItemCount - visibleItemCount;
				if (firstVisibleItem < i) {
					//
					Logger.v("joychang", "<<<firstVisibleItem="
							+ firstVisibleItem + ".....i=" + i);
					return;
				} else {
					// 分页加载数据
					pageDown();
				}
			}
		});
		
		gv_search_result.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				if(position > lastIndex){//向下按
//					Logger.i("joychang", "向下按position="+position+"....parent.getFirstVisiblePosition()="+parent.getFirstVisiblePosition()+"...lastIndex="+lastIndex+"后面条件="+(position < (parent.getCount()%6 == 0 ? parent.getCount()-6 : parent.getCount()-(parent.getCount()%6))));
//					if((position-parent.getFirstVisiblePosition()) >= 4 && (position-lastIndex) == 4 && position < (parent.getCount()%4 == 0 ? parent.getCount()-4 : parent.getCount()-(parent.getCount()%4))){
//						gv_search_result.post(new Runnable() {
//							@Override
//							public void run() {
//								gv_search_result.smoothScrollBy(305, 500);
//							}
//						});
//					}
//				}else {//向上按
//					Logger.i("joychang", "向上按position="+position+"....parent.getFirstVisiblePosition()="+parent.getFirstVisiblePosition()+"...lastIndex="+lastIndex);
//					if((position-parent.getFirstVisiblePosition()) < 4 && (lastIndex-position) == 4 && parent.getFirstVisiblePosition() != 0){
//						gv_search_result.post(new Runnable() {
//							@Override
//							public void run() {
//								gv_search_result.smoothScrollBy(-305, 500);
//							}
//						});
//					}
//				}
//				lastIndex = position;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initIntent() {
		Intent intent = getIntent();
		//VOD_TYPE = intent.getStringExtra("VOD_TYPE");
		type = intent.getStringExtra("TYPE");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void doClick(View target) {
		int tag = target.getId();
		if (tag == R.id.search_keybord_full_clear) {
			StringBuilder nsb = new StringBuilder();
			sb = nsb;
			readyToSearch();
			return;
		}
		if (tag == R.id.search_keybord_full_del) {
			if (this.sb.length() <= 0) {
			} else {
				sb.deleteCharAt(sb.length() - 1);
			}
			readyToSearch();
			return;
		}
		if(tag == R.id.search_keybord_sj){
			type = "TVPLAY";
			readyToSearch();
			return;
		}
		if(tag == R.id.search_keybord_sp){
			type = "MOVIE";
			readyToSearch();
			return;
		}
		Object obj = target.getTag();
		sb.append(obj);
		readyToSearch();
	}

	private void readyToSearch() {
		String str = sb.toString();
		search_keybord_input.setText(str);
		Logger.v("joychang", "搜索===="+str);
		SearchDatas(str);
	}

	/**
	 * 向下翻页
	 */
	private void pageDown() {
		Logger.v("joychang", "pageindex=" + pageindex + "....vodpageindex="
				+ vodpageindex);
		if (pageindex >= totalpage || pageindex > vodpageindex)
			return;
		pageindex = this.pageindex + 1;
		Logger.v("joychang", "请求页数===" + pageindex);
		processLogic();
	}
	
	/**
	 * 获取视频列表
	 */
	protected void processLogic() {
		RequestVo vo = new RequestVo();
		vo.context = context;
		//vo.requestUrl = VOD_URL + "&page=" + pageindex + "&pagesize="+ PAGESIZE;
		vo.requestUrl = VOD_URL;
		Logger.v("joychang", "访问:::" + VOD_URL);
		getDataFromServer(vo);
	}
	
	/**
	 * 搜索视频
	 */
	
	protected void SearchDatas(String filter){
		RequestVo vo = new RequestVo();
		vo.context = context;
		vodDatas = null;
		pageindex = 1;
		if(type.equals("MOVIE")||type.equals("DOCUMENTARY")||type.equals("TEACH")){
			VOD_URL = Constant.VOD_TYPE + filter + "&page=" + pageindex + "&pagesize="+ PAGESIZE;
			vo.requestUrl = VOD_URL;
			Logger.v("joychang", "访问:::" + Constant.VOD_TYPE+filter);
		}else if(type.equals("ALL")){
			VOD_URL = Constant.VOD_TYPE_ALL + filter + "&page=" + pageindex + "&pagesize="+ PAGESIZE;
			vo.requestUrl = VOD_URL;
			Logger.v("joychang", "访问:::" + Constant.VOD_TYPE_ALL+filter);
		}else{
			VOD_URL =Constant.VOD_TYPE_HAO123 + filter + "&page=" + pageindex + "&pagesize="+ PAGESIZE;
			vo.requestUrl = VOD_URL;
			Logger.v("joychang", "访问:::" + Constant.VOD_TYPE_HAO123+filter);
		}
		Logger.d("joychang", "搜索VOD_URL="+VOD_URL);
		getDataFromServer(vo);
	}
	/**
	 * 从服务器上获取数据，并回调处理
	 * 
	 * @param reqVo
	 * @param callBack
	 */
	protected void getDataFromServer(RequestVo reqVo) {
		showProgressDialog();
		mQueue = Volley.newRequestQueue(context, new HurlStack());
		if(Utils.hasNetwork(context)){
			GsonRequest<VodTypeInfo> mVodData = new GsonRequest<VodTypeInfo>(Method.GET, reqVo.requestUrl,
					VodTypeInfo.class,createVodDataSuccessListener(),createVodDataErrorListener()){
						@Override
                        public Map<String, String> getHeaders()
                        		throws AuthFailureError {
    						HashMap<String, String> headers = new HashMap<String, String>();
    						String base64 = new String(android.util.Base64.encode(
    								"admin:1234".getBytes(), android.util.Base64.DEFAULT));
    						headers.put("Authorization", "Basic " + base64);
                        	return headers;
                        }};
			mQueue.add(mVodData);     //     执行     
		}
	}

	//影视数据请求成功
    private Response.Listener<VodTypeInfo> createVodDataSuccessListener() {
        return new Response.Listener<VodTypeInfo>() {
            @Override
            public void onResponse(VodTypeInfo paramObject) {
            	// && null!=paramObject.getData()
    			if (null!=paramObject && null!=paramObject.getData() && paramObject.getData().size()>0) {
    				if (null != vodDatas && vodDatas.size() > 0) {
    					vodtypeinfo = paramObject;
    					ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) paramObject
    							.getData();
    					if (null != vodDatalist && vodDatalist.size() > 0) {
    						vodDatas.addAll(vodDatalist);
    						vodpageindex = vodDatas.size() / PAGESIZE;
    						searchtypeAdapter.changData(vodDatas);
    					}
    				} else {
    					vodpageindex = 1;
    					vodtypeinfo = paramObject;
    					Logger.v("joychang",
    							"vodtypeinfo" + vodtypeinfo.getPageindex() + "...."
    									+ vodtypeinfo.getVideonum());
    					totalpage = vodtypeinfo.getTotalpage();
    					ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) paramObject
    							.getData();
    					if (null != vodDatalist && vodDatalist.size() > 0) {
    						vodDatas = vodDatalist;
    						searchtypeAdapter = new SearchTypeAdapter(context, vodDatas,
    								imageLoader);
    						gv_search_result.setAdapter(searchtypeAdapter);
    					}
    				}

    			}else{
					Utils.showToast(context,"亲，没有搜索到相关内容！",R.drawable.toast_err);
					vodDatas = new ArrayList<VodDataInfo>();
					searchtypeAdapter = new SearchTypeAdapter(context, vodDatas,
							imageLoader);
					gv_search_result.setAdapter(searchtypeAdapter);
    			}
    			closeProgressDialog();
            }
        };
    } 
    //影视数据请求失败
    private Response.ErrorListener createVodDataErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	if(error instanceof TimeoutError){
            		Logger.e("joychang", "请求超时");
            		Utils.showToast(context,getString(R.string.str_data_loading_error),R.drawable.toast_err);
    				if(null != vodDatas && vodDatas.size() > 0){
    					vodpageindex = vodDatas.size() / PAGESIZE;
    					pageindex = vodpageindex;
    				}else{
    					pageindex = 0;
    				}
            	}else if(error instanceof ParseError){
            		pageindex = 2;
    				searchtypeAdapter.vodDatas.clear();
    				searchtypeAdapter.notifyDataSetChanged();
    				Utils.showToast(context,"亲，没有搜索到相关内容！",R.drawable.toast_err);
            		Logger.e("joychang", "ParseError="+error.toString());
                }else if(error instanceof AuthFailureError){
            		Logger.e("joychang", "AuthFailureError="+error.toString());
                }
            	closeProgressDialog();
            }
        };
    }
    
//    private Handler mHandler = new Handler(){
//    	public void handleMessage(Message msg) {
//    		switch (msg.what) {
//			case REFRESH_ADAPTER:
//				searchtypeAdapter.vodDatas.clear();
//				searchtypeAdapter.notifyDataSetChanged();
//				break;
//
//			default:
//				break;
//			}
//    	};
//    };

	/**
	 * 显示提示框
	 */
	protected void showProgressDialog() {
		Utils.loadingShow_tv(SearchActivity.this,R.string.str_data_loading);
	}

	/**
	 * 关闭提示框
	 */
	protected void closeProgressDialog() {
		Utils.loadingClose_Tv();
	}

	/**
	 * @class WindowMessageID
	 * @brief 内部消息ID定义类。
	 * @author joychang
	 */
	private class WindowMessageID {
		/**
		 * @brief 服务请求成功。
		 */
		public static final int SUCCESS = 0x00000001;
		/**
		 * @brief 服务请求失败。
		 */
		public static final int NET_FAILED = 0x00000002;
	}

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ArrayList<VodDataInfo> vodDatas;
	private VodTypeInfo vodtypeinfo;
	private SearchTypeAdapter searchtypeAdapter;
	private int pageindex = 1;
	private int vodpageindex;
	private int totalpage;
	private final static int PAGESIZE = 30;
	private String type=null;
//	private String VOD_TYPE = "http://api.lsott.com/app/app.php?nozzle=character&zm=";
//	private String VOD_TYPE_HAO123 = "http://api.lsott.com/app/?nozzle=character&zm=";
	private String VOD_URL = null;
	private EditText search_keybord_input;
	private TextView tv_search, tv_search_empty_text;
	private LinearLayout search_keybord_full_layout;
	private GridView gv_search_result;
	private StringBuilder sb;
	private Context context;
	private RequestQueue mQueue;
	private int lastIndex = -1;
	private static final int REFRESH_ADAPTER = 0x000000001;
}
