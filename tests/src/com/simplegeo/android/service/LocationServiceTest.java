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
package com.simplegeo.android.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.test.ServiceTestCase;

import com.simplegeo.android.test.TestEnvironment;
import com.simplegeo.client.SimpleGeoClient;
import com.simplegeo.client.model.Region;
import com.simplegeo.client.types.Point;

public class LocationServiceTest extends ServiceTestCase<LocationService> {

    public LocationServiceTest() {
        super(LocationService.class);
    }
    
    @Override
    public void setUp() throws Exception {
    	SimpleGeoClient client = SimpleGeoClient.getInstance();
    	client.getHttpClient().setToken(TestEnvironment.getKey(), TestEnvironment.getSecret());
    	client.futureTask = false;
    	super.setUp();
    }
        
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);
        assertNotNull(getService());
    }
    
    public void testBindable() {
    	Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        IBinder service = bindService(startIntent);
        assertNotNull(service);        
    }
    
    public void testRegionUpdates() throws ClientProtocolException, IOException, JSONException {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), LocationService.class);
        startService(startIntent);

    	LocationService locationService = getService();
    	Map<Point, JSONArray> stages = getStages();
    	
    	final List<Region> enteredRegions = new ArrayList<Region>();
    	final List<Region> exitedRegions = new ArrayList<Region>();
    	LocationHandler locationHandler = new LocationHandler() {
    		@Override
    		public void onRegionsEntered(List<Region> regions, Location fromLocation, Location toLocation) {
    			enteredRegions.addAll(regions);
    		}
    		
    		@Override
    		public void onRegionsExited(List<Region> regions, Location fromLocation, Location toLocation) {
    			exitedRegions.addAll(regions);
    		}
    	};
    	
    	locationService.addHandler(locationHandler);
    	for(Point point : stages.keySet()) {
    		enteredRegions.clear(); exitedRegions.clear();
    		
    		List<Region> previousRegionSet = locationService.getRegions();
    		
    		Location location = new Location("");
    		location.setLatitude(point.getLatitude());
    		location.setLongitude(point.getLongitude());
    		locationService.onLocationChanged(location);
    		
    		List<Region> currentRegionSet = Region.getRegions(stages.get(point));
    		List<Region> difference = Region.difference(previousRegionSet, currentRegionSet);
    		assertRegionsEqual(difference, exitedRegions);
    		
    		difference.clear();
    		for(Region region : currentRegionSet)
    			if(!region.contained(previousRegionSet))
    				difference.add(region);

    		assertRegionsEqual(difference, enteredRegions);
    	}
    }
    
    public Map<Point, JSONArray> getStages() throws ClientProtocolException, IOException {
    	List<Point> points = new ArrayList<Point>();
    	points.add(new Point(34.052659, -118.388672));
    	points.add(new Point(34.052659, -118.388672));
    	points.add(new Point(34.082522, -118.38672));
    	points.add(new Point(39.96028, -105.292969));
    	
    	SimpleGeoClient client = SimpleGeoClient.getInstance();
    	
    	Map<Point, JSONArray> stages = new HashMap<Point, JSONArray>();
    	for(Point point : points)
    		stages.put(point, (JSONArray)client.contains(point.getLatitude(), point.getLongitude()));
    		
    	return stages;
    }
    
    private void assertRegionsEqual(List<Region> regionSetOne, List<Region> regionSetTwo) {
    	if(regionSetOne == null && regionSetTwo == null)
    		assertEquals(regionSetOne, regionSetTwo);
    	else {
    		assertNotNull(regionSetOne);
    		assertNotNull(regionSetTwo);
    		assertEquals(regionSetOne.size(), regionSetTwo.size());
    		
    		for(int i = 0; i < regionSetOne.size(); i++)
    			assertTrue(regionSetOne.get(i).equals(regionSetTwo.get(i)));
    	}
    }
}
