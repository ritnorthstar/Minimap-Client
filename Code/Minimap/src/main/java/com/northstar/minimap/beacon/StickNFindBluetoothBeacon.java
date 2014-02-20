//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    public static final double PROPAGATION_CONSTANT = 1.1;
    public static final int RSSI_AT_ONE_METER = -75;

    public StickNFindBluetoothBeacon(BluetoothDevice device, String beaconID, Position location) {
        super(device, beaconID, location);
    }

    /**
     * Method to use StickNFind's API to
     * for bluetooth to get signal Strength.
     */
    @Override
    public int getSignalStrength() {
        return 0;
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance() {
        return Math.pow(10, (RSSI_AT_ONE_METER - signalStrength) / (10.0 * PROPAGATION_CONSTANT));
    }


    public String getFormattedDistance() {
        return (new DecimalFormat("#.##").format(computeDistance()) + " m");
    }
}
