//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.google.android.gms.maps.model.Circle;
import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;

/**
* Class to create a wrapper around a bluetooth device.
* Adds our own ID and the position on the map, and handles
* all functions relating to accessing the device.
*/
public abstract class BluetoothBeacon implements IBeacon {

    protected boolean isInProximityZone;
    protected int number;
    protected int signalStrength;

    protected Circle markerCircle;
    protected Circle rangeCircle;
    protected String id;
    protected Position position;

    public BluetoothBeacon(int number, String id, Position position) {
        this.id = id;
        this.number = number;
        this.position = position;
    }

    @Override
    public Circle getMarkerCircle() {
        return markerCircle;
    }

    @Override
    public Circle getRangeCircle() {
        return rangeCircle;
    }

    @Override
    public boolean isInProximityZone() {
        return isInProximityZone;
    }

    @Override
    public void setInProximityZone(boolean isInProximityZone) {
        this.isInProximityZone = isInProximityZone;
    }

    @Override
    public void setMarkerCircle(Circle markerCircle) {
        this.markerCircle = markerCircle;
    }

    @Override
    public void setRangeCircle(Circle rangeCircle) {
        this.rangeCircle = rangeCircle;
    }

    /**
     * Method to obtain the ID of a beacon.
     * @return A string representing a beacon's unique ID.
     */
    public String getId(){
        return id;
    }

    public Position getPosition(){
        return position;
    }

    /**
     * Method to use Androids method of attaining signal strength
     * for bluetooth to get signal Strength.
     * @return An int representing a beacons signal strength in dBm
     */
    public int getSignalStrength(){
        return 0;
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     * @return The distance to the beacon
     */
    public Double computeDistance() {
        return 0.0;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
}