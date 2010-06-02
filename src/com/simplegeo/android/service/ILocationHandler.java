package com.simplegeo.android.service;

import java.util.List;
import android.location.Location;
import com.simplegeo.client.model.IRecord;

public interface ILocationHandler {
	
	public List<IRecord> getRecords(Location location);

}
