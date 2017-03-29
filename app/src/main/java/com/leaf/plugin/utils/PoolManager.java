package com.leaf.plugin.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: PoolManager
 * @Description: TODO(线程池管理)
 * @author wcp
 * @date 2015年1月13日 下午5:04:09
 * 
 */
public class PoolManager {
	private static PoolManager instance = null;
	private static ExecutorService service = null;

	private PoolManager() {
		// 获取系统处理器数量
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num * 2);
	}

	public synchronized static PoolManager create() {
		if (instance == null) {
			instance = new PoolManager();
			// 创建可重复使用的固定的线程

		}
		return instance;
	}

	/**
	 * 在线程池中执行我传进来的任务
	 */
	public void addTask(Runnable run) {
		service.execute(run);
	}
}
