package com.simplegeo.android.service.intent;

public class CacheLocationIntent extends LocationIntent {

    public CacheLocationIntent() {
    	super();
    	this.putExtra(super.CACHE_LOCATION, true);
    }

}
