package com.shenma.tvlauncher.vod.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
/**
 * 筛选参数
 * @author joychang
 *
 */
public class VodFilterInfo implements Serializable{
	private String name;
	private String field;
	private String[] values;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	@Override
	public String toString() {
		return "VodFilterInfo [name=" + name + ", field=" + field + ", values="
				+ Arrays.toString(values) + "]";
	}

}

