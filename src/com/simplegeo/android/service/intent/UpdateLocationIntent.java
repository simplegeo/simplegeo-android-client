package com.simplegeo.android.service.intent;

public class UpdateLocationIntent extends LocationIntent {

    public UpdateLocationIntent() {
    	super();
    	this.putExtra(UPDATE_LOCATION, true);
    }

}
