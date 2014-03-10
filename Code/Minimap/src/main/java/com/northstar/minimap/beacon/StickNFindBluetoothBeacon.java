//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    private static double propagationConstant = 1.8;//2.007;
    private static double rssiAtOneMeter = -68.6;
    private double previousAverageRssi = 0;

    public static HashMap<String, Integer> beaconMap;

    public static final int SMOOTHING_RANGE = 5;

    private LinkedList<Double> previousRssis;

    public StickNFindBluetoothBeacon(BluetoothDevice device, String beaconID, Position location) {
        super(device, beaconID, location);

        previousRssis = new LinkedList<Double>();

        beaconMap = new HashMap<String, Integer>();
        beaconMap.put("FD:65:28:71:80:C0", 1);
        beaconMap.put("FE:CC:38:AA:BE:B5", 2);
        beaconMap.put("E4:3E:A0:63:BC:C6", 3);
        beaconMap.put("E3:BF:2E:56:BF:B9", 4);
    }

    @Override
    public int getSignalStrength() {
        return signalStrength;
    }

    public double getAverageRssi() {
        double smoothRssi = 0;

        if (previousRssis.size() == SMOOTHING_RANGE && Math.abs(signalStrength - previousAverageRssi) > 7) {
            return previousAverageRssi;
        }

        if (previousRssis.size() == SMOOTHING_RANGE) {
            previousRssis.removeFirst();
        }

        previousRssis.addLast((double) signalStrength);

        for (double previousRssi: previousRssis) {
            smoothRssi += previousRssi;
        }

        previousAverageRssi = (smoothRssi / previousRssis.size());
        return previousAverageRssi;
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance() {
        return rssiToDistance(getAverageRssi());
    }

    public String getFormattedDistance() {
        String distanceString = new DecimalFormat("#.##").format(computeDistance());
        String rssiString = new DecimalFormat("#.##").format(getAverageRssi());
        return (distanceString + " m (" + rssiString + ")");
    }

    public static double rssiToDistance(double rssi) {
        return Math.pow(10, (rssiAtOneMeter - (rssi)) / (10.0 * propagationConstant));
    }

    public static double getPropagationConstant() {
        return propagationConstant;
    }

    public static double getRssiAtOneMeter() {
        return rssiAtOneMeter;
    }

    public static void setPropagationConstant(double propagationConstant) {
        StickNFindBluetoothBeacon.propagationConstant = propagationConstant;
    }

    public static void setRssiAtOneMeter(double rssiAtOneMeter) {
        StickNFindBluetoothBeacon.rssiAtOneMeter = rssiAtOneMeter;
    }
}
