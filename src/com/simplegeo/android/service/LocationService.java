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
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.simplegeo.client.SimpleGeoClient;
import com.simplegeo.client.model.IRecord;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener {
	
	public static final String MIN_TIME = "minimum_time";
	public static final String MIN_DISTANCE = "minimun_distance";
	
	private long minTime;
	private float minDistance;
	
	private boolean cacheUpdates;
	
	private List<ILocationHandler> locationHandlers = new ArrayList<ILocationHandler>();

    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    
    private final LocationBinder locationBinder = new LocationBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		minTime = intent.getLongExtra(MIN_TIME, 0);
		minDistance = intent.getFloatExtra(MIN_DISTANCE, 0.0f);		
		return locationBinder;
	}
	
    @Override
    public void onCreate() {
		updateProviders();
    }	
    
    @Override
    public void onDestroy() {
    	
    }

    public void updateProviders() {
    	updateProviders(null);
    }
    
    public void addLocationHandler(ILocationHandler locationHandler) {
    	locationHandlers.add(locationHandler);
    }
    
    public void removeLocationHandler(ILocationHandler locationHandler) {
    	locationHandlers.remove(locationHandler);
    }
    
    public void updateProviders(Criteria criteria) {
    	if(criteria == null)
    		criteria = generateCriteria();
    	
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		List<String> providerNames = locationManager.getProviders(criteria, true);
		for(String providerName : providerNames)
			locationManager.requestLocationUpdates(providerName, minTime, minDistance, this);
    }
	
	private Criteria generateCriteria() {
		Criteria criteria = new Criteria();
		
		// Do some setup of the criteria.
		
		return criteria;
	}

	public void onLocationChanged(Location location) {
		
		List<IRecord> updateRecords = new ArrayList<IRecord>();
		List<IRecord> cacheRecords = new ArrayList<IRecord>();
		for(ILocationHandler locationHandler : locationHandlers) {
			List<IRecord> records = locationHandler.getRecords(location);
			if(records != null && !records.isEmpty())
				if(cacheUpdates)
					cacheRecords.addAll(records);
				else
					updateRecords.addAll(records);
		}
		
		if(!updateRecords.isEmpty())
			try {
				SimpleGeoClient.getInstance().update(updateRecords);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if(!cacheRecords.isEmpty())
			cacheRecords(cacheRecords);
	}

	public void onProviderDisabled(String provider) {
		// Don't really care...
	}

	public void onProviderEnabled(String provider) {
		// Don't really care (or add as a listener)
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}
	
	public void cacheRecords(List<IRecord> records) {
		
	}
	
	/**
	 * @return the cacheUpdates
	 */
	public boolean isCacheUpdates() {
		return cacheUpdates;
	}

	/**
	 * @param cacheUpdates the cacheUpdates to set
	 */
	public void setCacheUpdates(boolean cacheUpdates) {
		this.cacheUpdates = cacheUpdates;
	}
}
