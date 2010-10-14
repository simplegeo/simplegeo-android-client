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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.simplegeo.android.cache.CommitLog;
import com.simplegeo.client.SimpleGeoClient;
import com.simplegeo.client.encoder.GeoJSONEncoder;
import com.simplegeo.client.geojson.GeoJSONObject;
import com.simplegeo.client.model.DefaultRecord;
import com.simplegeo.client.model.GeoJSONRecord;
import com.simplegeo.client.model.IRecord;
import com.simplegeo.client.model.Region;

/**
 *	A local location Service that registers itself with location providers
 *  in order to receive location updates. This allows the service to do automatic
 *  updates for pre-registered records whenever the device's location is updated.
 *  
 *  Another strong feature of the LocationService is its ability to notify 
 *  {@link com.simplegeo.android.service.ILocationHandler}s when a new location
 *  is contained with a different region set. A region set is defined by SimpleGeo's
 *  PushPin service @see {@link com.simplegeo.client.SimpleGeoClient#contains(double, double)}.  
 */
public class LocationService extends Service implements LocationListener {
	
	private static final String TAG = LocationService.class.getCanonicalName();
	
	/**
	 * The minimum time interval for notifications, in milliseconds. This field is only used 
	 * as a hint to conserve power, and actual time between location updates
	 *  may be greater or lesser than this value.
	 */
	public static final long DEFAULT_TIME = 120000;
	private long minTime;
	
	/**
	 * The minimum distance interval for notifications, in meters
	 */
	public static final float DEFAULT_DISTANCE = 10.0f;
	private float minDistance;
	
	/**
	 * When a location notification is received, records can either be updated on the spot
	 * or cached for a later time. Set this value to true if you wish to enable cacheing.
	 */
	public boolean cacheUpdates = false;
	
	/**
	 * When a location notification is received,
	 * {@link com.simplegeo.client.SimpleGeoClient#contains(double, double)} is called
	 * in order to retrieve the polygons that contain the new point. Set this value to false
	 * if you do not require region updates based on new location updates.
	 */
	public boolean enableRegionUpdates = true;
	
	/**
	 * Add instances of {@link com.simplegeo.client.model.IRecord} to this list
	 * if you want a record to be updated automatically when a location notification
	 * is received.
	 * 
	 * This list is also passed into {@link com.simplegeo.android.service.ILocationHandler#getRecords(Location, List)}
	 * to give the handler the opportunity to fine tune records before they are sent to SimpleGeo.
	 */
	public List<IRecord> trackedRecords = new ArrayList<IRecord>();
	
