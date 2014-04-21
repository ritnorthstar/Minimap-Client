package com.northstar.minimap.beacon;

public interface BeaconListener {
    public boolean getBeaconCirclesVisible();
    public void setBeaconCirclesVisible(boolean visible);
    public void onBeaconDistanceChanged(IBeacon beacon, double distance);
    public void onBeaconInProximityZoneChanged(IBeacon beacon, boolean isInProximityZone);
}
