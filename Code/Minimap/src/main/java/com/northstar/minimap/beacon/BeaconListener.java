package com.northstar.minimap.beacon;

/**
 * Created by Chris on 4/11/14.
 */
public interface BeaconListener {
    public void onBeaconDistanceChanged(IBeacon beacon, double distance);
}
