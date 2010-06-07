package com.simplegeo.android.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommitLog {
	
	private CacheHandler cacheHandler = null;
	
	public CommitLog(String path, String username) {
		cacheHandler = new CacheHandler(path, "commitlog-" + username);
	}
	
	public void commit(String key, String commit) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.changeDirectory(key);
		cacheHandler.setValue(getCommitKey(), commit);
	}

	public Map<String, List<String>> getAllCommits() {
		cacheHandler.changeToParentDirectory();
		List<String> keys = cacheHandler.getKeys();
		Map<String, List<String>> commits = new HashMap<String, List<String>>();
		for(String key : keys)
			commits.put(key, getCommits(key));
			
		return commits;
	}
	
	public List<String> getCommits(String key) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.changeDirectory(key);
		return cacheHandler.getAllValues();
	}
	
	public void deleteCommits(String key) {
		cacheHandler.changeToParentDirectory();
		cacheHandler.delete(key);
	}
	
	public void deleteAllCommits() {
		cacheHandler.deleteAll();
	}
	
	private String getCommitKey() {
		return "commit-" + System.currentTimeMillis();
	}

}
