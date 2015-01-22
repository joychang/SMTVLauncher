package com.shenma.tvlauncher.vod.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @author joychang
 * 
 */
public class VideoDetailInfo {

	private String id;// vodID
	private String title;// 语言
	private String trunk;
	private String img_url;// 海报地址
	private String intro;// 介绍
	private String is_finish;
	private String pubtime;// 上映年份
	private String cur_episode;// 更新集数
	private String max_episode;// 总集数
	private String season_num;
	private String raing;
	private String play_filter;
	private String foreign_ip;
	private String[] actor;// 演员
	private String[] director;// 导演
	private String[] area;// 地区
	private String[] type;// 类型S
	private AboutInfo about;
	private VideoList videolist;

	public AboutInfo getAbout() {
		return about;
	}

	public void setAbout(AboutInfo about) {
		this.about = about;
	}

	public String[] getActor() {
		return actor;
	}

	public void setActor(String[] actor) {
		this.actor = actor;
	}

	public String[] getDirector() {
		return director;
	}

	public void setDirector(String[] director) {
		this.director = director;
	}

	public String[] getArea() {
		return area;
	}

	public void setArea(String[] area) {
		this.area = area;
	}

	public String[] getType() {
		return type;
	}

	public void setType(String[] type) {
		this.type = type;
	}

	public VideoList getVideolist() {
		return videolist;
	}

	public void setVideolist(VideoList videolist) {
		this.videolist = videolist;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public VideoList getVideolist() {
	// return videolist;
	// }
	// public void setVideolist(VideoList videolist) {
	// this.videolist = videolist;
	// }
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTrunk() {
		return trunk;
	}

	public void setTrunk(String trunk) {
		this.trunk = trunk;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getIs_finish() {
		return is_finish;
	}

	public void setIs_finish(String is_finish) {
		this.is_finish = is_finish;
	}

	public String getPubtime() {
		return pubtime;
	}

	public void setPubtime(String pubtime) {
		this.pubtime = pubtime;
	}

	public String getCur_episode() {
		return cur_episode;
	}

	public void setCur_episode(String cur_episode) {
		this.cur_episode = cur_episode;
	}

	public String getMax_episode() {
		return max_episode;
	}

	public void setMax_episode(String max_episode) {
		this.max_episode = max_episode;
	}

	public String getSeason_num() {
		return season_num;
	}

	public void setSeason_num(String season_num) {
		this.season_num = season_num;
	}

	public String getRaing() {
		return raing;
	}

	public void setRaing(String raing) {
		this.raing = raing;
	}

	public String getPlay_filter() {
		return play_filter;
	}

	public void setPlay_filter(String play_filter) {
		this.play_filter = play_filter;
	}

	public String getForeign_ip() {
		return foreign_ip;
	}

	public void setForeign_ip(String foreign_ip) {
		this.foreign_ip = foreign_ip;
	}

	@Override
	public String toString() {
		return "VideoDetailInfo [id=" + id + ", title=" + title + ", trunk="
				+ trunk + ", img_url=" + img_url + ", intro=" + intro
				+ ", is_finish=" + is_finish + ", pubtime=" + pubtime
				+ ", cur_episode=" + cur_episode + ", max_episode="
				+ max_episode + ", season_num=" + season_num + ", raing="
				+ raing + ", play_filter=" + play_filter + ", foreign_ip="
				+ foreign_ip + ", actor=" + Arrays.toString(actor)
				+ ", director=" + Arrays.toString(director) + ", area="
				+ Arrays.toString(area) + ", type=" + Arrays.toString(type)
				+ ", about=" + about + ", videolist=" + videolist + "]";
	}

	/*
	 * "id": "1090", "title": "Another日语", "trunk": "Another", "img_url":
	 * "http://t2.baidu.com/it/u=3336384440,3102309761&fm=20", "intro":
	 * "26年前夜见山北中学的三年三班里，曾经有一位名叫「见崎」的学生。她不仅是位运动天才还是个优等生，个性平易近人广受众人喜爱的她却在某日突然猝死，极度悲伤的同班同学们无法接受这件事实，众人决定到毕业之前仍想像见崎还活著般度过每一天。因此见崎的座位就这样一大早上课会有人来打招呼，偶而还会有人藉机搭话——但这段美丽的插曲却对三年三班带来某种扭曲的现象。而在1998年春天，转到三年三班的榊原恒一，莫名感到班上的气氛持续著不安感。在这样的班级里，有位行事异于常人，孤高的美少女见崎鸣——一只眼睛遮著眼罩总是独自一人在画画，拥有不可思议的存在感让恒一不禁想试著接近她，但却就此陷入更深的谜团之中。随著故事的进展，班上的气氛越来越紧张，也有越来越多的人不知是巧合还是「诅咒」而意外生亡。在绫辻大师的笔触下，恐怖的气氛越发浓厚。"
	 * , "is_finish": "1", "pubtime": "2012", "cur_episode": "13",
	 * "max_episode": "12", "season_num": 0, "area": [ "日本" ],
	 */

}
