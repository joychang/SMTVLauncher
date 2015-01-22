package com.shenma.tvlauncher.vod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.ImageUtil;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.Reflect3DImage;
import com.shenma.tvlauncher.vod.adapter.DetailsBottomGridAdapter;
import com.shenma.tvlauncher.vod.adapter.DetailsBottomListAdapter;
import com.shenma.tvlauncher.vod.adapter.VodDetailsAdapter;
import com.shenma.tvlauncher.vod.dao.VodDao;
import com.shenma.tvlauncher.vod.db.Album;
import com.shenma.tvlauncher.vod.domain.AboutInfo;
import com.shenma.tvlauncher.vod.domain.RequestVo;
import com.shenma.tvlauncher.vod.domain.VideoDetailInfo;
import com.shenma.tvlauncher.vod.domain.VideoInfo;
import com.shenma.tvlauncher.vod.domain.VideoList;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodUrl;
public class VodDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate.....");
		setContentView(R.layout.mv_video_details);
		context = this;
		dao = new VodDao(this);
		initData();
		initView();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Logger.d(TAG, "onStart.....");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		closeProgressDialog();
		if(null!=mQueue){
			mQueue.stop();
		}
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		Logger.d(TAG, "onStop.....");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Logger.d(TAG, "onStop.....");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
		Logger.d(TAG, "onDestroy.....");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Logger.v(TAG, " onResume()获取的videoId="+videoId);
		if(null!=videoId){
			albums = dao.queryAlbumById(videoId,2);
			if(null!=albums && albums.size()>0){
				album = albums.get(0);
			}
			if(null!=album){
				b_details_replay.setVisibility(View.VISIBLE);
				b_details_replay.requestFocus();
				Logger.v(TAG, "onResume()续播playIndex=="+album.getPlayIndex()+"...collectionTime=="+album.getCollectionTime());
			}
		}
		super.onResume();
	}
	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		vodtype = intent.getStringExtra("vodtype");
		vodstate = intent.getStringExtra("vodstate");
		nextlink = intent.getStringExtra("nextlink");
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.hao260x366)
				// 默认图片
				.showImageForEmptyUri(R.drawable.hao260x366)
				.showImageOnFail(R.drawable.hao260x366)
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		rbWidth = getResources().getDimensionPixelSize(R.dimen.sm_90);
		rbHeigth = getResources().getDimensionPixelSize(R.dimen.sm_36);
		findViewById();
		loadViewLayout();
		setListener();
		processLogic();
	}

	protected void findViewById() {

		iv_details_poster = (ImageView) findViewById(R.id.iv_details_poster);
//		Drawable drawable = iv_details_poster.getDrawable();
//		Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
//		Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
//		iv_details_poster.setImageBitmap(bit);
		//iv_details_sharpness = (ImageView) findViewById(R.id.iv_details_sharpness);
		tv_details_name = (TextView) findViewById(R.id.tv_details_name);
		tv_details_year = (TextView) findViewById(R.id.tv_details_year);
		tv_details_rate = (TextView) findViewById(R.id.tv_details_rate);
		tv_details_director = (TextView) findViewById(R.id.tv_details_director);
		tv_details_type = (TextView) findViewById(R.id.tv_details_type);
		tv_details_actors = (TextView) findViewById(R.id.tv_details_actors);
		tv_details_playTimes = (TextView) findViewById(R.id.tv_details_playTimes);
		tv_details_area = (TextView) findViewById(R.id.tv_details_area);
		tv_details_video_introduce = (TextView) findViewById(R.id.tv_details_video_introduce);
		b_details_replay = (Button) findViewById(R.id.b_details_replay);
		b_details_play = (Button) findViewById(R.id.b_details_play);
		b_details_choose = (Button) findViewById(R.id.b_details_choose);
		b_details_favicon = (Button) findViewById(R.id.b_details_favicon);
		b_details_colection = (Button) findViewById(R.id.b_details_colection);
		b_details_introduce = (Button) findViewById(R.id.b_details_introduce);
		details_recommend = (LinearLayout) findViewById(R.id.details_recommend);
		gv_recommend_grid = (GridView) findViewById(R.id.recommend_grid);
		gv_recommend_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		details_key_arts = (LinearLayout) findViewById(R.id.details_key_arts);
		details_video_introduce = (LinearLayout) findViewById(R.id.details_video_introduce);
		details_key_list = (ListView) findViewById(R.id.details_key_list);
		details_key_list.setSelector(new ColorDrawable(0));
		details_key_grid = (GridView) findViewById(R.id.details_key_grid);
		details_key_grid.setSelector(new ColorDrawable(0));
		rg_video_details_resources = (RadioGroup) findViewById(R.id.rg_video_details_resources);
		if(vodtype.equals("MOVIE")){
			b_details_favicon.setVisibility(View.VISIBLE);
			b_details_choose.setVisibility(View.GONE);
		}else{
			b_details_colection.setVisibility(View.VISIBLE);
			b_details_choose.setVisibility(View.VISIBLE);
		}
		//b_details_play.requestFocus();
		
	}

	protected void loadViewLayout() {

	}
	
	protected void CreateBottomLayout(){
		if(null!=now_source&&now_source.size()>0){
			gv_lists = Utils.getVideogvDatas(now_source,false);
			bg_Adapter = new DetailsBottomGridAdapter(VodDetailsActivity.this, gv_lists);
			details_key_grid.setAdapter(bg_Adapter);

			lv_lists = Utils.getVideolvDatas(now_source,0);
			bl_Adapter = new DetailsBottomListAdapter(VodDetailsActivity.this, lv_lists);
			details_key_list.setAdapter(bl_Adapter);
		}
		details_key_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gv_postion = position;
				lv_lists = Utils.getVideolvDatas(now_source,position);
				bl_Adapter.changData(lv_lists);
			}
			
		});
		details_key_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ArrayList<VideoInfo> videoinfo = new ArrayList<VideoInfo>();
				for(int i = 0;i<now_source.size();i++){
					VideoInfo vinfo = new VideoInfo();
					vinfo.title = now_source.get(i).getTitle();
					vinfo.url = now_source.get(i).getUrl();
					videoinfo.add(vinfo);
				}
				if(null!=videoinfo&&videoinfo.size()>0){
					Intent intent = null;
					intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
//					if(null!=domain && domain.contains("pps")||domain.contains("qiyi")){
//						intent = new Intent(VodDetailsActivity.this,MeidaActivity.class);
//					}else{
//						intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
//					}
					intent.putParcelableArrayListExtra("videoinfo",videoinfo);//数据集合
					intent.putExtra("albumPic",albumPic);//图片路径
					intent.putExtra("vodtype", vodtype);//影片类型
					intent.putExtra("vodstate", vodstate);//影片情势
					intent.putExtra("nextlink", nextlink);
					intent.putExtra("videoId", videoId);//影片ID
					intent.putExtra("vodname", vodname);//电影的名字
					intent.putExtra("sourceId", sourceId);//源id
					intent.putExtra("domain", domain);//domain
					intent.putExtra("playIndex",gv_postion*10+position);//剧集标
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				}else{
					Utils.showToast(context,
							"对不起！没有找到数据源，请切换其它源。",
							R.drawable.toast_err);
				}
			}
			
		});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		if(details_key_arts.getVisibility()==View.VISIBLE||details_video_introduce.getVisibility()==View.VISIBLE){
			details_video_introduce.setVisibility(View.GONE);
			details_key_arts.setVisibility(View.GONE);
			details_recommend.setVisibility(View.VISIBLE);
		}else{
			finish();
		}
	}

	protected void setListener() {
		//播放
		b_details_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//将播放数据传给播放器
				ArrayList<VideoInfo> videoinfo = new ArrayList<VideoInfo>();
				Logger.v(TAG, "now_source=="+now_source);
				if(null==now_source||sourceId==null){
					Utils.showToast(context,"对不起！当前影视没有数据源。",R.drawable.toast_err);
					return;
				}else{
					for(int i = 0;i<now_source.size();i++){
						VideoInfo vinfo = new VideoInfo();
						vinfo.title = now_source.get(i).getTitle();
						vinfo.url = now_source.get(i).getUrl();
						videoinfo.add(vinfo);
					}
				}
				if(null!=videoinfo&&videoinfo.size()>0){
					Intent intent = null;
//					if(null!=domain && domain.contains("pps")||domain.contains("qiyi")){
//						intent = new Intent(VodDetailsActivity.this,MeidaActivity.class);
//					}else{
//						intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
//					}		
					intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
					intent.putParcelableArrayListExtra("videoinfo",videoinfo);//数据集合
					intent.putExtra("albumPic",albumPic);//图片路径
					intent.putExtra("vodtype", vodtype);//影片类型
					intent.putExtra("vodstate", vodstate);//影片情势
					intent.putExtra("videoId", videoId);//影片ID
					intent.putExtra("nextlink", nextlink);
					intent.putExtra("vodname", vodname);//电影的名字
					intent.putExtra("sourceId", sourceId);//源id
					intent.putExtra("domain", domain);//domain
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				}else{
					Utils.showToast(context,
							"对不起！没有找到数据源，请切换其它源。",
							R.drawable.toast_err);
				}
				//PrepareVodData(0);
			}
		});
		
		b_details_replay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//将播放数据传给播放器
				ArrayList<VideoInfo> videoinfo = new ArrayList<VideoInfo>();
				if(null==now_source||sourceId==null){
					Utils.showToast(VodDetailsActivity.this,
							"对不起！当前影视没有数据源。",
							R.drawable.toast_err);
					return;
				}else{
					for(int i = 0;i<now_source.size();i++){
						VideoInfo vinfo = new VideoInfo();
						vinfo.title = now_source.get(i).getTitle();
						vinfo.url = now_source.get(i).getUrl();
						videoinfo.add(vinfo);
					}
				}
				if(null!=videoinfo&&videoinfo.size()>0){
					Intent intent = null;
//					if(null!=domain && domain.contains("pps")||domain.contains("qiyi")){
//						intent = new Intent(VodDetailsActivity.this,MeidaActivity.class);
//					}else{
//						intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
//					}
					intent = new Intent(VodDetailsActivity.this,VideoPlayerActivity.class);
					intent.putParcelableArrayListExtra("videoinfo",videoinfo);//数据集合
					intent.putExtra("albumPic",albumPic);//图片路径
					intent.putExtra("vodtype", vodtype);//影片类型
					intent.putExtra("vodstate", vodstate);//影片情势
					intent.putExtra("videoId", videoId);//影片ID
					intent.putExtra("vodname", vodname);//电影的名字
					intent.putExtra("sourceId", sourceId);//源id
					intent.putExtra("nextlink", nextlink);
					intent.putExtra("domain", domain);//domain
					intent.putExtra("playIndex", album.getPlayIndex());//剧集标
					intent.putExtra("collectionTime", album.getCollectionTime());//时间点
					Logger.v(TAG, "续播playIndex=="+album.getPlayIndex()+"...collectionTime=="+album.getCollectionTime()+"...videoId="+videoId);
					startActivity(intent);
					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				}else{
					Utils.showToast(VodDetailsActivity.this,
							"对不起！没有找到数据源，请切换其它源。",
							R.drawable.toast_err);
				}
			}
		});
		//选集
		b_details_choose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(details_key_arts.getVisibility()==View.VISIBLE){
					details_key_arts.setVisibility(View.GONE);
					details_video_introduce.setVisibility(View.GONE);
					details_recommend.setVisibility(View.VISIBLE);
				}else{
					details_key_arts.setVisibility(View.VISIBLE);
					details_recommend.setVisibility(View.GONE);
					details_video_introduce.setVisibility(View.GONE);
				}
				CreateBottomLayout();
			}
		});
		//收藏
		b_details_favicon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null!=videoId){
					Boolean b = dao.queryZJById(videoId, 1);
					if(b){
						dao.deleteByWhere(videoId, vodtype, 1);
						b_details_favicon.setBackgroundResource(R.drawable.video_details_favicon_selector);
						Utils.showToast(VodDetailsActivity.this, "取消收藏成功！", R.drawable.toast_smile);
					}else{
						Album al = new Album();
						al.setAlbumId(videoId);
						al.setAlbumType(vodtype);
						al.setTypeId(1);
						al.setAlbumState(vodstate);
						al.setNextLink(nextlink);
						al.setAlbumPic(albumPic);
						al.setAlbumTitle(vodname);
						dao.addAlbums(al);
						b_details_favicon.setBackgroundResource(R.drawable.video_details_yifavicon_selector);
						Utils.showToast(VodDetailsActivity.this, "收藏成功!", R.drawable.toast_smile);
					}
				}
			}
		});
		
		//追剧
		b_details_colection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null!=videoId){
					Boolean b = dao.queryZJById(videoId, 0);
					if(b){
						dao.deleteByWhere(videoId, vodtype, 0);
						b_details_colection.setBackgroundResource(R.drawable.video_details_zhuiju_selector);
						Utils.showToast(VodDetailsActivity.this, "取消追剧成功！", R.drawable.toast_smile);
					}else{
						Album al = new Album();
						al.setAlbumId(videoId);
						al.setAlbumType(vodtype);
						al.setAlbumState(vodstate);
						al.setNextLink(nextlink);
						al.setTypeId(0);
						al.setAlbumPic(albumPic);
						al.setAlbumTitle(vodname);
						dao.addAlbums(al);
						b_details_colection.setBackgroundResource(R.drawable.video_details_yizhuiju_selector);
						Utils.showToast(VodDetailsActivity.this, "追剧成功!", R.drawable.toast_smile);
					}
				}
			}
		});
		b_details_introduce.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(details_video_introduce.getVisibility()==View.VISIBLE){
					details_video_introduce.setVisibility(View.GONE);
					details_recommend.setVisibility(View.VISIBLE);
					details_key_arts.setVisibility(View.GONE);
				}else{
					details_key_arts.setVisibility(View.GONE);
					details_video_introduce.setVisibility(View.VISIBLE);
					details_recommend.setVisibility(View.GONE);
				}
			}
		});
		
		
		/**
		 * 监听视频源的选中
		 */
		rg_video_details_resources.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				//RadioButton rb = (RadioButton) findViewById(checkedId);
				//Logger.v(TAG, "rg_video_details_resources数量="+rg_video_details_resources.getChildCount());
				switch (checkedId) {
				case R.string.vod_qq:
					now_source = qqs;
					domain = "qq_com";
					break;
				case R.string.vod_letv:
					now_source = letvs;
					domain = "letv_com";
					break;
				case R.string.vod_youku:
					now_source = youkus;
					domain = "youku_com";
					break;
				case R.string.vod_sohu:
					now_source = sohus;
					domain = "sohu_com";
					break;
				case R.string.vod_iqiyi:
					now_source = iqiyis;
					domain = "iqiyi_com";
					break;
				case R.string.vod_kankan:
					now_source = kankans;
					domain = "kankan_com";
					break;
				case R.string.vod_pptv:
					now_source = pptvs;
					domain = "pptv_com";
					break;
				case R.string.vod_pps_tv:
					now_source = pps_tvs;
					domain = "pps_tv";
					break;
				case R.string.vod_funshion:
					now_source = funshions;
					domain = "funshion_com";
					break;
				case R.string.vod_hunan:
					now_source = hunans;
					domain = "hunantv_com";
					break;
				case R.string.vod_cntv:
					now_source = cntvs;
					domain = "cntv_cn";
					break;
				case R.string.vod_tudou:
					now_source = tudous;
					domain = "tudou_com";
					break;
				case R.string.vod_baofeng:
					now_source = baofengs;
					domain = "baofeng_com";
					break;
				case R.string.vod_wasus:
					now_source = wasus;
					domain = "wasu_cn";
					break;
					//电影端
				case R.string.vod_m_pps:
					now_source = mppss;
					domain = "pps";
					break;
				case R.string.vod_m_yuku:
					now_source = myukus;
					domain = "yuku";
					break;
				case R.string.vod_m_fengxing:
					now_source = mfengxings;
					domain = "fengxing";
					break;
				case R.string.vod_m_qq:
					now_source = mqqs;
					domain = "qq";
					break;
				case R.string.vod_m_sohu:
					now_source = msohus;
					domain = "sohu";
					break;
				case R.string.vod_m_tv189:
					now_source = mtv189s;
					domain = "tv189";
					break;
				case R.string.vod_m_bdwp:
					now_source = mbdwps;
					domain = "bdwp";
					break;
				case R.string.vod_m_letv:
					now_source = mletvs;
					domain = "letv";
					break;
				case R.string.vod_m_pptv:
					now_source = mpptvs;
					domain = "pptv";
					break;
				case R.string.vod_m_cntv:
					now_source = mcntvs;
					domain = "cntv";
					break;
				case R.string.vod_m_m1905:
					now_source = mm1905s;
					domain = "m1905";
					break;
				case R.string.vod_m_wole:
					now_source = mwoles;
					domain = "wole";
					break;
				case R.string.vod_m_qiyi:
					now_source = mqiyis;
					domain = "qiyi";
					break;
				case R.string.vod_m_tudou:
					now_source = mtudous;
					domain = "tudou";
					break;
				case R.string.vod_m_flv:
					now_source = mflvs;
					domain = "flv";
					break;
				case R.string.vod_m_xunlei:
					now_source = mxunleis;
					domain = "xunlei";
					break;

				}
				//Logger.v(TAG, "rd=="+getString(checkedId)+"domain=="+domain);
				if(vodtype.equals("MOVIE")&&now_source!=null && now_source.size()>1){
					Logger.v(TAG, "now_source数量="+now_source.size());
					b_details_choose.setVisibility(View.VISIBLE);
				}
				sourceId = String.valueOf(checkedId);
				if(null!=album && sourceId.equals(album.getAlbumSourceType())){
					b_details_replay.setVisibility(View.VISIBLE);
				}else{
					b_details_replay.setVisibility(View.GONE);
				}
				if(details_key_arts.getVisibility()==View.VISIBLE){
					CreateBottomLayout();
				}else{
					details_key_arts.setVisibility(View.GONE);
					details_recommend.setVisibility(View.VISIBLE);
				}
			}
			
		});

	}

	/**
	 * 填充RadioGroup数据集
	 */
	private void fillRadioGroup() {
		if(rg_video_details_resources.getChildCount()>0){
			rg_video_details_resources.clearCheck();
			rg_video_details_resources.removeAllViews();
			//rg_video_details_resources.invalidate();
		}
		if(vodtype.equals("MOVIE")||vodtype.equals("DOCUMENTARY")||vodtype.equals("TEACH")){
			//电影端
			mtudous = videos.getTudou();
			mqqs = videos.getQq();
			mxunleis = videos.getXunlei();
			mqiyis = videos.getQiyi();
			msohus = videos.getSohu();
			myukus = videos.getYuku();
			mletvs = videos.getLetv();
			mppss = videos.getPps();
			mpptvs = videos.getPptv();
			mbdwps = videos.getBdwp();
			mcntvs = videos.getCntv_cn();
			mm1905s = videos.getM1905();
			mwoles = videos.getWole();
			mfengxings = videos.getFengxing();
			mtv189s = videos.getTv189();
			mflvs = videos.getFlv();
			
			if (null != mletvs && mletvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_letv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_letv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mqqs && mqqs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_qq_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_qq);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);

			}
			if (null != mqiyis && mqiyis.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_iqiyi_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_qiyi);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != msohus && msohus.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_sohu_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_sohu);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != myukus && myukus.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_youku_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_yuku);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}

			if (null != mbdwps && mbdwps.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_bdyb_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_bdwp);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mflvs && mflvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_flv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_flv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mcntvs && mcntvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_cntv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_cntv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mfengxings && mfengxings.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_funshion_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_fengxing);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mxunleis && mxunleis.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_xunlei_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_xunlei);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mpptvs && mpptvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_pptv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_pptv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mppss && mppss.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_pps_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_pps);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}

			if (null != mm1905s && mm1905s.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_m1905_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_m1905);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}

			if (null != mtudous && mtudous.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_tudou_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_tudou);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mwoles && mwoles.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_56_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_wole);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != mtv189s && mtv189s.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_tv189_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_tv189);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}

		}else{
			//电视剧、综艺、动漫、教育
			tudous = videos.getTudou_com();
			qqs = videos.getQq_com();
			kankans = videos.getKankan_com();
			iqiyis = videos.getIqiyi_com();
			sohus = videos.getSohu_com();
			youkus = videos.getYouku_com();
			letvs = videos.getLetv_com();
			pps_tvs = videos.getPps_tv();
			pptvs = videos.getPptv_com();
			baofengs = videos.getBaofeng_com();
			cntvs = videos.getCntv_cn();
			m1905s = videos.getM1905_com();
			wasus = videos.getWasu_cn();
			funshions = videos.getFunshion_com();
			hunans = videos.getHunantv_com();
			mwoles = videos.getWole();
			if (null != youkus && youkus.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_youku_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_youku);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != letvs && letvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_letv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_letv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			
			if (null != iqiyis && iqiyis.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_iqiyi_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_iqiyi);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			
			if (null != sohus && sohus.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_sohu_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_sohu);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}

			if (null != pps_tvs && pps_tvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_pps_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_pps_tv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != qqs && qqs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_qq_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_qq);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);

			}
			if (null != cntvs && cntvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_cntv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_cntv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != hunans && hunans.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_other_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_hunan);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
				
			}

			if (null != funshions && funshions.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_funshion_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_funshion);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != pptvs && pptvs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_pptv_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_pptv);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != kankans && kankans.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_xunlei_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_kankan);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != m1905s && m1905s.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_m1905_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_m_m1905);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != wasus && wasus.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_other_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_wasus);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != baofengs && baofengs.size() > 0) {
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.source_other_selector);
				rb.setButtonDrawable(R.drawable.detailsource_bg_s);
				rb.setPadding(0, 0, 0, 0);
				rb.setId(R.string.vod_baofeng);
				rg_video_details_resources.addView(rb,rbWidth,rbHeigth);
			}
			if (null != tudous && tudous.size() > 0) {
					RadioButton rb = new RadioButton(this);
					rb.setBackgroundResource(R.drawable.source_tudou_selector);
					rb.setButtonDrawable(R.drawable.detailsource_bg_s);
					rb.setPadding(0, 0, 0, 0);
					rb.setId(R.string.vod_tudou);
					rg_video_details_resources.addView(rb,rbWidth,rbHeigth);//84,30
			}
		}
		
		if(rg_video_details_resources.getChildCount()>0){
			Logger.d(TAG, "rg_video_details_resources.getChildCount()==="+rg_video_details_resources.getChildCount());
			if(null!=album){
				sourceId = album.getAlbumSourceType();
				for(int i = 0;i<rg_video_details_resources.getChildCount();i++){
					if(rg_video_details_resources.getChildAt(i).getId()==Integer.parseInt(sourceId)){
						rg_video_details_resources.check(Integer.parseInt(sourceId));
						Logger.v(TAG, "sourceId="+getString(Integer.parseInt(sourceId)));
					}
				}
			}else{
				Logger.i(TAG, "rg_video_details_resources.getChildAt(0).getId()="+rg_video_details_resources.getChildAt(0).getId());
				rg_video_details_resources.check(rg_video_details_resources.getChildAt(0).getId());
				Logger.i(TAG, "isFocusable="+rg_video_details_resources.getChildAt(0).isFocusable());
				Logger.i(TAG, "isClickable="+rg_video_details_resources.getChildAt(0).isClickable());
				Logger.i(TAG, "isFocused="+rg_video_details_resources.getChildAt(0).isFocused());
				Logger.i(TAG, "isEnabled="+rg_video_details_resources.getChildAt(0).isEnabled());
				Logger.i(TAG, "getDrawableState="+rg_video_details_resources.getChildAt(0).getDrawableState());
				Logger.i(TAG, "isPressed="+rg_video_details_resources.getChildAt(0).isPressed());
				Logger.i(TAG, "isSelected="+rg_video_details_resources.getChildAt(0).isSelected());
			}
		}
	}

	/**
	 * 组拼数据
	 */

	protected void processLogic() {
		RequestVo vo = new RequestVo();
		vo.context = context;
		if (null != nextlink) {
			vo.requestUrl = nextlink;
			Logger.v(TAG, "访问:::" + nextlink);
			getDataFromServer(vo);
		}
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
			GsonRequest<VideoDetailInfo> mVodData = new GsonRequest<VideoDetailInfo>(Method.GET, reqVo.requestUrl,
					VideoDetailInfo.class,createVodDataSuccessListener(),createVodDataErrorListener());
			mQueue.add(mVodData); 
		}
	}
	
	//影视详细数据请求成功
    private Response.Listener<VideoDetailInfo> createVodDataSuccessListener() {
        return new Response.Listener<VideoDetailInfo>() {
            @Override
            public void onResponse(VideoDetailInfo paramObject) {
    			if (null != paramObject) {
    				vodname = paramObject.getTitle();
    				videoId = paramObject.getId();
    				Logger.v(TAG, "服务器获取的videoId="+videoId);
    				albums = dao.queryAlbumById(videoId,2);
    				if(null!=albums && albums.size()>0){
    					album = albums.get(0);
    				}
    				b_details_play.requestFocus();
    				if(null!=album){
    					b_details_replay.setVisibility(View.VISIBLE);
    					b_details_replay.requestFocus();
    					Logger.v(TAG, "续播playIndex=="+album.getPlayIndex()+"...collectionTime=="+album.getCollectionTime());
    				}else{
    					b_details_replay.setVisibility(View.GONE);
    					b_details_play.requestFocus();
    				}
    				tv_details_name.setText(vodname);
    				tv_details_director.setText(Arrays.toString(paramObject.getDirector()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
    				tv_details_type.setText(Arrays.toString(paramObject.getType()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
    				tv_details_actors.setText(Arrays.toString(paramObject.getActor()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
    				tv_details_area.setText(Arrays.toString(paramObject.getArea()).replace("[", "").replace("]", "").replace(",", " / ").replace("null", "暂无"));
    				if(null!=paramObject.getIntro()&&!"".equals(paramObject.getIntro())){
    					//详情
    					tv_details_video_introduce.setText("简介："+paramObject.getIntro().replace("null", "暂无"));
    				}else{
    					tv_details_video_introduce.setText("简介：暂无");
    				}
    				albumPic = paramObject.getImg_url();
    				//imageLoader.displayImage(paramObject.getImg_url(),iv_details_poster, options);
    				imageLoader.displayImage(albumPic,iv_details_poster, options, new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							// TODO Auto-generated method stub
		    				Drawable drawable = iv_details_poster.getDrawable();
		    				Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
		    				Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
		    				iv_details_poster.setImageBitmap(bit);
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							// TODO Auto-generated method stub
		    				Drawable drawable = iv_details_poster.getDrawable();
		    				Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
		    				Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
		    				iv_details_poster.setImageBitmap(bit);
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub
		    				Drawable drawable = iv_details_poster.getDrawable();
		    				Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
		    				Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
		    				iv_details_poster.setImageBitmap(bit);
						}
					});
    				videos = paramObject.getVideolist();
    				if (vodtype.equals("MOVIE")) {
    					//是否收藏
    					b_details_favicon.setBackgroundResource(dao.queryZJById(videoId, 1)?R.drawable.video_details_yifavicon_selector:R.drawable.video_details_favicon_selector);
    					// 电影
    					b_details_choose.setVisibility(View.GONE);
    				}else{
    					//是否追剧
    					Logger.d(TAG,"是否追剧==="+dao.queryZJById(videoId, 0));
    					b_details_colection.setBackgroundResource(dao.queryZJById(videoId, 0)?R.drawable.video_details_yizhuiju_selector:R.drawable.video_details_zhuiju_selector);
    					//电视剧、动漫、综艺
    					tv_details_year.setText(paramObject.getPubtime());
    					tv_details_playTimes.setText(paramObject.getPubtime());
    					if(null!=paramObject.getCur_episode()){
    						String state = "更新至"+ paramObject.getCur_episode().replace("null", "0") + "集";
    						if(null==vodstate || "".equals(vodstate)){
    							vodstate = state;
    						}
    						tv_details_rate.setText(state);	
    					}else{
    						tv_details_rate.setText("");
    					}
    				}
    				AboutInfo about = paramObject.getAbout();
    				if(null!=about){
    					ArrayList<VodDataInfo> similary = (ArrayList<VodDataInfo>) about.getSimilary();
    					ArrayList<VodDataInfo> actor = (ArrayList<VodDataInfo>)about.getActor();
    					if (null != similary && similary.size() > 0) {
    						aboutlist  = similary;
    						vodDetailsAdapter = new VodDetailsAdapter(context,
    								aboutlist, imageLoader);
    						Logger.v(TAG, "similary==" + similary.size());

    					} else if (null != actor && actor.size() > 0) {
    						aboutlist = actor;
    						vodDetailsAdapter = new VodDetailsAdapter(context,
    								aboutlist, imageLoader);
    					}

    				}
    				fillRadioGroup();
    				gv_recommend_grid.setAdapter(vodDetailsAdapter); // 为界面填充数据
    				gv_recommend_grid.setOnItemClickListener(new OnItemClickListener() {

    					@Override
    					public void onItemClick(AdapterView<?> parent, View view,
    							int position, long id) {
    						if(null != aboutlist && aboutlist.size() > 0){
    							nextlink = aboutlist.get(position).getNextlink();
    							album = null;
    							initView();
    							//processLogic();
    						}
    					}
    				});
    			}
    			closeProgressDialog();
            }
        };
     }
    
    //影视详细数据请求失败
    private Response.ErrorListener createVodDataErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				Utils.showToast(context,
						getString(R.string.str_data_loading_error),
						R.drawable.toast_err);
            	if(error instanceof TimeoutError){
            		Logger.e(TAG, "请求超时");
            	}else if(error instanceof AuthFailureError){
            		Logger.e(TAG, "AuthFailureError="+error.toString());
                }
            	closeProgressDialog();
            }
        };
    }



	/**
	 * 显示提示框
	 */
	protected void showProgressDialog() {
		Utils.loadingShow_tv(VodDetailsActivity.this,R.string.str_data_loading);
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
		/**
		 * @brief 播放数据准备OK。
		 */
		public static final int DATA_PREPARE_OK = 0x00000003;
		/**
		 * @brief base64后的数据准备OK。
		 */
		public static final int DATA_BASE64_PREPARE_OK = 0x00000004;
	}

	private final String TAG = "VodDetailsActivity";
	private DisplayImageOptions options;
	private String nextlink = null;
	private String vodtype = null;
	private String vodstate = null;
	private TextView tv_details_name, tv_details_rate, tv_details_director,
			tv_details_type, tv_details_actors, tv_details_playTimes,tv_details_area,
			tv_details_video_introduce, tv_details_year;
	private ImageView iv_details_poster, iv_details_sharpness;
	private Button b_details_replay, b_details_play, b_details_choose,b_details_favicon,
			b_details_colection,b_details_introduce;
	private RadioGroup rg_video_details_resources;
	private VodDetailsAdapter vodDetailsAdapter;
	private VideoList videos = null;
	private Context context;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private String domain;
	private String vodname;
	private List<VodUrl> tudous,qqs,kankans,iqiyis,sohus,letvs,youkus,pps_tvs,
						pptvs,baofengs,cntvs,m1905s,wasus,funshions,hunans;
	private List<VodUrl> mppss,mtudous,mqqs,mxunleis,mqiyis,msohus,myukus,mflvs,
						mletvs,mpptvs,mbdwps,mcntvs,mm1905s,mwoles,mfengxings,mtv189s;
	private List<VodUrl> now_source = null;
	private String sourcetype;//数据源类型
	private String videoId;//
	private LinearLayout details_recommend,details_key_arts,details_video_introduce;
	private ListView details_key_list;
	private GridView details_key_grid,gv_recommend_grid;
	private DetailsBottomGridAdapter bg_Adapter;
	private DetailsBottomListAdapter bl_Adapter;
	private List<String> gv_lists;
	private List<VodUrl> lv_lists;
	private List<Album> albums;
	private Album album = null;
	private String sourceId = null;
	private int gv_postion = 0;
	private ArrayList<VodDataInfo> aboutlist = null;
	private RequestQueue mQueue;
	private VodDao dao;
	private String albumPic;//影片图片路径
	private int rbWidth = 90;
	private int rbHeigth = 36;
}
