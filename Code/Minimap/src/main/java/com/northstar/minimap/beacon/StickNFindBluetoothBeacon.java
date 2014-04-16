//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.google.android.gms.maps.model.Circle;
import com.northstar.minimap.Globals;
import com.northstar.minimap.Position;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    private double propagationConstant = 2.007;
    private double rssiAtOneMeter = -68.6;

    public static final double MEAN_SHIFT_RSSI_RANGE = 2.0;
    public static final int MEAN_SHIFT_ITERATIONS = 5;

    public static final int SMOOTHING_RANGE = 5;

    public static Map<Integer, String> beaconIdMap;

    private double meanShiftedRssi = 0;

    private Circle circle;
    private LinkedList<Double> previousRssis;

    public StickNFindBluetoothBeacon(BluetoothDevice device, int number, String id, Position position) {
        super(device, number, id, position);

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

    public void calibrate(double expectedDistance) {
        double min = 0.01;
        double max = 10.0;

        // Perform a binary search to determine the best value for the propagation constant.
        while (max - min > 0.0001) {
            this.setPropagationConstant((min + max) / 2.0);

            if (computeDistance() > expectedDistance) {
                if (expectedDistance > 1.0) {
                    min = (min + max) / 2.0;
                } else {
                    max = (min + max) / 2.0;
                }
            } else {
                if (expectedDistance > 1.0) {
                    max = (min + max) / 2.0;
                } else {
                    min = (min + max) / 2.0;
                }
            }
        }

        Log.d("BT-CALI", number + " " + expectedDistance + " " + computeDistance() + " " + propagationConstant);
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance() {
        return Math.pow(10, (rssiAtOneMeter - meanShiftedRssi) / (10.0 * propagationConstant));
    }

    public Double computeDistance(double rssi) {
        return Math.pow(10, (rssiAtOneMeter - (rssi)) / (10.0 * propagationConstant));
    }

    public void meanShift() {
        if (previousRssis.size() == SMOOTHING_RANGE) {
            previousRssis.removeFirst();
        }

        previousRssis.addLast((double) signalStrength);

        if (meanShiftedRssi == 0.0) {
            meanShiftedRssi = previousRssis.getLast();
        }

        for (int i = 0; i < MEAN_SHIFT_ITERATIONS; i++) {
            double sigma = 0.0;
            int count = 0;

            for (double previousRssi : previousRssis) {
                if (Math.abs(previousRssi - meanShiftedRssi) <= MEAN_SHIFT_RSSI_RANGE) {
                    sigma += previousRssi;
                    count++;
                }
            }

            if (count > 0) {
                meanShiftedRssi = sigma / count;
            }
        }

        Log.d("BT-MS", number + " " + meanShiftedRssi);
    }

    public int getNumber() {
        return number;
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
        String rssiString = new DecimalFormat("#.##").format(meanShiftedRssi);
        return (distanceString + " m (" + rssiString + ")");
    }

    public double getPropagationConstant() {
        return propagationConstant;
    }

    public double getRssiAtOneMeter() {
        return rssiAtOneMeter;
    }

    @Override
    public int getSignalStrength() {
        return signalStrength;
    }

    public void setPropagationConstant(double propagationConstant) {
        this.propagationConstant = propagationConstant;
    }

    public void setRssiAtOneMeter(double rssiAtOneMeter) {
        this.rssiAtOneMeter = rssiAtOneMeter;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        return circle;
    }
}
