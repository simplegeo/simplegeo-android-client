package com.simplegeo.android.cache;

import java.util.List;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class CacheHandlerTest extends AndroidTestCase {
	
	private CacheHandler cacheHandler = new CacheHandler("/sdcard", "simplegeo_test");
		
	public void tearDown() {
		cacheHandler.deleteAll();
	}
	
	@SmallTest
	public void testCacheSetValue() {
		
		cacheHandler.setValue("commit-1", "goldfinger");
		cacheHandler.setValue("commit-2", "boris");
		
		assertTrue(cacheHandler.getValue("commit-1").equals("goldfinger"));
		assertTrue(cacheHandler.getValue("commit-2").equals("boris"));
	
		List<String> values = cacheHandler.getAllValues();
		assertEquals(values.size(), 2);
	}
	
	@SmallTest
	public void testCacheDeleteValue() {
		
		cacheHandler.setValue("commit-1", "goldfinger");
		cacheHandler.setValue("commit-2", "boris");
		
		cacheHandler.delete("commit-1");
		assertNull(cacheHandler.getValue("commit-1"));
		assertTrue(cacheHandler.getValue("commit-2").equals("boris"));
	}
	
	@SmallTest
	public void testCacheFlush() {

		cacheHandler.setValue("commit-1", "goldfinger");
		cacheHandler.setValue("commit-2", "boris");

		cacheHandler.flush();
		
		assertNull(cacheHandler.getValue("commit-1"));
		assertNull(cacheHandler.getValue("commit-2"));
		
		cacheHandler.reload();
		
		assertTrue(cacheHandler.getValue("commit-1").equals("goldfinger"));
		assertTrue(cacheHandler.getValue("commit-2").equals("boris"));

	}
}
