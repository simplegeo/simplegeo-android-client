package com.simplegeo.android.cache;

import android.test.AndroidTestCase;

public class CommitLogTest extends AndroidTestCase {

	private CommitLog commitLog = new CommitLog("/sdcard/testing-commitlog", "agate");

	public void setUp() {
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
