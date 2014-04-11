package com.northstar.minimap;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

/**
 * Performs various calculations such as multilateration and calibration.
 */
public class PositionCalculator {

    public static final double MAX_POSITION_DELTA = 1.0;
    public static final int GAUSS_NEWTON_ITERATIONS = 10;
    public static final int SMOOTHING_RANGE = 5;

    public static final double GRID_HEIGHT = 3.0;
    public static final double GRID_WIDTH = 3.0;

    private double prevAvgX;
    private double prevAvgY;

    private LinkedList<Double> prevX;
    private LinkedList<Double> prevY;

    public PositionCalculator() {
        prevX = new LinkedList<Double>();
        prevY = new LinkedList<Double>();
    }

    /**
     * Determines the RssiAtOneMeter and PropagationConstant values based on the distances
     * to beacons placed at 1m and 5m.
     */
    public void calibrate(Map<Double, Double> distanceMap) {
        StickNFindBluetoothBeacon.setRssiAtOneMeter(distanceMap.get(1.0));

        double min = 1.0;
        double max = 5.0;
        double expectedDistance = 5.0;

        // Perform a binary search to determine the best value for the propagation constant.
        while (max - min > 0.01) {
            StickNFindBluetoothBeacon.setPropagationConstant((min + max) / 2.0);

            double calculatedDistance =
                    StickNFindBluetoothBeacon.computeDistance(distanceMap.get(expectedDistance));

            if (calculatedDistance > expectedDistance) {
                min = (min + max) / 2.0;
            } else {
                max = (min + max) / 2.0;
            }
        }

        Globals.log("CALIBRATION", "RSSI at one meter: " +
                Double.toString(StickNFindBluetoothBeacon.getRssiAtOneMeter()));
        Globals.log("CALIBRATION", "Propagation Constant: " +
                Double.toString(StickNFindBluetoothBeacon.getPropagationConstant()));
    }

    /**
     * The function to minimize in the multilateration calculation (errors in distance).
     * @param x - Estimated x position of user
     * @param y - Estimated y position of user
     * @param xi - Known x position of beacon
     * @param yi - Known y position of beacon
     * @param r - Estimated distance between user and beacon
     * @return
     */
    private double minFn(double x, double y, double xi, double yi, double r) {
        return Math.sqrt(Math.pow(x - xi, 2) + Math.pow(y - yi, 2)) - r;
    }

    /**
     * Uses the Gauss-Newton Algorithm to perform a Non-Linear Least Squares estimation on the
     * user's x and y position based on the given beacons and their positions.
     * @param beacons
     * @return
     */
    public Position multilaterate(List<IBeacon> beacons) {
        // TODO: Abstract out to more than four beacons.

        IBeacon b1 = beacons.get(0);
        IBeacon b2 = beacons.get(1);
        IBeacon b3 = beacons.get(2);
        IBeacon b4 = beacons.get(3);

        Position p1 = b1.getPosition();
        Position p2 = b2.getPosition();
        Position p3 = b3.getPosition();
        Position p4 = b4.getPosition();

        double r1 = b1.computeDistance();
        double r2 = b2.computeDistance();
        double r3 = b3.computeDistance();
        double r4 = b4.computeDistance();

        // Known x and y positions of beacons.  Estimated distances between user and beacons.
        double xi[] = new double[] { p1.getX(), p2.getX(), p3.getX(), p4.getX() };
        double yi[] = new double[] { p1.getY(), p2.getY(), p3.getY(), p4.getY() };
        double ri[] = new double[] { r1, r2, r3, r4 };

        // Estimated x and y position of user.
        double x = (xi[0] + xi[1] + xi[2] + xi[3]) / 4;
        double y = (yi[0] + yi[1] + yi[2] + yi[3]) / 4;

        // The algorithm is repeated n=GAUSS_NEWTON_ITERATIONS times
        for (int k = 0; k < GAUSS_NEWTON_ITERATIONS; k++) {
            double j0 = 0.0;
            double j1 = 0.0;
            double j2 = 0.0;
            double j3 = 0.0;
            double j4 = 0.0;
            double j5 = 0.0;

            // Calculate elements of transposed Jacobian matrices needed.
            for (int i = 0; i < 4; i++) {
                double f = minFn(x, y, xi[i], yi[i], ri[i]);
                j0 += Math.pow(x - xi[i], 2) / Math.pow(f + ri[i], 2);
                j1 += ((x - xi[i]) * (y - yi[i])) / Math.pow(f + ri[i], 2);
                j2 += ((x - xi[i]) * (y - yi[i])) / Math.pow(f + ri[i], 2);
                j3 += Math.pow(y - yi[i], 2) / Math.pow(f + ri[i], 2);

                j4 += ((x - xi[i]) * f) / (f + ri[i]);
                j5 += ((y - yi[i]) * f) / (f + ri[i]);
            }

            // Form transposed Jacobian matrices.
            Matrix J1 = new Matrix(new double[][]{{j0, j1}, {j2, j3}});
            Matrix J2 = new Matrix(new double[][]{{j4}, {j5}});
            Matrix B = new Matrix(new double[][]{{x}, {y}});

            // Perform Gauss-Newton calculation and set refined x and y value estimations.
            Matrix Bk = B.minus(J1.inverse().times(J2));
            x = Bk.get(0, 0);
            y = Bk.get(1, 0);
        }

        return getAveragedPosition(x, y);
    }

    /**
     * @return An averaged position based on the last n=SMOOTHING_RANGE positions.
     */
    private Position getAveragedPosition(double x, double y) {
        if (prevX.size() == SMOOTHING_RANGE) {
            // Do not calculate a new average if the given value's delta is too high (jitter).
            double delta = Math.sqrt(Math.pow(x - prevAvgX, 2) + Math.pow(y - prevAvgY, 2));
            if (delta > MAX_POSITION_DELTA) {
                return new Position(prevAvgX, prevAvgY);
            }

            prevX.removeLast();
            prevY.removeLast();
        }

        double avgX = 0.0;
        double avgY = 0.0;

        prevX.addFirst(x);
        prevY.addFirst(y);

        for (int n = 0; n < prevX.size(); n++) {
            avgX += prevX.get(n);
            avgY += prevY.get(n);
        }

        avgX /= prevX.size();
        avgY /= prevX.size();

        prevAvgX = avgX;
        prevAvgY = avgY;

        return new Position(avgX, avgY);
    }
}
