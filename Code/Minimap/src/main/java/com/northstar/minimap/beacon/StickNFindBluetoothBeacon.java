//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    private static double propagationConstant = 2.007;
    private static double rssiAtOneMeter = -68.6;

    public static final int SMOOTHING_RANGE = 5;
    public static final int MAX_SMOOTHING_DEVIATION = 7;

    public static Map<Integer, String> beaconIdMap;

    private double previousAverageRssi = 0;

    private LinkedList<Double> previousRssis;

    public StickNFindBluetoothBeacon(BluetoothDevice device, String beaconID, Position position) {
        super(device, beaconID, position);

        previousRssis = new LinkedList<Double>();
    }

    public static void initBeaconIdMap() {
        beaconIdMap = new HashMap<Integer, String>();
        beaconIdMap.put(1, "FD:65:28:71:80:C0");
        beaconIdMap.put(2, "FE:CC:38:AA:BE:B5");
        beaconIdMap.put(3, "E4:3E:A0:63:BC:C6");
        beaconIdMap.put(4, "E3:BF:2E:56:BF:B9");
        beaconIdMap = Collections.unmodifiableMap(beaconIdMap);
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance() {
        return Math.pow(10, (rssiAtOneMeter - (getAverageRssi())) / (10.0 * propagationConstant));
    }

    public static Double computeDistance(double rssi) {
        return Math.pow(10, (rssiAtOneMeter - (rssi)) / (10.0 * propagationConstant));
    }

    public double getAverageRssi() {
        double smoothRssi = 0;

        if (previousRssis.size() == SMOOTHING_RANGE &&
                Math.abs(signalStrength - previousAverageRssi) > MAX_SMOOTHING_DEVIATION) {
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

    public static int getBeaconNumber(String id) {
        for (int number: beaconIdMap.keySet()) {
            if (beaconIdMap.get(number).equals(id)) {
                return number;
            }
        }

        return -1;
    }

    public String getFormattedDistance() {
        String distanceString = new DecimalFormat("#.##").format(computeDistance());
        String rssiString = new DecimalFormat("#.##").format(getAverageRssi());
        return (distanceString + " m (" + rssiString + ")");
    }

    public static double getPropagationConstant() {
        return propagationConstant;
    }

    public static double getRssiAtOneMeter() {
        return rssiAtOneMeter;
    }

    @Override
    public int getSignalStrength() {
        return signalStrength;
    }

    public static void setPropagationConstant(double propagationConstant) {
        StickNFindBluetoothBeacon.propagationConstant = propagationConstant;
    }

    public static void setRssiAtOneMeter(double rssiAtOneMeter) {
        StickNFindBluetoothBeacon.rssiAtOneMeter = rssiAtOneMeter;
    }
}
