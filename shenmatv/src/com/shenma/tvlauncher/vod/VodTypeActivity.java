package com.shenma.tvlauncher.vod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.shenma.tvlauncher.vod.adapter.TypeDetailsSubMenuAdapter;
import com.shenma.tvlauncher.vod.adapter.VodtypeAdapter;
import com.shenma.tvlauncher.vod.domain.RequestVo;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodFilter;
import com.shenma.tvlauncher.vod.domain.VodFilterInfo;
import com.shenma.tvlauncher.vod.domain.VodTypeInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class VodTypeActivity extends Activity implements OnItemClickListener {

	public VodTypeActivity() {
		// TODO Auto-generated constructor stub
		vodDatas = new ArrayList<VodDataInfo>();
		context = VodTypeActivity.this;
		//mQueue = MyVolley.getRequestQueue();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vod);
		findViewById(R.id.vod).setBackgroundResource(R.drawable.video_details_bg);
		initIntent();
		initView();
		initData();
		initMenuData();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}
	/**
	 * 获取影视类型
	 */
	private void initIntent() {
		Intent intent = getIntent();
		type = intent.getStringExtra("TYPE");
		if (null != type && type.equals("TVPLAY")) {
			// 连续剧
			VOD_TYPE = Constant.TVPLAY;
		} else if (null != type && type.equals("COMIC")) {
			// 动漫
			VOD_TYPE = Constant.COMIC;
		} else if (null != type && type.equals("TVSHOW")) {
			// 综艺
			VOD_TYPE = Constant.TVSHOW;
		} else if (null != type && type.equals("MOVIE")) {
			// 电影
			VOD_TYPE = Constant.MOVIE;
		} else if (null != type && type.equals("TEACH")) {
			// 教育
			VOD_TYPE = Constant.TEACH;
		} else if (null != type && type.equals("DOCUMENTARY")) {
			// 记录
			VOD_TYPE = Constant.DOCUMENTARY;
		}

	}

	/**
	 * 初始化
	 */
	private void initView() {
		mQueue = Volley.newRequestQueue(context, new HurlStack());
		findViewById();
		loadViewLayout();
		setListener();
		processLogic("");
		gHeight = gv_type_details_grid.getHeight();
		Logger.i(TAG, "gHeight="+gHeight);
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (VOD_TYPE.equals(Constant.TVPLAY)) {
			// 连续剧
			iv_type_details_type.setImageResource(R.drawable.vod_tvplay);
		} else if (VOD_TYPE.equals(Constant.COMIC)) {
			// 动漫
			iv_type_details_type.setImageResource(R.drawable.vod_comic);
		} else if (VOD_TYPE.equals(Constant.TVSHOW)) {
			// 综艺
			iv_type_details_type.setImageResource(R.drawable.vod_tvshow);
		} else if (VOD_TYPE.equals(Constant.MOVIE)) {
			// 电影
			iv_type_details_type.setImageResource(R.drawable.vod_movie);
		} else if (VOD_TYPE.equals(Constant.TEACH)) {
			// 教育
			iv_type_details_type.setImageResource(R.drawable.vod_teach);
		} else if (VOD_TYPE.equals(Constant.DOCUMENTARY)) {
			// 教育
			iv_type_details_type.setImageResource(R.drawable.vod_documentary);
		}
		getFilterDataFromServer();
	}

	/**
	 * 初始化菜单数据
	 */
	private void initMenuData() {
		//clearFilter();
	}

	protected void findViewById() {
		iv_type_details_type = (ImageView) findViewById(R.id.type_details_type);
		tv_type_details_sum = (TextView) findViewById(R.id.type_details_sum);
		b_type_details_fliter = (ImageView) findViewById(R.id.type_details_fliter);
		gv_type_details_grid = (GridView) findViewById(R.id.type_details_grid);
		gv_type_details_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));

		menulayout = (LinearLayout) findViewById(R.id.type_details_menulayout);
		tv_filter_year = (TextView) menulayout.findViewById(R.id.tv_filter_year);
		filter_list_type = (ListView) menulayout.findViewById(R.id.filter_list_type);
		filter_list_type.setChoiceMode(1);
		filter_list_year = (ListView) menulayout.findViewById(R.id.filter_list_year);
		filter_list_year.setChoiceMode(1);
		filter_list_area = (ListView) menulayout.findViewById(R.id.filter_list_area);
		filter_list_area.setChoiceMode(1);
		filter_list_seach = (ListView) menulayout.findViewById(R.id.filter_list_seach);
		filter_list_seach.setChoiceMode(1);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.equals(filter_list_seach)) {
			if (position == 0) {
				// 转到搜索界面
				Intent intent = new Intent(VodTypeActivity.this,SearchActivity.class);
				intent.putExtra("VOD_TYPE", VOD_TYPE);
				intent.putExtra("TYPE", type);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			} else if (position == 1) {
				// 清空筛选
				clearFilter();
				//menulayout.clearFocus();
				//filter_list_seach.setSelection(position);
				//menulayout.setVisibility(View.GONE);
				//gv_type_details_grid.setFocusable(true);
			}
		}
		if (parent.equals(filter_list_type)) {
			filter_list_type.setItemChecked(position, true);
			((TypeDetailsSubMenuAdapter) filter_list_type.getAdapter())
					.setSelctItem(position);
			setFilterString();
			//menulayout.setVisibility(View.GONE);
			//menulayout.clearFocus();
			//filter_list_type.setSelection(position);
			//gv_type_details_grid.setFocusable(true);
		}
		if (parent.equals(filter_list_year)) {
			filter_list_year.setItemChecked(position, true);
			((TypeDetailsSubMenuAdapter) filter_list_year.getAdapter())
					.setSelctItem(position);
			setFilterString();
			//menulayout.setVisibility(View.GONE);
			//menulayout.clearFocus();
			//filter_list_year.setSelection(position);
			//gv_type_details_grid.setFocusable(true);
		}
		if (parent.equals(filter_list_area)) {
			filter_list_area.setItemChecked(position, true);
			((TypeDetailsSubMenuAdapter) filter_list_area.getAdapter())
					.setSelctItem(position);
			setFilterString();
			//menulayout.setVisibility(View.GONE);
			//menulayout.clearFocus();
			//filter_list_area.setSelection(position);
			//gv_type_details_grid.setFocusable(true);
		}
	}

	protected void loadViewLayout() {

	}

	/**
	 * 获取视频筛选信息
	 */
	protected void getFilterDataFromServer() {
		RequestVo vo = new RequestVo();
		vo.context = context;
		if(type.equals("MOVIE")||type.equals("DOCUMENTARY")||type.equals("TEACH")){
			vo.requestUrl = Constant.VODFILTER;
		}else{
			vo.requestUrl = Constant.VODFILTER_H123;
		}
		vo.type = VOD_FILTER;
		getDataFromServer(vo);
	}

	private void setFilterString() {
		String filterString = "";
		vodDatas = null;
		pageindex  = 1;
		int j = filter_list_area.getCheckedItemPosition();
		if (j >= 0) {
			//String area = areas.get(j);
			String area = (String) filter_list_area.getAdapter().getItem(j);
			area = Utils.getEcodString(area);
			filterString = filterString + "&area=" + area;
		}
		int k = filter_list_type.getCheckedItemPosition();
		if (k >= 0) {
			//String type = types.get(j);
			String type = (String) filter_list_type.getAdapter().getItem(k);
			type = Utils.getEcodString(type);
			filterString = filterString + "&type=" + type;
		}
		int m = filter_list_year.getCheckedItemPosition();
		if (m >= 0) {
			// areas.get(j);
			String year = (String) filter_list_year.getAdapter().getItem(m);
			if(type.equals("TEACH")){
				filterString = filterString + "&language=" + year;
			}else if(type.equals("COMIC")){
				filterString = filterString + "&prop=" + year;
			}else if(type.equals("TVPLAY")){
				filterString = filterString + "&start=" + year;
			}else{
				filterString = filterString + "&year=" + year;
			}
		}
//		try {
//			filterString = new String(filterString.getBytes(),"utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		processLogic(filterString);
	}

	private void clearFilter() {
		((TypeDetailsSubMenuAdapter)filter_list_area.getAdapter()).setSelctItem(-1);
		filter_list_area.setItemChecked(-1, true);
		((TypeDetailsSubMenuAdapter)filter_list_type.getAdapter()
				
				).setSelctItem(-1);
		filter_list_type.setItemChecked(-1, true);
		((TypeDetailsSubMenuAdapter)filter_list_year.getAdapter()).setSelctItem(-1);
		filter_list_year.setItemChecked(-1, true);
		((TypeDetailsSubMenuAdapter)filter_list_seach.getAdapter()).setSelctItem(-1);
		filter_list_seach.setItemChecked(-1, true);
		vodDatas = null;
		pageindex  = 1;
		processLogic("");
	}
	private long start;
	/**
	 * 获取视频列表
	 */
	protected void processLogic(String filter) {
		RequestVo vo = new RequestVo();
		vo.context = context;
		vo.type = VOD_DATA;
		//vo.requestUrl = VOD_TYPE + "&page=" + pageindex + "&pagesize="+ PAGESIZE + filter;
		vo.requestUrl = VOD_TYPE + "&page=" + pageindex + filter;
		Logger.d("joychang","vo.requestUrl="+vo.requestUrl);
		start = System.currentTimeMillis();
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
		if(Utils.hasNetwork(context)){
			if(reqVo.type==VOD_DATA){
				GsonRequest<VodTypeInfo> mVodData = new GsonRequest<VodTypeInfo>(Method.GET, reqVo.requestUrl,
						VodTypeInfo.class,createVodDataSuccessListener(),createVodDataErrorListener());
				mQueue.add(mVodData);     //     执行     
			}else if(reqVo.type==VOD_FILTER){
				GsonRequest<VodFilter> mVodData = new GsonRequest<VodFilter>(Method.GET, reqVo.requestUrl,
						VodFilter.class,createVodFilterSuccessListener(),createVodFilterErrorListener());
				mQueue.add(mVodData);     //     执行     
			}
		}else{

		}
		


		     
	}
	//数据筛选请求成功
	 private Response.Listener<VodFilter> createVodFilterSuccessListener() {
	        return new Response.Listener<VodFilter>() {
				@Override
				public void onResponse(VodFilter response) {
					// TODO Auto-generated method stub
					if (response != null) {
						if (VOD_TYPE.equals(Constant.TVPLAY)) {
							// 连续剧
							vodFilter = response.getTvplay();
						} else if (VOD_TYPE.equals(Constant.COMIC)) {
							// 动漫
							vodFilter = response.getComic();
						} else if (VOD_TYPE.equals(Constant.TVSHOW)) {
							// 综艺
							vodFilter = response.getTvshow();
						} else if (VOD_TYPE.equals(Constant.MOVIE)) {
							// 电影
							vodFilter = response.getMovie();
						} else if (VOD_TYPE.equals(Constant.TEACH)) {
							// 教育
							vodFilter = response.getTeach();
							tv_filter_year.setText("科目");
						} else if (VOD_TYPE.equals(Constant.DOCUMENTARY)) {
							// 记录
							vodFilter = response.getDocumentary();
						}
						ArrayList<String> seachs = new ArrayList<String>();
						seachs.add("搜索");
						seachs.add("清空筛选");
						if(type.equals("MOVIE")||type.equals("DOCUMENTARY")||type.equals("TEACH")){
							if(vodFilter.size()>0){
								types = Arrays.asList(vodFilter.get(0).getValues());
							}
							if(vodFilter.size()>1){
								years = Arrays.asList(vodFilter.get(1).getValues());
							}
							if(vodFilter.size()>2){
								areas = Arrays.asList(vodFilter.get(2).getValues());
							}
						}else{
							if(vodFilter.size()>0){
								String name = vodFilter.get(0).getField();
								if(name.equals("type")){
									types = Arrays.asList(vodFilter.get(0).getValues());
								}else if(name.equals("year")){
									years = Arrays.asList(vodFilter.get(0).getValues());
								}else if(name.equals("area")){
									areas = Arrays.asList(vodFilter.get(0).getValues());
								}else if(name.equals("prop")){
									years = Arrays.asList(vodFilter.get(0).getValues());
									tv_filter_year.setText("动漫版本");
								}else if(name.equals("start")){
									years = Arrays.asList(vodFilter.get(0).getValues());
									tv_filter_year.setText("开播时间");
								}
							}
							if(vodFilter.size()>1){
								String name = vodFilter.get(1).getField();
								if(name.equals("type")){
									types = Arrays.asList(vodFilter.get(1).getValues());
								}else if(name.equals("year")){
									years = Arrays.asList(vodFilter.get(1).getValues());
								}else if(name.equals("area")){
									areas = Arrays.asList(vodFilter.get(1).getValues());
								}else if(name.equals("prop")){
									years = Arrays.asList(vodFilter.get(1).getValues());
									tv_filter_year.setText("动漫版本");
								}else if(name.equals("start")){
									years = Arrays.asList(vodFilter.get(1).getValues());
									tv_filter_year.setText("开播时间");
								}
							}
							if(vodFilter.size()>2){
								String name = vodFilter.get(2).getField();
								if(name.equals("type")){
									types = Arrays.asList(vodFilter.get(2).getValues());
								}else if(name.equals("year")){
									years = Arrays.asList(vodFilter.get(2).getValues());
								}else if(name.equals("area")){
									areas = Arrays.asList(vodFilter.get(2).getValues());
								}else if(name.equals("prop")){
									years = Arrays.asList(vodFilter.get(2).getValues());
									tv_filter_year.setText("动漫版本");
								}else if(name.equals("start")){
									years = Arrays.asList(vodFilter.get(2).getValues());
									tv_filter_year.setText("开播时间");
								}
							}
						}

						if(null!=types&&types.size()>0){
							TypeDetailsSubMenuAdapter typemenuAdapter1 = new TypeDetailsSubMenuAdapter(
									context, types);
							filter_list_type.setAdapter(typemenuAdapter1);

						}
						if(null!=years&&years.size()>0){
							TypeDetailsSubMenuAdapter typemenuAdapter2 = new TypeDetailsSubMenuAdapter(
									context, years);
							filter_list_year.setAdapter(typemenuAdapter2);
						}
						if(null!=areas&&areas.size()>0){
							TypeDetailsSubMenuAdapter typemenuAdapter3 = new TypeDetailsSubMenuAdapter(
									context, areas);
							filter_list_area.setAdapter(typemenuAdapter3);
							
						}
						TypeDetailsSubMenuAdapter typemenuAdapter = new TypeDetailsSubMenuAdapter(
								context, seachs);
						filter_list_seach.setAdapter(typemenuAdapter);
					}
				
				}	
			};
	      }

	 
	 private Response.ErrorListener createVodFilterErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	
	            }
	        };
	 }
	//影视数据请求成功
    private Response.Listener<VodTypeInfo> createVodDataSuccessListener() {
        return new Response.Listener<VodTypeInfo>() {
            @Override
            public void onResponse(VodTypeInfo response) {
            	closeProgressDialog();
    			if (response != null) {
                    Log.v("joychang", "Vod用时=="+(System.currentTimeMillis()-start));
    				Logger.v("joychang", "获取数据成功！pageindex="+pageindex);
    				if (null != vodDatas && vodDatas.size() > 0) {
    					vodtypeinfo = response;
    					ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) response
    							.getData();
    					if (null != vodDatalist && vodDatalist.size() > 0) {
    						vodDatas.addAll(vodDatalist);
    						//vodpageindex = vodDatas.size() / PAGESIZE;
    						vodpageindex = ++vodpageindex;
    						vodtypeAdapter.changData(vodDatas);
    					}
    				} else {
    					vodpageindex = 1;
    					vodtypeinfo = response;
    					Logger.v("joychang",
    							"vodtypeinfo" + vodtypeinfo.getPageindex() + "...."
    									+ vodtypeinfo.getVideonum());
    					tv_type_details_sum.setText("共" + vodtypeinfo.getVideonum()
    							+ "部");
    					totalpage = vodtypeinfo.getTotalpage();
    					ArrayList<VodDataInfo> vodDatalist = (ArrayList<VodDataInfo>) response
    							.getData();
    					if (null != vodDatalist && vodDatalist.size() > 0) {
    						vodDatas = vodDatalist;
    						vodtypeAdapter = new VodtypeAdapter(context, vodDatas,
    								imageLoader);
    						gv_type_details_grid.setAdapter(vodtypeAdapter);
    					}else{
    	            		pageindex = 2;
    	    				vodtypeAdapter.vodDatas.clear();
    	    				vodtypeAdapter.notifyDataSetChanged();
    	    				//Utils.showToast(context,"亲，没有搜索到相关内容！",R.drawable.toast_err);
    					}
    				}

    			}else{
    				if(null != vodDatas && vodDatas.size() > 0){
    					//vodpageindex = vodDatas.size() / PAGESIZE;
    					pageindex = vodpageindex;
    				}else{
    					vodDatas = new ArrayList<VodDataInfo>();
						vodtypeAdapter = new VodtypeAdapter(context, vodDatas,
								imageLoader);
						gv_type_details_grid.setAdapter(vodtypeAdapter);
    					pageindex = 0;
    				}
    				Logger.v("joychang", "获取数据失败！dataCallBack...pageindex="+pageindex);
    			}
    		
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
    				Utils.showToast(context,
    						getString(R.string.str_data_loading_error),
    						R.drawable.toast_err);
    				if(null != vodDatas && vodDatas.size() > 0){
    					//vodpageindex = vodDatas.size() / PAGESIZE;
    					pageindex = vodpageindex;
    				}else{
    					pageindex = 0;
    				}
            	}else if(error instanceof ParseError){
            		tv_type_details_sum.setText("共0部");
            		pageindex = 2;
    				vodtypeAdapter.vodDatas.clear();
    				vodtypeAdapter.notifyDataSetChanged();
    				Utils.showToast(context,"亲，没有搜索到相关内容！",R.drawable.toast_err);
            		Logger.e("joychang", "ParseError="+error.toString());
                }else if(error instanceof AuthFailureError){
            		Logger.e("joychang", "AuthFailureError="+error.toString());
                }
            	closeProgressDialog();
            }
        };
    }

	protected void setListener() {
		// TODO Auto-generated method stub
		filter_list_type.setOnItemClickListener(this);
		filter_list_year.setOnItemClickListener(this);
		filter_list_area.setOnItemClickListener(this);
		filter_list_seach.setOnItemClickListener(this);
		b_type_details_fliter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showFilter();
			}
		});
		gv_type_details_grid
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						menulayout.setVisibility(View.VISIBLE);
						gv_type_details_grid.clearFocus();
						gv_type_details_grid.setFocusable(false);
						Boolean bl = menulayout.requestFocus();
						return bl;
					}

				});
		gv_type_details_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(VodTypeActivity.this,
						VodDetailsActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("vodtype", type);
				intent.putExtra("vodstate", vodDatas.get(position).getState());
				intent.putExtra("nextlink", vodDatas.get(position).getNextlink());
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			}

		});
		gv_type_details_grid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int i = totalItemCount - visibleItemCount;
				Logger.v("joychang", "<<<firstVisibleItem="+ firstVisibleItem + ".....i=" + i);
				if (i == 0 || firstVisibleItem < i) {
					//
					return;
				} else {
					// 分页加载数据
					pageDown();
				}
			}
		});
		gv_type_details_grid.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
