package com.simplegeo.android.service;

import com.simplegeo.android.service.handlers.LocationHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus.NmeaListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;

public class LocationService extends Service {
	
	private Criteria locationCriteria;
			
	final Messenger messanger = new Messenger(new LocationHandler());
	
	@Override
	public IBinder onBind(Intent intent) {
		return messanger.getBinder();
	}
	
    @Override
    public void onCreate() {
		this.locationCriteria = generateCriteria();
		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
    }	
    
    @Override
    public void onDestroy() {
    	
    }
    
	private void startListening() {
		
		
	}
	
	private void stopListening() {
		
	}
	
	private Criteria generateCriteria() {
		Criteria criteria = new Criteria();
		
		return criteria;
	}
}
