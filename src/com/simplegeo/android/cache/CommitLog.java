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
