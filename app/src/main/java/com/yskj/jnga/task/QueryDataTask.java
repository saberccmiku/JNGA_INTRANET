package com.yskj.jnga.task;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public abstract class QueryDataTask extends TimerTask {

	private Timer timer;
	private long startTime;
	private long endTime;
	private String tag;
	private QueryDataListener qdl;


	public QueryDataTask(Timer timer, String tag) {
		this.timer = timer;
		this.tag = tag;
		startTime = System.currentTimeMillis();
	}
	
	public QueryDataTask(Timer timer, String tag,QueryDataListener qdl) {
		
		this.timer = timer;
		this.qdl = qdl;
		this.tag = tag;
		startTime = System.currentTimeMillis();
	}
	
	
	@Override
	public void run() {
		doingTask();
		endTime = System.currentTimeMillis();
		if ((endTime - startTime) / 1000 > 20) {
			timer.cancel();
			if(qdl!=null){
				qdl.onCompleted();
			}
			Log.v(tag, tag + ":查询完毕未得到结果");
		}

	}

	public abstract void doingTask();

	public interface QueryDataListener {
		public void onCompleted();
	}

}
