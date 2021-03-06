package com.northstar.minimap.beacon;

public interface BeaconListener {
    public void onBeaconDistanceChanged(IBeacon beacon, double distance);
    public void onBeaconInProximityZoneChanged(IBeacon beacon, boolean isInProximityZone);
}
