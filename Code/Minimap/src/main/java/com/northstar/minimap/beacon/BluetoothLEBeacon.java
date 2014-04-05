//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.map.Position;

import android.bluetooth.BluetoothDevice;

/**
 * Class to create a wrapper around a bluetooth device.
 * Holds the ID and position of a bluetooth beacon.
 */
public class BluetoothLEBeacon implements IBeacon {

    private String ID;
    private Position pos;
    
    /**
     * Constructor for the Bluetooth beacon
     * @param beaconID A string ID of the beacon
     * @param location A position with the location coordinates
     */
    public BluetoothLEBeacon(String beaconID, Position location){
        ID = beaconID;
        pos = location;
    }

    /**
     * Method to obtain the ID of a beacon.
     * @return A string representing a beacon's unique ID.
     */
    public String getID(){
        return ID;
    }

    /**
     * Method to obtain the position of a beacon.
     * @return A Position object with the x,y coordinates of the beacon.
     */
    public Position getPos(){
        return pos;
    }

}
