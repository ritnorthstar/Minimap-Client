package com.northstar.minimap.map;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


/**
 * 
 * 
 * @author John Paul
 */
public abstract class BoundaryLocationSource implements LocationSource{

	protected OnLocationChangedListener locListener;
	protected boolean mPaused;
	
	private List<LatLngBounds> boundaries = new ArrayList<LatLngBounds>();
	protected Map map;
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		locListener = listener;
	}

	@Override
	public void deactivate() {
		locListener = null;
	}
	
	public void addBoundaries(LatLngBounds boundary){
    	boundaries.add(boundary);
    }
	
	public LatLngBounds setLocation(Location loc){
		if (locListener != null) {
			for(int i = 0; i < boundaries.size(); i++){
				LatLngBounds bound = boundaries.get(i);
				
				LatLng position = new LatLng(loc.getLatitude(), loc.getLongitude());
				
				if(bound.contains(position)){
					return bound;
				}
			}
			locListener.onLocationChanged(loc);
		}
		return null;
	}
	
	public void setMap(Map map){
		boundaries.clear();
		
		this.map = map;
	}
	
	public void onPause() {
        mPaused = true;
    }

    public void onResume() {
        mPaused = false;
    }

}