	private Location previousLocation = null;
	private List<Region> regions = null;
	private List<ILocationHandler> locationHandlers = new ArrayList<ILocationHandler>();
	private CommitLog commitLog = null;
	
    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }
    
    private final LocationBinder locationBinder = new LocationBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return locationBinder;
	}
	
    @Override
    public void onCreate() {
    	Application application = getApplication();
    
    	// There seems to be a bug when retreiving the cache
    	// directory from the ContextWrapper where a NPE is
    	// thrown. This only happens in testing.
    	String cachePath, username;
    	try {
    		File cacheDir = application.getApplicationContext().getCacheDir();
    		cachePath = cacheDir.getAbsolutePath();
   		} catch (Exception e) {
   			Log.e(TAG, e.toString(), e);   			
   			cachePath = "/sdcard/";
   		}

   		try {
   			username = application.getApplicationContext().getPackageName();
   		} catch (Exception e) {
   			Log.e(TAG, e.toString(), e);
   			username = "com.simplegeo.android.cache";
   		}
    	
   		updateCommitLog(cachePath, username);
		updateProviders();
    }	
        
    @Override
    public void onDestroy() {
    	commitLog.flush();
    }

    /**
     * Update the providers with some provided default values.
     */
    public void updateProviders() {
    	updateProviders(null, DEFAULT_TIME, DEFAULT_DISTANCE);
    }
    
    /**
     * @param locationHandler
     */
    public void addLocationHandler(ILocationHandler locationHandler) {
    	locationHandlers.add(locationHandler);
    }
    
    /**
     * @param locationHandler
     */
    public void removeLocationHandler(ILocationHandler locationHandler) {
    	locationHandlers.remove(locationHandler);
    }
    
    /**
     * Obtain a list of providers and register with them to receive callback
     * notifications.
     * 
     * @param criteria
     * @param minTime
     * @param minDistance
     */
    public void updateProviders(Criteria criteria, long minTime, float minDistance) {
    	if(criteria == null)
    		criteria = generateCriteria();
    	
    	this.minTime = minTime;
    	this.minDistance = minDistance;
    	
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		List<String> providerNames = locationManager.getProviders(criteria, true);
		for(String providerName : providerNames)
			locationManager.requestLocationUpdates(providerName, minTime, minDistance, this);
    }
    
    /**
     * Registers this instance as a listener for the LocationManager. This is useful
     * if you wish to resume location notifications when an activity has been
     * paused. 
     */
    public void startUpdating() {
    	updateProviders();
    }
    
    /**
     * Removes this instance as a listener for the LocatoinManager. This is useful
     * if you wish to cancel location notifications when an Activity is paused.
     */
    public void stopUpdating() {
    	LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    	locationManager.removeUpdates(this);
    }
    	
    /*
     * Generates a default Criteria object.
     */
	private Criteria generateCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		
		return criteria;
	}

	public void onLocationChanged(Location location) {
		Log.d(TAG, "location updated to " + location.toString());
		
		for(ILocationHandler handler : locationHandlers)
			handler.onLocationChanged(previousLocation, location);
		
		updateRecordsFromLocation(location);
		updateRegionsFromLocation(location);
		
		previousLocation = location;
	}

	public void onProviderDisabled(String provider) {
		Log.d(TAG, String.format("provider %s was disbaled", provider));
	}

	public void onProviderEnabled(String provider) {
		Log.d(TAG, String.format("provide %s was enabled", provider));
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}
	
	/*
	 * Determines what regions have been entered/exited based on the new
	 * location and old location objects.
	 */
	private void updateRegionsFromLocation(Location location) {
		JSONArray boundaries = fetchRegions(location);
		if(boundaries != null && enableRegionUpdates) {
			List<Region> regions = Region.getRegions(boundaries);
			List<Region> enteredRegions = new ArrayList<Region>();
			List<Region> exitedRegions = new ArrayList<Region>();
			if(this.regions == null) {
				enteredRegions = regions;
			} else {
				exitedRegions.addAll(Region.difference(this.regions, regions));
				
				for(Region region : regions)
					if(!region.contained(this.regions))
						enteredRegions.add(region);
			}
			
			if(enteredRegions != null && enteredRegions.size() > 0)
				for(ILocationHandler handler : locationHandlers)
					handler.onRegionsEntered(enteredRegions, previousLocation, location);
			
			if(exitedRegions != null && exitedRegions.size() > 0)
				for(ILocationHandler handler : locationHandlers)
					handler.onRegionsExited(exitedRegions, previousLocation, location);
			
			this.regions = regions;
		}
	}
	
	/*
	 * Makes the request to the PushPin service in order to retrieve the 
	 * polygons for a given location.
	 */
	private JSONArray fetchRegions(Location location) {
		JSONArray boundaries = null;
		try {
			SimpleGeoClient client = SimpleGeoClient.getInstance();
			Object returnObject = client.contains(location.getLatitude(), location.getLongitude());
			
			// There are two possible return values from the client
			// depending on the value of futureTask
			if(client.getFutureTask())
				boundaries = (JSONArray)((FutureTask)returnObject).get();
			else
				boundaries = (JSONArray)returnObject;

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);			
		} catch (InterruptedException e) {
			Log.e(TAG, e.toString(), e);	
		} catch (ExecutionException e) {
			Log.e(TAG, e.toString(), e);			
		}
		
		return boundaries;
	}
	
	/*
	 * Handles the work that updates/caches the list of trackedRecords whenever
	 * a location notification is recieved.
	 */
	private void updateRecordsFromLocation(Location location) {
		List<IRecord> updateRecords = new ArrayList<IRecord>();
		List<IRecord> cacheRecords = new ArrayList<IRecord>();
		
		for(IRecord record : trackedRecords) {
			if((record instanceof DefaultRecord) || (record instanceof GeoJSONRecord)) {
				((DefaultRecord)record).setLatitude(location.getLatitude());
				((DefaultRecord)record).setLongitude(location.getLongitude());
			}
		}
		
		if(locationHandlers.isEmpty()) {
			if(cacheUpdates)
				cacheRecords.addAll(trackedRecords);
			else
				updateRecords.addAll(trackedRecords);
		}
		
		for(ILocationHandler locationHandler : locationHandlers) {
			List<IRecord> records = locationHandler.getRecords(location, trackedRecords);
			if(records != null && !records.isEmpty())
				if(cacheUpdates)
					cacheRecords.addAll(records);
				else
					updateRecords.addAll(records);
		}
		
		if(!updateRecords.isEmpty())
			updateRecords(updateRecords);
		
		if(!cacheRecords.isEmpty())
			cacheRecords(cacheRecords);
	}
	
	private void updateRecords(List<IRecord> records) {
		if(!records.isEmpty())
			try {
				Log.d(TAG, String.format("updating %d records", records.size()));
				SimpleGeoClient.getInstance().update(records);
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, e.toString(), e);
			}
	}
	
	private void cacheRecords(List<IRecord> records) {
		if(records != null && records.size() > 0) {
			Log.d(TAG, String.format("cacheing %d records", records.size()));
			GeoJSONObject geoJSON = GeoJSONEncoder.getGeoJSONRecord(records);
			commitLog.commit("update_records", geoJSON.toString());
		}
	}
	
	/**
	 * During the lifetime of the LocationService, records can potentially be committed
	 * to the {@link com.simplegeo.android.cache.CommitLog} if 
	 * {@link com.simplegeo.android.service.LocationService#cacheUpdates} is enabled. 
	 * 
	 * Replaying the commit log will call {@link com.simplegeo.client.SimpleGeoClient#update(GeoJSONObject)}
	 * on all the records that were saved to cache.
	 */
	public void replayCommitLog() {
		List<String> commits = commitLog.getCommits("update_records");
		List<IRecord> recordsToUpdate = new ArrayList<IRecord>();
		for(String commit : commits) {
			GeoJSONObject geoJSON;
			try {
				geoJSON = new GeoJSONObject("FeatureCollection", commit);
				List<DefaultRecord> records = GeoJSONEncoder.getRecords(geoJSON);
				if(records != null)
					recordsToUpdate.addAll(records);

			} catch (JSONException e) {
				Log.e(TAG, e.toString(), e);
			}
		}
		
		if(!recordsToUpdate.isEmpty())
			updateRecords(recordsToUpdate);
	}
	
	/**
	 * @param handler
	 */
	public void addHandler(ILocationHandler handler) {
		locationHandlers.add(handler);
	}
	
	/**
	 * @param handler
	 */
	public void removeHandler(ILocationHandler handler) {
		locationHandlers.remove(handler);
	}
	
	/**
	 * @return the cacheUpdates
	 */
	public boolean cacheUpdates() {
		return cacheUpdates;
	}

	/**
	 * @param cacheUpdates the cacheUpdates to set
	 */
	public void setCacheUpdates(boolean cacheUpdates) {
		this.cacheUpdates = cacheUpdates;
	}

	/**
	 * @return the minTime
	 */
	public long getMinTime() {
		return minTime;
	}

	/**
	 * @return the minDistance
	 */
	public float getMinDistance() {
		return minDistance;
	}
	
	/**
	 * @return the current {@link com.simplegeo.client.model.Region}s that contain
	 * the  
	 */
	public List<Region> getRegions() {
		return this.regions;
	}
	
	/**
	 * Creates a new {@link com.simplegeo.android.cache.CommitLog} with the given
	 * arguments.
	 * 
	 * @param cachePath
	 * @param username
	 */
	public void updateCommitLog(String cachePath, String username) {
		this.commitLog = new CommitLog(cachePath, username);
		replayCommitLog();
	}	
}
