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

import android.test.AndroidTestCase;

public class CommitLogTest extends AndroidTestCase {

	// The path needs to be writable by the testing environment
	private CommitLog commitLog;

	public void setUp() {
		commitLog = new CommitLog(mContext.getCacheDir().getAbsolutePath(), "agate");
		commitLog.deleteAllCommits();
	}

	public void testCommit() {
		commitLog.commit("tigers-eye", "brown and black");
		assertEquals(commitLog.getCommits("tigers-eye").size(), 1);
		assertTrue(commitLog.getCommits("tigers-eye").get(0).equals("brown and black"));
		
		assertEquals(commitLog.getCommits("turritella").size(), 0);
		commitLog.commit("turritella", "brown and tan");
		assertEquals(commitLog.getCommits("turritella").size(), 1);
		assertEquals(commitLog.getAllCommits().keySet().size(), 2);
		commitLog.deleteCommits("turritella");
		assertEquals(commitLog.getCommits("turritella").size(), 0);

		commitLog.commit("tigers-eye", "non-banded");
		assertEquals(commitLog.getCommits("tigers-eye").size(), 2);
		assertEquals(commitLog.getAllCommits().get("tigers-eye").size(), 2);		
	}
}