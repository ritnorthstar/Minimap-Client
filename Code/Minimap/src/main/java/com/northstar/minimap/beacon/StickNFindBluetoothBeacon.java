//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    public static final double PROPAGATION_CONSTANT = 1.1;
    public static final int RSSI_AT_ONE_METER = -75;
    public static final int SMOOTHING_RANGE = 5;

    private LinkedList<Double> previousDistances;

    public StickNFindBluetoothBeacon(BluetoothDevice device, String beaconID, Position location) {
        super(device, beaconID, location);

        previousDistances = new LinkedList<Double>();
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
        double distance = rssiToDistance(signalStrength);
        double smoothedDistance = 0.0;

        if (previousDistances.size() == SMOOTHING_RANGE) {
            previousDistances.removeFirst();
        }

        previousDistances.addLast(distance);

        for (double previousDistance: previousDistances) {
            smoothedDistance += previousDistance;
        }

        return (smoothedDistance / previousDistances.size());
    }

    public String getFormattedDistance() {
        return (new DecimalFormat("#.##").format(computeDistance()) + " m");
    }

    private double rssiToDistance(int rssi) {
        return Math.pow(10, (RSSI_AT_ONE_METER - rssi) / (10.0 * PROPAGATION_CONSTANT));
    }
}
