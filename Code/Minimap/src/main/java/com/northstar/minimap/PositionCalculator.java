package com.northstar.minimap;

import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;

import java.util.Map;

/**
 * Created by Chris on 2/27/14.
 */
public class PositionCalculator {
    public static void calibrate(Map<Double, Double> distanceMap) {
        StickNFindBluetoothBeacon.setRssiAtOneMeter(distanceMap.get(1.0));

        double min = 1.0;
        double max = 5.0;
        double expectedDistance = 5.0;

        while (max - min > 0.01) {
            StickNFindBluetoothBeacon.setPropagationConstant((min + max) / 2.0);

            double calculatedDistance =
                    StickNFindBluetoothBeacon.rssiToDistance(distanceMap.get(expectedDistance));

            if (calculatedDistance > expectedDistance) {
                min = (min + max) / 2.0;
            } else {
                max = (min + max) / 2.0;
            }
        }

        Globals.log("CALI RSSI", Double.toString(StickNFindBluetoothBeacon.getRssiAtOneMeter()));
        Globals.log("CALI PROP", Double.toString(StickNFindBluetoothBeacon.getPropagationConstant()));

        // Write code to adjust propagation constant until correct 5.0m is found

//        double[] x = new double[distanceMap.size()];
//        double[] y = new double[distanceMap.size()];
//
//        int i = 0;
//        for (double distance: distanceMap.keySet()) {
//            x[i] = distanceMap.get(distance) * distanceMap.get(distance); // rssi
//            y[i] = distance;
//
//            Globals.log("CALI", Double.toString(x[i]) + " " + Double.toString(y[i]));
//            i++;
//        }
//
//        RegressionMethods regression = new RegressionMethods();
//
//        try {
//            double[] constants = regression.logarithmic(x, y);
//            double a = constants[0];
//            double b = constants[1];
//
//            double rssiAtOneMeter = a;
//            double propagationConstant = b;//(-b * 2 * Math.log(10)) / 10;
//
//            Globals.log("CALI", "Propagation Constant: " + propagationConstant);
//            Globals.log("CALI", "RSSI at One Meter: " + rssiAtOneMeter);
//
//            Globals.log("CALI", Double.toString(propagationConstant) + " " + Double.toString(rssiAtOneMeter));
//
//            StickNFindBluetoothBeacon.setPropagationConstant(propagationConstant);
//            StickNFindBluetoothBeacon.setRssiAtOneMeter(rssiAtOneMeter);
//        } catch (regression.NotEnoughValues e) {};
    }
}
