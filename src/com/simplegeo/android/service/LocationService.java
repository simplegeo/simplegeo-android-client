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
