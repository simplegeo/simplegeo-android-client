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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for the {@link com.simplegeo.android.cache.CacheHandler} that controls
 * a three level data structure defined like:
 * 
 * commitlog-${cache_path} -> ${username} -> ${key} -> commit-${current_time}
 * 
 *  This might make it easier to load commits that happened within a certain
 *  time interval.
 * 
 * @author dsmith
 */
public class CommitLog {
	
	private CacheHandler cacheHandler = null;
	
	/**
	 * Initializes a cache handler that will used to do data stores and retrievals.
	 * 
	 * @param path the top-level path where the files will be located
	 * @param username the username that acts as the second-level directory 
	 */
	public CommitLog(String path, String username) {
		cacheHandler = new CacheHandler(path, "commitlog-" + username);
	}
	
	/**
	 * Commits a new value for the given key. The commit value is stored in
	 * at file:
	 * 
	 * commitlog-${cache_path} -> ${username} -> ${key} -> commit-${current_time}
	 * 
	 * @param key the key 
	 * @param commit the commits
	 */
	public void commit(String key, String commit) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.changeDirectory(key);
		cacheHandler.setValue(getCommitKey(), commit);
	}

	/**
	 * @return all the commits for the user
	 */
	public Map<String, List<String>> getAllCommits() {
		cacheHandler.changeToParentDirectory();
		List<String> keys = cacheHandler.getKeys();
		Map<String, List<String>> commits = new HashMap<String, List<String>>();
		for(String key : keys)
			commits.put(key, getCommits(key));
			
		return commits;
	}
	
	/**
	 * @param key the key
	 * @return the commits for the user for a given key
	 */
	public List<String> getCommits(String key) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.changeDirectory(key);
		return cacheHandler.getAllValues();
	}
	
	/**
	 * Deletes all commits that are located within a key.
	 * 
	 * @param key
	 */
	public void deleteCommits(String key) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.delete(key);
	}
	
	/**
	 * Deletes all commits for the user.
	 */
	public void deleteAllCommits() {
		cacheHandler.deleteAll();
	}
	
	private String getCommitKey() {
		return "commit-" + System.currentTimeMillis();
	}

}
