//John Paul Mardelli
//Last updated November 2nd, 2013

package com.northstar.minimap.beacon;

import com.google.android.gms.maps.model.Circle;
import com.northstar.minimap.Globals;
import com.northstar.minimap.MapActivity;
import com.northstar.minimap.Position;
import com.northstar.minimap.util.MedianList;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StickNFindBluetoothBeacon extends BluetoothBeacon {

    public static final double DEFAULT_PROPAGATION_CONSTANT = 2.007;
    public static final double DEFAULT_RSSI_AT_ONE_METER = -68.6;
    public static final double PROXIMITY_ZONE_RANGE = 0.8;
    public static final int MEDIAN_RANGE_RSSI = 10;

    public static Map<Integer, String> beaconIdMap;

    private double medianRssi = 0;
    private double propagationConstant = DEFAULT_PROPAGATION_CONSTANT;
    private double rssiAtOneMeter = DEFAULT_RSSI_AT_ONE_METER;

    private MedianList rssis;

    public StickNFindBluetoothBeacon(int number, String id, Position position) {
        super(number, id, position);

        rssis = new MedianList(MEDIAN_RANGE_RSSI);
    }

    public static void initBeaconIdMap() {
        beaconIdMap = new HashMap<Integer, String>();
        beaconIdMap.put(1, "FD:65:28:71:80:C0");
        beaconIdMap.put(2, "FE:CC:38:AA:BE:B5");
        beaconIdMap.put(3, "E4:3E:A0:63:BC:C6");
        beaconIdMap.put(4, "E3:BF:2E:56:BF:B9");
        beaconIdMap.put(5, "CA:49:2F:AF:C6:33");
        beaconIdMap.put(6, "C1:87:95:AA:38:B3");
        beaconIdMap.put(7, "ED:1D:DA:AC:D9:59");
        beaconIdMap.put(8, "FB:E3:E8:AD:1A:7A");
        beaconIdMap = Collections.unmodifiableMap(beaconIdMap);
    }

    @Override
    public Double computeUnaveragedDistance() {
        return computeDistance(rssis.getLast());
    }

    /**
     * Method to use our own algorithm to compute distance to a
     * bluetooth beacon
     */
    @Override
    public Double computeDistance() {
        double meters = Math.pow(10, (rssiAtOneMeter - medianRssi) / (10.0 * propagationConstant));
        return (meters * MapActivity.M_TO_FT);
    }

    @Override
    public void resetCalibration() {
        propagationConstant = DEFAULT_PROPAGATION_CONSTANT;
        rssiAtOneMeter = DEFAULT_RSSI_AT_ONE_METER;
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

    public Double computeDistance(double rssi) {
        double meters = Math.pow(10, (rssiAtOneMeter - (rssi)) / (10.0 * propagationConstant));
        return (meters * MapActivity.M_TO_FT);
    }

    public void computeMedianRssi() {
        rssis.add(signalStrength);
        medianRssi = rssis.getMedian();

        // XXX: Unneccessary, but we'd still like to the median values logged for now...
        List<Double> sortedRssis = new ArrayList<Double>();
        String s = getNumber() + ": ";
        for (double rssi : rssis) {
            sortedRssis.add(rssi);
        }
        Collections.sort(sortedRssis);
        for (double rssi : sortedRssis) {
            s += rssi + " ";
        }
        s += " | " + medianRssi;
        s += " | " + getFormattedDistance();
        Log.d("BT-MEDIAN-" + getNumber(), s);
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
        String rssiString = new DecimalFormat("#.##").format(medianRssi);
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
}
