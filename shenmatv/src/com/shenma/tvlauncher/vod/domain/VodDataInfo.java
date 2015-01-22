package com.shenma.tvlauncher.vod.domain;

public class VodDataInfo {
		
		private String title;//节目名称
		private String nextlink;//节目详细地址
		private String pic;//节目图片地址
		private String state;//节目状态
		private String type;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getNextlink() {
			return nextlink;
		}
		public void setNextlink(String nextlink) {
			this.nextlink = nextlink;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@Override
		public String toString() {
			return "VodDataInfo [title=" + title + ", nextlink=" + nextlink
					+ ", pic=" + pic + ", state=" + state + ", type=" + type
					+ "]";
		}
		
	}
