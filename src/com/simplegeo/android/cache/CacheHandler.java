/**
 * Copyright (c) 2009-2010, SimpleGeo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 
 * Neither the name of the SimpleGeo nor the names of its contributors may
 * be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.simplegeo.android.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class has the ability to traverse a directory structure using a given
 * path and write blocks of data in the form of a file.
 * 
 * @author dsmith
 */
public class CacheHandler {
	
	private static final String TAG = CacheHandler.class.getCanonicalName();
	
	private Timer flushTimer = null;
	private long flushTimeInterval = 60;
	private String absoluteFile = null;
	private String currentPath = null;
	private JSONObject data = null;
	
	// I miss real pointers
	private JSONObject parentData = null;
	private JSONObject currentData = null;
	
	/**
	 * If a file has been modified within the given amount of time,
	 * then it will be deleted when {@link com.simplegeo.android.cache.CacheHandler#deleteStaleCacheFiles(String)}
	 * is called.
	 */
	public static long ttl =  604800;
	
	/**
	 * Initializes a new CacheHandler object while creating the cache path
	 * if necessary.
	 * 
	 * @param cachePath the aboslute path to write to
	 * @param fileName the name of the directory to create within the cachePath
	 */
	public CacheHandler(String cachePath, String fileName) {
		File cacheDir = new File(cachePath + File.separator + fileName);
		absoluteFile = cacheDir.getAbsolutePath();
		
		if(!cacheDir.exists() && !cacheDir.mkdirs())
			Log.e(TAG, "unable to create " + absoluteFile);
		
		currentPath = absoluteFile;
		parentData = null;
		data = new JSONObject();
		currentData = data;
	
		deleteStaleCacheFiles(absoluteFile);
		reload();
	}
		
	/**
	 * Initiates the flush timer.
	 */
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
	
	/**
	 * Cancels the flush timer.
	 */
	public void stopFlushTimer() {
		if(flushTimer != null) {
			Log.d(TAG, "stoping the flush timer");
			
			flushTimer.cancel();
			flushTimer = null;
		}
	}
	
	/**
	 * Flushes the entire data memory structure to disk, maintaining
	 * the hierarchical structure.
	 */
	public void flush() {
		Log.d(TAG, "flushing the cache handler");
		flushJSONObject(absoluteFile, data, null);
		data = new JSONObject();
		parentData = null;
		currentData = data;
	}
	
