package com.shenma.tvlauncher.tvback.domain;
/**
 * @Description 节目Info
 * @author joychang
 *
 */
public class ProgramInfo {
	private String programUrl;
	private String programName;
	private String time;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getProgramUrl() {
		return programUrl;
	}
	public void setProgramUrl(String programUrl) {
		this.programUrl = programUrl;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
}
