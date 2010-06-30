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
 * A default implementation of {@link com.simplegeo.android.service.ILocationHandler}.
 */
public class LocationHandler implements ILocationHandler {

	/**
	 * @see com.simplegeo.android.service.ILocationHandler#getRecords(android.location.Location, java.util.List)
	 */
	public List<IRecord> getRecords(Location location, List<IRecord> trackedRecords) {		
		return trackedRecords;
	}

	/**
	 * @see com.simplegeo.android.service.ILocationHandler#onLocationChanged(android.location.Location, android.location.Location)
	 */
	public void onLocationChanged(Location fromLocation, Location toLocation) {
		;
	}

	/**
	 * @see com.simplegeo.android.service.ILocationHandler#onRegionsEntered(java.util.List, android.location.Location, android.location.Location)
	 */
	public void onRegionsEntered(List<Region> regions, Location fromLocation, Location toLocation) {
		;
	}

	/**
	 * @see com.simplegeo.android.service.ILocationHandler#onRegionsExited(java.util.List, android.location.Location, android.location.Location)
	 */
	public void onRegionsExited(List<Region> regions, Location fromLocation, Location toLocation) {
		;
	}
}
