package com.northstar.minimap;

import android.util.Log;

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

    public static final double GRID_HEIGHT = 2.0;
    public static final double GRID_WIDTH = 2.0;

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
        // Known x and y positions of beacons.  Estimated distances between user and beacons.
        double bx[] = new double[beacons.size()];
        double by[] = new double[beacons.size()];
        double br[] = new double[beacons.size()];

        // Estimated x and y position of user.
        double x = 0;
        double y = 0;

        for (int i = 0; i < beacons.size(); i++) {
            bx[i] = beacons.get(i).getPosition().getX();
            by[i] = beacons.get(i).getPosition().getY();
            br[i] = beacons.get(i).computeDistance();
        }

        int maxI = 0;
        double maxR = 0;

        for (int i = 0; i < beacons.size(); i++) {
            if (br[i] > maxR) {
                maxI = i;
                maxR = br[i];
            }
        }

        for (int i = 0; i < beacons.size(); i++) {
            if (i != maxI) {
                x += bx[i];
                y += by[i];
            } else {
                Log.d("BT-MAX", ((StickNFindBluetoothBeacon) beacons.get(i)).getNumber() + "");
            }
        }

        x /= (beacons.size() - 1);
        y /= (beacons.size() - 1);

        // The algorithm is repeated n=GAUSS_NEWTON_ITERATIONS times
        for (int k = 0; k < GAUSS_NEWTON_ITERATIONS; k++) {
            double j0 = 0.0;
            double j1 = 0.0;
            double j2 = 0.0;
            double j3 = 0.0;
            double j4 = 0.0;
            double j5 = 0.0;

            // Calculate elements of transposed Jacobian matrices needed.
            for (int i = 0; i < beacons.size(); i++) {
                if (i != maxI) {
                    double f = minFn(x, y, bx[i], by[i], br[i]);
                    j0 += Math.pow(x - bx[i], 2) / Math.pow(f + br[i], 2);
                    j1 += ((x - bx[i]) * (y - by[i])) / Math.pow(f + br[i], 2);
                    j2 += ((x - bx[i]) * (y - by[i])) / Math.pow(f + br[i], 2);
                    j3 += Math.pow(y - by[i], 2) / Math.pow(f + br[i], 2);

                    j4 += ((x - bx[i]) * f) / (f + br[i]);
                    j5 += ((y - by[i]) * f) / (f + br[i]);
                }
            }

            // Form transposed Jacobian matrices.
            Matrix jacobian1 = new Matrix(new double[][]{{j0, j1}, {j2, j3}});
            Matrix jacobian2 = new Matrix(new double[][]{{j4}, {j5}});
            Matrix B = new Matrix(new double[][]{{x}, {y}});

            // Perform Gauss-Newton calculation and set refined x and y value estimations.
            Matrix Bk = B.minus(jacobian1.inverse().times(jacobian2));
            x = Bk.get(0, 0);
            y = Bk.get(1, 0);
        }

        return getAveragedPosition(x, y);
    }

    /**
     * @return An averaged position based on the last n=SMOOTHING_RANGE positions.
     */
    private Position getAveragedPosition(double x, double y) {
        return new Position(x, y);
//        if (prevX.size() == SMOOTHING_RANGE) {
//            // Do not calculate a new average if the given value's delta is too high (jitter).
//            double delta = Math.sqrt(Math.pow(x - prevAvgX, 2) + Math.pow(y - prevAvgY, 2));
//            if (delta > MAX_POSITION_DELTA) {
//                return new Position(prevAvgX, prevAvgY);
//            }
//
//            prevX.removeLast();
//            prevY.removeLast();
//        }
//
//        double avgX = 0.0;
//        double avgY = 0.0;
//
//        prevX.addFirst(x);
//        prevY.addFirst(y);
//
//        for (int n = 0; n < prevX.size(); n++) {
//            avgX += prevX.get(n);
//            avgY += prevY.get(n);
//        }
//
//        avgX /= prevX.size();
//        avgY /= prevX.size();
//
//        prevAvgX = avgX;
//        prevAvgY = avgY;
//
//        return new Position(avgX, avgY);
    }
}
