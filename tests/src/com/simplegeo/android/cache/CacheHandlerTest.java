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

import java.util.List;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class CacheHandlerTest extends AndroidTestCase {
	
	private CacheHandler cacheHandler;
	
	public void setUp() {
		cacheHandler = new CacheHandler(mContext.getCacheDir().getAbsolutePath(), "simplegeo_test");
	}
	
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
