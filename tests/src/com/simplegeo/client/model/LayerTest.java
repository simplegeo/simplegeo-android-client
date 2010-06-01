/**
 * Copyright 2010 SimpleGeo. All rights reserved.
 */
package com.simplegeo.client.model;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.simplegeo.client.test.TestEnvironment;
import com.simplegeo.client.SimpleGeoClient;
import com.simplegeo.client.test.ModelHelperTest;


/**
 * @author Derek Smith
 *
 */
public class LayerTest extends ModelHelperTest {

	private Layer testingLayer;
	
	public void setUp() throws Exception {
		
		SimpleGeoClient.getInstance().getHttpClient().setToken(TestEnvironment.getKey(), TestEnvironment.getSecret());
		testingLayer = new Layer(TestEnvironment.getLayer());
		
	}

	public void tearDown() {
		
		SimpleGeoClient locationService = SimpleGeoClient.getInstance();
		locationService.futureTask = false;
		
		List<IRecord> records = testingLayer.getRecords();
		
		try {
			
			for(IRecord record : records)
				locationService.delete(record);
			
		} catch (ClientProtocolException e) {
			assertTrue(e.getMessage(), false);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);	
		}
	}

	public void testLayerRetrieval() throws Exception {
		
		
		testingLayer.add(getRandomDefaultRecordList(50));
		try {
			
			testingLayer.update();
			
		} catch (ClientProtocolException e) {
			assertTrue(e.getMessage(), false);
		} catch (IOException e) {
			assertTrue(e.getMessage(), false);
		}
		
		
	}
}
