package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;
import java.util.List;
/**
 * 筛选类型
 * @author joychang
 *
 */
public class VodFilter implements Serializable{
	
	private List<VodFilterInfo> tvplay;
	private List<VodFilterInfo> comic;
	private List<VodFilterInfo> tvshow;
	private List<VodFilterInfo> documentary;
	private List<VodFilterInfo> teach;
	private List<VodFilterInfo> movie;
	/**
	 * @return the tvplay
	 */
	public List<VodFilterInfo> getTvplay() {
		return tvplay;
	}
	/**
	 * @param tvplay the tvplay to set
	 */
	public void setTvplay(List<VodFilterInfo> tvplay) {
		this.tvplay = tvplay;
	}
	/**
	 * @return the comic
	 */
	public List<VodFilterInfo> getComic() {
		return comic;
	}
	/**
	 * @param comic the comic to set
	 */
	public void setComic(List<VodFilterInfo> comic) {
		this.comic = comic;
	}
	/**
	 * @return the tvshow
	 */
	public List<VodFilterInfo> getTvshow() {
		return tvshow;
	}
	/**
	 * @param tvshow the tvshow to set
	 */
	public void setTvshow(List<VodFilterInfo> tvshow) {
		this.tvshow = tvshow;
	}
	/**
	 * @return the documentary
	 */
	public List<VodFilterInfo> getDocumentary() {
		return documentary;
	}
	/**
	 * @param documentary the documentary to set
	 */
	public void setDocumentary(List<VodFilterInfo> documentary) {
		this.documentary = documentary;
	}
	/**
	 * @return the teach
	 */
	public List<VodFilterInfo> getTeach() {
		return teach;
	}
	/**
	 * @param teach the teach to set
	 */
	public void setTeach(List<VodFilterInfo> teach) {
		this.teach = teach;
	}
	/**
	 * @return the movie
	 */
	public List<VodFilterInfo> getMovie() {
		return movie;
	}
	/**
	 * @param movie the movie to set
	 */
	public void setMovie(List<VodFilterInfo> movie) {
		this.movie = movie;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VodFilter [tvplay=" + tvplay + ", comic=" + comic + ", tvshow="
				+ tvshow + ", documentary=" + documentary + ", teach=" + teach
				+ ", movie=" + movie + "]";
	}
	
}

