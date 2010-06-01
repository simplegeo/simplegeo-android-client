package com.simplegeo.android.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

public class LocationService extends Service implements LocationListener {
	
	public static final String MIN_TIME = "minimum_time";
	public static final String MIN_DISTANCE = "minimun_distance";
	
	private long minTime;
	private float minDistance;

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
		// Update each handler
	}

	public void onProviderDisabled(String provider) {
		// Don't really care...
	}

	public void onProviderEnabled(String provider) {
		// Don't really care (or add as a listener)
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Don't really care
	}
}
