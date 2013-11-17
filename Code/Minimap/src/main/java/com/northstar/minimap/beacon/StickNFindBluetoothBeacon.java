//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

public class StickNFindBluetoothBeacon extends BluetoothBeacon{

    public StickNFindBluetoothBeacon(BluetoothDevice device, String beaconID, Position location){
        super(device, beaconID, location);
    }

    /**
     * Method to use StickNFind's API to
     * for bluetooth to get signal Strength.
     */
    @Override
    public int getSignalStrength(){
        return 0;
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance(){
        return 0.0;
    }

}
