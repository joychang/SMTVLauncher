package com.shenma.tvlauncher.utils;

public interface StatusCallBack {
	/**
	 * 执行结果
	 * @param result 成功/失败
	 * @param packageName 包名
	 */
	public void onResult(boolean result, String packageName);
}