	private void flushJSONObject(String path, JSONObject object, String key) {
		try {
			
			if(key == null) {
				Iterator<String> keys = object.keys();
				while(keys.hasNext())
					flushJSONObject(path, object, keys.next());
				
			} else {
				Log.d(TAG, "flushing " + key);
				Object value = object.get(key);
				String newPath = path + File.separator + key;
				if(value instanceof JSONObject) {
					Iterator<String> keys = object.keys();
					while(keys.hasNext())
						flushJSONObject(path, (JSONObject)value, keys.next());
				} else if(value instanceof String) {
					writeStringToPath((String)value, newPath);
				}			
			}
			
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	private void writeStringToPath(String value, String path) {
		File file = new File(path);
		try {
			if(!file.exists() && !file.createNewFile())
				Log.e(TAG, "unable to create file at " + path);
		
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			fileOutputStream.write(value.getBytes());
			fileOutputStream.close();
			fileOutputStream = null;
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Returns the cache handler to the directory it was initiated at.
	 */
	public void changeToParentDirectory() {
		Log.d(TAG, "changing to parent at " + absoluteFile);
		currentPath = absoluteFile;
		currentData = data;
	}
	
	/**
	 * Returns the cache handler to the specified directory.
	 * 
	 * @param directory the directory to point to
	 */
	public void changeDirectory(String directory) {
		String childPath = currentPath + File.separator + directory;
		File file =  new File(childPath);
		if(!file.exists() && !file.mkdir())
			Log.e(TAG, "unable to create directory " + childPath);
		
		if(file.exists() && file.isDirectory())
			currentPath = childPath;
		
		try {
			
			JSONObject jsonObject = (JSONObject)currentData.get(directory);
			parentData = currentData;
			currentData = jsonObject;
		
		} catch (JSONException e) {
			JSONObject jsonObject = new JSONObject();
			try {
				currentData.put(directory, jsonObject);
				currentData = jsonObject;
			} catch (JSONException e1) {
				Log.e(TAG, e.getLocalizedMessage());			
			}
		}
	}
		
	/**
	 * Sets the value for the given key within the current directory/level.
	 * 
	 * @param key the key
	 * @param value the values
	 */
	public void setValue(String key, String value) {
		try {
			currentData.put(key, value);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		String value = null;
		try {
			value = (String)currentData.get(key);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		
		return value;
	}
	
	/**
	 * @return a list of all the keys defined at the current level/directory
	 */
	public List<String> getKeys() {
		List<String> values = new ArrayList<String>();
		Iterator<String> keys = currentData.keys();
		while(keys.hasNext())
			values.add(keys.next());
			
		return values;
	}
	
	/**
	 * @return a list of all the values created at the current level/directory
	 */
	public List<String> getAllValues() {
		List<String> values = new ArrayList<String>();
		Iterator<String> keys = currentData.keys();
		while(keys.hasNext())
			values.add(getValue(keys.next()));
		
		return values;
	}
	
	/**
	 * Removes all directories and files that have been created by the cache handler. This
	 * also removes all in memeory data structures.
	 */
	public void deleteAll() {
		data = new JSONObject();
		changeToParentDirectory();
		File file = new File(absoluteFile);
		recursiveDelete(file);
		file.mkdir();
	}
	
	private void recursiveDelete(File file) {
		File[] files = file.listFiles();
		for(File subdir : files) {
			if(subdir.isDirectory())
				recursiveDelete(subdir);
			else
				subdir.delete();
		}
	}
	
	/**
	 * Deletes all files, directories and in-memory objects for the given key
	 * at the current path.
	 * 
	 * @param key
	 */
	public void delete(String key) {
		data.remove(key);
		File fileToDelete = new File(absoluteFile + File.separator + key);
		if(fileToDelete.exists() && !fileToDelete.delete())
			Log.e(TAG, "unable to delete file at " + fileToDelete.getAbsolutePath());
	}
	
	/**
	 * Disregards all current, in-memory objects and reloads them from the current
	 * disk representation.
	 */
	public void reload() {
		Log.d(TAG, "reloading data from disk");
		
		data = new JSONObject();
		changeToParentDirectory();
		try {
			loadDirectory(data, absoluteFile);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	private void loadDirectory(JSONObject jsonObject, String path) throws JSONException {
		Log.d(TAG, "reloading directory " + path);
		File file = new File(path);
		if(file.exists()) {
			File[] children = file.listFiles();
			for(File child : children) {
				if(child.isFile()) {
					try {
						FileInputStream inputStream = new FileInputStream(child);
						byte[] buffer = new byte[inputStream.available()];
						inputStream.read(buffer);
						data.put(child.getName(), new String(buffer));
					} catch (FileNotFoundException e) {
						Log.e(TAG, e.getLocalizedMessage());
					} catch (IOException e) {
						Log.e(TAG, e.getLocalizedMessage());
					}
				} else if(child.isDirectory()) {
					JSONObject newJSONObject = new JSONObject();
					jsonObject.put(file.getName(), newJSONObject);
					loadDirectory(newJSONObject, child.getAbsolutePath());
				}
			}
		}
	}
	
	private void deleteStaleCacheFiles(String path) {
		long currentTime = System.currentTimeMillis();
		File file = new File(path);
		if(file.exists()) {
			File[] subdirectories = file.listFiles();
			for(File subdirectory : subdirectories) {
				if(subdirectory.isDirectory()) {
					deleteStaleCacheFiles(subdirectory.getAbsolutePath());
				} else if(subdirectory.isFile()) {
					if(currentTime - subdirectory.lastModified() > ttl &&
							!subdirectory.delete())
						Log.e(TAG, "unable to delete file at " + subdirectory.getAbsolutePath());
					else
						Log.d(TAG, "deleting stale files at " + subdirectory.getAbsolutePath());
				}
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
