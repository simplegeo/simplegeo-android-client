package com.simplegeo.android.cache;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

public class CacheHandler {
	
	private Timer flushTimer = null;
	private long flushTimeInterval = 60;
	
	public CacheHandler(String fileName, Context context) {
		File cacheDir = context.getCacheDir();
		cacheDir = new File(cacheDir.getAbsoluteFile() + cacheDir.pathSeparator + fileName);
		if(!cacheDir.exists())
			cacheDir.mkdir();
		
	}
		
	public void startFlushTimer() {
		if(flushTimer == null) {
			flushTimer = new Timer("FlushTimer");
			flushTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						flush();
					}
				}, 
				flushTimeInterval);
		}
			
	}
	
	public void stopFlushTimer() {
		if(flushTimer != null) {
			flushTimer.cancel();
			flushTimer = null;
		}
	}
	
	public void flush() {
		
	}
	
	public void setValue(String key, String value) {
		
	}
	
	/**
	 * @return the flushTimeInterval
	 */
	public long getFlushTimeInterval() {
		return flushTimeInterval;
	}

	/**
	 * @param flushTimeInterval the flushTimeInterval to set
	 */
	public void setFlushTimeInterval(long flushTimeInterval) {
		this.flushTimeInterval = flushTimeInterval;
	}
}
