package com.shenma.tvlauncher.tvlive.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 线程管理
 * @author joychang
 *
 */
public class ThreadPoolManager {
	private ExecutorService service;
	
	private ThreadPoolManager(){
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num*4);
	}
	
	private static ThreadPoolManager manager;
	
	
	public static ThreadPoolManager getInstance(){
		if(manager==null)
		{
			manager= new ThreadPoolManager();
		}
		return manager;
	}
	
	public void addTask(Runnable runnable){
		service.submit(runnable);
	}
	
	public void removeTask(){
		service.shutdown();
	}
	
}
