package com.simplegeo.android.service;

import java.util.List;

import android.location.Location;

import com.simplegeo.client.model.IRecord;
import com.simplegeo.client.model.Region;

public class LocationHandler implements ILocationHandler {

	public List<IRecord> getRecords(Location location, List<IRecord> trackedRecords) {
		return trackedRecords;
	}

	public void onLocationChanged(Location fromLocation, Location toLocation) {
		
	}

	public void onRegionsEntered(List<Region> regions, Location fromLocation, Location toLocation) {
		
	}

	public void onRegionsExited(List<Region> regions, Location fromLocation, Location toLocation) {
		
	}
}
