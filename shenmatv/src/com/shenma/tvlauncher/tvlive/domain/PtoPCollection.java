package com.shenma.tvlauncher.tvlive.domain;

import java.util.ArrayList;

import com.lisen.Encoder;
import com.shenma.tvlauncher.tvlive.network.LiveConstant;

import android.util.Log;
public class PtoPCollection {
	public static final int HANGKON_TV = 1;
	public static final int LOCK_CODE_TV = 2;
	public static final int FAVORITE = 3;
	public static final int UNKNOW_TV = 4;

	private ArrayList<PtoP> p2p = null;

	private int tvType = UNKNOW_TV;

	private int playPosition = -1;

	public void copyTo(PtoPCollection mPtoPCollection) {
		mPtoPCollection.setPlayPosition(this.playPosition);
		mPtoPCollection.getP2p().clear();
		mPtoPCollection.getP2p().addAll(this.p2p);
		mPtoPCollection.setTvType(this.tvType);
	}

	public int getTvType() {
		return tvType;
	}

	public void setTvType(int tvType) {
		this.tvType = tvType;
	}

	public PtoPCollection() {
		this.p2p = new ArrayList<PtoP>();
	}

	public int getPlayPosition() {
		return playPosition;
	}

	public void setPlayPosition(int playPosition) {
		this.playPosition = playPosition;
	}

	public ArrayList<PtoP> getP2p() {
		return p2p;
	}

	public void setP2p(ArrayList<PtoP> p2p) {
		this.p2p = p2p;
	}

	public String getServicePath(int position) {

		if (p2p.get(position).getLinkid() == null
				&& p2p.get(position).getServiceId() == null
				|| p2p.get(position).getLinkid().length() == 0
				&& p2p.get(position).getServiceId().length() == 0
				|| p2p.get(position).getLinkid().equals("")
				&& p2p.get(position).getServiceId().equals("")) {
			// return "http://127.0.0.1:9906/api?func=switch_chan&id="
			// + p2p.get(position).getFilmId() + "&link="
			// + p2p.get(position).getLinkid()
			// + "&userid=&flag=&monitor=&path=&file=&server="
			// + p2p.get(position).getServiceId();

//			Log.i("id", "userid1===" + Constant.MAC);
			LiveConstant.PTOP_SERVER=p2p.get(position).getServiceId();
			return "http://127.0.0.1:9906/api?func=switch_chan&id="
					+ p2p.get(position).getFilmId() + "&link="
					+ p2p.get(position).getLinkid() + "&userid="
					+ "&flag=&monitor=&path=&file=&server="
					+ p2p.get(position).getServiceId();
		}

		if (p2p.get(position).getLinkid() == null
				|| p2p.get(position).getLinkid().length() == 0
				|| p2p.get(position).getLinkid().equals("")) {
			// return "http://127.0.0.1:9906/api?func=switch_chan&id="
			// + p2p.get(position).getFilmId() + "&link="
			// + p2p.get(position).getLinkid()
			// + "&userid=&flag=&monitor=&path=&file=&server="
			// + Encoder.getDecode(p2p.get(position).getServiceId());

			LiveConstant.PTOP_SERVER=Encoder.getDecode(p2p.get(position).getServiceId());
			return "http://127.0.0.1:9906/api?func=switch_chan&id="
					+ p2p.get(position).getFilmId() + "&link="
					+ p2p.get(position).getLinkid() + "&userid=" 
					+ "&flag=&monitor=&path=&file=&server="
					+ Encoder.getDecode(p2p.get(position).getServiceId());
		}
		if (p2p.get(position).getServiceId() == null
				|| p2p.get(position).getServiceId().length() == 0
				|| p2p.get(position).getServiceId().equals("")) {
			// return "http://127.0.0.1:9906/api?func=switch_chan&id="
			// + p2p.get(position).getFilmId() + "&link="
			// + Encoder.getDecode(p2p.get(position).getLinkid())
			// + "&userid=&flag=&monitor=&path=&file=&server="
			// + p2p.get(position).getServiceId();

			LiveConstant.PTOP_SERVER=p2p.get(position).getServiceId();
			return "http://127.0.0.1:9906/api?func=switch_chan&id="
					+ p2p.get(position).getFilmId() + "&link="
					+ Encoder.getDecode(p2p.get(position).getLinkid())
					+ "&userid=" 
					+ "&flag=&monitor=&path=&file=&server="
					+ p2p.get(position).getServiceId();

		}
		// return "http://127.0.0.1:9906/api?func=switch_chan&id="
		// + p2p.get(position).getFilmId() + "&link="
		// + Encoder.getDecode(p2p.get(position).getLinkid())
		// + "&userid=&flag=&monitor=&path=&file=&server="
		// + Encoder.getDecode(p2p.get(position).getServiceId());

		LiveConstant.PTOP_SERVER=Encoder.getDecode(p2p.get(position).getServiceId());
		return "http://127.0.0.1:9906/api?func=switch_chan&id="
				+ p2p.get(position).getFilmId() + "&link="
				+ Encoder.getDecode(p2p.get(position).getLinkid()) + "&userid="
			    + "&flag=&monitor=&path=&file=&server="
				+ Encoder.getDecode(p2p.get(position).getServiceId());

	}

	
	public String getFilmUrl(String ip,int position){
		
		return "http://"+ip+":9906/"+p2p.get(position).getFilmId()+".ts";
		
	}
	
	public String getFilmPath(int position) {
		
		return "http://127.0.0.1:9906/" + p2p.get(position).getFilmId() + ".ts";
	}

	@Override
	public String toString() {
		return "PtoPCollection [p2p=" + p2p + "]";
	}
}
