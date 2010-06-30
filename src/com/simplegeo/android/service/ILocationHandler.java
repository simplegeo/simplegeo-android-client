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

import java.util.List;

import android.location.Location;

import com.simplegeo.client.model.IRecord;
import com.simplegeo.client.model.Region;

/**
 * Use this interface in order to receive location notifications from the 
 * {@link com.simplegeo.android.service.LocationService}.
 * 
 */
public interface ILocationHandler {
	
	/**
	 * Returns a list of {@link com.simplegeo.client.model.IRecord}s that will be updated
	 * when a location update notification is received by the {@link com.simplegeo.android.service.LocationService}.
	 * The records that are passed in are the pre-registered 
	 * {@link com.simplegeo.android.service.LocationService#trackRecords}. This gives the implementor the opportunity
	 * to manipulate the tracked records before they are updated.
	 * 
	 * @param location the new location
	 * @param trackedRecords the pre-registered track records
	 * @return A list of records to update. This value can be null if no records need to be updated.
	 */
	public List<IRecord> getRecords(Location location, List<IRecord> trackedRecords);
	
	/**
	 * Notifies the implementor that the location has changed. The old location can be null if this is the 
	 * first location update.
	 * 
	 * @param fromLocation the old location
	 * @param toLocation the new location
	 */
	public void onLocationChanged(Location fromLocation, Location toLocation); 
	
	/**
	 * Notifies the implementor that the most recent location update now contains a new set of polygons
	 * that were retrieved from SimpleGeo's PushPin service.
	 * 
	 * @param regions the new set of {@link com.simplegeo.client.model.Region} that contain the new location
	 * @param fromLocation the old location
	 * @param toLocation the new location
	 */
	public void onRegionsEntered(List<Region> regions, Location fromLocation, Location toLocation);
	
	/**
	 * Notifies the implementor that the most recent location update is no longer contained within a
	 * set of polygons that were originally retrieved from SimpleGeo's PushPin service. 
	 * 
	 * @param regions the old set of {@link com.simplegeo.client.model.Region} that no longer contain
	 * the new location
	 * @param fromLocation the old location
	 * @param toLocation the new location
	 */
	public void onRegionsExited(List<Region> regions, Location fromLocation, Location toLocation);

}
