package com.simplegeo.android.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;

public class CacheHandler {
	
	private static final String TAG = CacheHandler.class.getCanonicalName();
	
	private Timer flushTimer = null;
	private long flushTimeInterval = 60;
	private String absoluteFile = null; 
	private Map<String, String> data = null;
	
	public static long ttl =  604800;
	
	public CacheHandler(String fileName, String cachePath) {
		File cacheDir = new File(cachePath + File.separator + fileName);
		if(!cacheDir.exists())
			cacheDir.mkdir();
		
		absoluteFile = cacheDir.getAbsolutePath();
	
		deleteStaleCacheFiles(absoluteFile);
		reload();
		startFlushTimer();
	}
		
	public void startFlushTimer() {
		if(flushTimer == null) {
			Log.d(TAG, "starting the flush timer");
			
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
			Log.d(TAG, "stoping the flush timer");
			
			flushTimer.cancel();
			flushTimer = null;
		}
	}
	
	public void flush() {
		Log.d(TAG, "flushing the cache handler");
		
		for(String key : data.keySet()) {
			String value = data.get(key);
			String path = absoluteFile + File.separator + key;
			try {
				File file = new File(path);
				if(!file.exists() && !file.createNewFile())
					Log.e(TAG, "unable to create file at " + path);
				
				FileOutputStream fileOutputStream = new FileOutputStream(path);
				fileOutputStream.write(value.getBytes());
				fileOutputStream.close();
				fileOutputStream = null;
				data.remove(key);

			} catch (FileNotFoundException e) {
				Log.e(TAG, e.getLocalizedMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}

		data = new HashMap<String, String>();
	}
	
	public void setValue(String key, String value) {
		data.put(key, value);
	}
	
	public String getValue(String key) {
		return data.get(key);
	}
	
	public void deleteAll() {
		data = new HashMap<String, String>();
		File file = new File(absoluteFile);
		file.delete();
		file.mkdir();
	}
	
	public void delete(String key) {
		data.remove(key);
		File fileToDelete = new File(absoluteFile + File.separator + key);
		if(fileToDelete.exists() && !fileToDelete.delete())
			Log.e(TAG, "unable to delete file at " + fileToDelete.getAbsolutePath());
	}
	
	public void reload() {
		data = new HashMap<String, String>();
		File file = new File(absoluteFile);
		File[] children = file.listFiles();
		for(File child : children) {
			if(file.isFile()) {
				try {

					FileInputStream inputStream = new FileInputStream(child);
					byte[] buffer = new byte[inputStream.available()];
					inputStream.read(buffer);
					data.put(file.getName(), new String(buffer));

				} catch (FileNotFoundException e) {
					Log.e(TAG, e.getLocalizedMessage());
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
		}
	}
	
	public void deleteStaleCacheFiles(String path) {
		Log.d(TAG, "deleting stale files at " + path);
		
		long currentTime = System.currentTimeMillis();
		
		File file = new File(path);
		File[] subdirectories = file.listFiles();
		for(File subdirectory : subdirectories) {
			if(subdirectory.isDirectory()) {
				deleteStaleCacheFiles(subdirectory.getAbsolutePath());
			} else if(subdirectory.isFile()) {
				if(currentTime - subdirectory.lastModified() < ttl &&
						!subdirectory.delete())
					Log.e(TAG, "unable to delete file at " + subdirectory.getAbsolutePath());		
			}
		}
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
