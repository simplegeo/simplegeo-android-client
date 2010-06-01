package com.simplegeo.android.service.intent;

import com.simplegeo.android.service.LocationService;

import android.content.Context;
import android.content.Intent;

public class LocationIntent extends Intent {
	
    protected static final String CACHE_LOCATION = "cache_location";
    protected static final String UPDATE_LOCATION = "update_location";
    
    public LocationIntent() {
    	super();
    	this.putExtra(CACHE_LOCATION, true);
    	this.putExtra(UPDATE_LOCATION, true);
    }
    
    public LocationIntent(Context packageContext) {
    	super(packageContext, LocationService.class);
    }
}