//				if(position > lastIndex){//向下按
//					Logger.i(TAG, "向下按position="+position+"....parent.getFirstVisiblePosition()="+parent.getFirstVisiblePosition()+"...lastIndex="+lastIndex+"后面条件="+(position < (parent.getCount()%6 == 0 ? parent.getCount()-6 : parent.getCount()-(parent.getCount()%6))));
//					if((position-parent.getFirstVisiblePosition()) >= 6 && (position-lastIndex) == 6 && position < (parent.getCount()%6 == 0 ? parent.getCount()-6 : parent.getCount()-(parent.getCount()%6))){
//						gv_type_details_grid.post(new Runnable() {
//							@Override
//							public void run() {
//								gv_type_details_grid.smoothScrollBy(310, 500);
//							}
//						});
//					}
//				}else {//向上按
//					Logger.i(TAG, "向上按position="+position+"....parent.getFirstVisiblePosition()="+parent.getFirstVisiblePosition()+"...lastIndex="+lastIndex);
//					if((position-parent.getFirstVisiblePosition()) < 6 && (lastIndex-position) == 6 && parent.getFirstVisiblePosition() != 0){
//						gv_type_details_grid.post(new Runnable() {
//							@Override
//							public void run() {
//								gv_type_details_grid.smoothScrollBy(-310, 500);
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
		processLogic("");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Logger.i(TAG, "KeyEvent.KEYCODE_BACK");
			if(menulayout.getVisibility()==View.VISIBLE){
				menulayout.clearFocus();
				menulayout.setVisibility(View.GONE);
				gv_type_details_grid.setFocusable(true);
				return true;
			}
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			break;
			
		case KeyEvent.KEYCODE_MENU:
			showFilter();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void showFilter() {
		if (menulayout.getVisibility() != View.VISIBLE) {
			menulayout.setVisibility(View.VISIBLE);
			gv_type_details_grid.clearFocus();
			gv_type_details_grid.setFocusable(false);
			menulayout.requestFocus();
			Logger.i(TAG, "menulayout=GONE");
		} else {
			Logger.i(TAG, "menulayout=VISIBIE");
			menulayout.clearFocus();
			menulayout.setVisibility(View.GONE);
			gv_type_details_grid.setFocusable(true);
		}
	}
	/**
	 * 显示提示框
	 */
	protected void showProgressDialog() {
		Utils.loadingShow_tv(VodTypeActivity.this,R.string.str_data_loading);
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
	private ImageView iv_type_details_type;
	private TextView tv_type_details_sum,tv_filter_year;
	private ImageView b_type_details_fliter;
	private GridView gv_type_details_grid;
	private final static int PAGESIZE = 30;
	private int pageindex = 1;
	private int vodpageindex;
	private int totalpage;
	private static String VOD_TYPE;
	private ArrayList<VodDataInfo> vodDatas;
	private VodTypeInfo vodtypeinfo;
	private VodtypeAdapter vodtypeAdapter;
	private String type = null;
	private Context context;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<VodFilterInfo> vodFilter;
	private ListView filter_list_type, filter_list_year, filter_list_area,
			filter_list_seach;
	private LinearLayout menulayout;
	private List<String> types, years, areas;
	private RequestQueue mQueue;
	private String VOD_DATA = "VOD_DATA";
	private String VOD_FILTER = "VOD_FILTER";
	private int lastIndex = -1;
	private int gHeight;
	private final String TAG = "VodTypeActivity";
}
