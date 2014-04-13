//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;

/**
* Class to create a wrapper around a bluetooth device.
* Adds our own ID and the position on the map, and handles
* all functions relating to accessing the device.
*/
public abstract class BluetoothBeacon implements IBeacon {

    protected int number;
    protected int signalStrength;

    protected String id;
    protected Position position;
    private BluetoothDevice beacon;

    public BluetoothBeacon(BluetoothDevice device, int number, String id, Position position) {
        beacon = device;
        this.id = id;
        this.number = number;
        this.position = position;
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