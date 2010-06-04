package com.simplegeo.android.cache;

import android.test.AndroidTestCase;

public class CacheHandlerTest extends AndroidTestCase {
	
	private CacheHandler cacheHandler = new CacheHandler("simplegeo_test", "/tmp");;
		
	public void tearDown() {
		cacheHandler.deleteAll();
	}
	
	public void testCacheSetValue() {
		
		cacheHandler.setValue("commit-1", "goldfinger");
		cacheHandler.setValue("commit-2", "boris");
		
		assertTrue(cacheHandler.getValue("commit-1").equals("goldfinger"));
		assertTrue(cacheHandler.getValue("commit-2").equals("boris"));
		
	}
	
	public void testCacheDeleteValue() {
		
		cacheHandler.setValue("commit-1", "goldfinger");
		cacheHandler.setValue("commit-2", "boris");
		
		cacheHandler.delete("commit-1");
		assertNull(cacheHandler.getValue("commit-1"));
		assertTrue(cacheHandler.getValue("commit-2").equals("boris"));
	}
	
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
