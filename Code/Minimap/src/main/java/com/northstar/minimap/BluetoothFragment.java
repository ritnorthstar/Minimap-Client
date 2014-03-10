package com.northstar.minimap;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

/**
 * Created by Chris on 12/9/13.
 */
public class BluetoothFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;

    public static final int SMOOTHING_RANGE = 5;

    private boolean scanning = false;
    private int scans = 0;

    private Activity activity;
    private ArrayAdapter<String> beaconAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Button calibrateButton;
    private Handler handler;
    private LinkedList<Double> previousPositions;
    private List<String> beaconList;
    private List<String> knownBeacons;
    private ListView beaconListView;
    private TextView positionTextView;
    private Map<String, IBeacon> beaconMap;
    private Map<String, PointF> beaconLocations;

    private LinkedList<Double> prevX;
    private LinkedList<Double> prevY;
    double prevAvgX;
    double prevAvgY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        beaconListView = (ListView) layout.findViewById(R.id.beacon_list);
        positionTextView = (TextView) layout.findViewById(R.id.positionTextView);
        calibrateButton = (Button) layout.findViewById(R.id.calibrateButton);
        previousPositions = new LinkedList<Double>();

        calibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calibrate();
            }
        });

        initBluetooth();

        beaconMap = new HashMap<String, IBeacon>();
        prevX = new LinkedList<Double>();
        prevY = new LinkedList<Double>();

        beaconLocations = new HashMap<String, PointF>();
        beaconLocations.put("FD:65:28:71:80:C0", new PointF(0.0f, 0.0f));
        beaconLocations.put("FE:CC:38:AA:BE:B5", new PointF(22.0f, 0.0f));
        beaconLocations.put("E4:3E:A0:63:BC:C6", new PointF(0.0f, 5.0f));
        beaconLocations.put("E3:BF:2E:56:BF:B9", new PointF(22.0f, 5.0f));

        knownBeacons = new ArrayList<String>();
        knownBeacons.add("FD:65:28:71:80:C0");
        knownBeacons.add("FE:CC:38:AA:BE:B5");
        knownBeacons.add("E4:3E:A0:63:BC:C6");
        knownBeacons.add("E3:BF:2E:56:BF:B9");

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        initBluetooth();
    }

    private boolean initBluetooth() {
        if (activity == null || beaconListView == null) {
            return false;
        }

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        beaconList = new ArrayList<String>();
        beaconAdapter = new ArrayAdapter<String>(activity, R.layout.list_beacon, beaconList);
        beaconListView.setAdapter(beaconAdapter);

        if (bluetoothAdapter.getDefaultAdapter() == null) {
            beaconList.add("No bluetooth available");
            Globals.log("no bluetooth");
            beaconAdapter.notifyDataSetChanged();
            return false;
        }

        beaconList.add("BEACON LIST");

        if (!scanning) {
            scanning = true;
            scanLeDevice();
        }

        return true;
    }

    private double f(double x, double y, double xi, double yi, double r) {
        return Math.sqrt(Math.pow(x - xi, 2) + Math.pow(y - yi, 2)) - r;
    }

    private void updatePosition() {
        String text = Integer.toString(beaconMap.size());

        if (beaconMap.size() == 4) {
            // Non-Linear Least Squares Estimation

            PointF p1 = beaconLocations.get("FD:65:28:71:80:C0");
            PointF p2 = beaconLocations.get("FE:CC:38:AA:BE:B5");
            PointF p3 = beaconLocations.get("E4:3E:A0:63:BC:C6");
            PointF p4 = beaconLocations.get("E3:BF:2E:56:BF:B9");

            double r1 = beaconMap.get("FD:65:28:71:80:C0").computeDistance();
            double r2 = beaconMap.get("FE:CC:38:AA:BE:B5").computeDistance();
            double r3 = beaconMap.get("E4:3E:A0:63:BC:C6").computeDistance();
            double r4 = beaconMap.get("E3:BF:2E:56:BF:B9").computeDistance();

            double xi[] = new double[] { p1.x, p2.x, p3.x, p4.x };
            double yi[] = new double[] { p1.y, p2.y, p3.y, p4.y };
            double ri[] = new double[] { r1, r2, r3, r4 };

            double x = (xi[0] + xi[1] + xi[2] + xi[3]) / 4;
            double y = (yi[0] + yi[1] + yi[2] + yi[3]) / 4;

            for (int k = 0; k < 10; k++) {
                double j0 = 0.0;
                double j1 = 0.0;
                double j2 = 0.0;
                double j3 = 0.0;
                double j4 = 0.0;
                double j5 = 0.0;

                for (int i = 0; i < 4; i++) {
                    double f = f(x, y, xi[i], yi[i], ri[i]);
                    j0 += Math.pow(x - xi[i], 2) / Math.pow(f + ri[i], 2);
                    j1 += ((x - xi[i]) * (y - yi[i])) / Math.pow(f + ri[i], 2);
                    j2 += ((x - xi[i]) * (y - yi[i])) / Math.pow(f + ri[i], 2);
                    j3 += Math.pow(y - yi[i], 2) / Math.pow(f + ri[i], 2);

                    j4 += ((x - xi[i]) * f) / (f + ri[i]);
                    j5 += ((y - yi[i]) * f) / (f + ri[i]);
                }

                Matrix J1 = new Matrix(new double[][]{{j0, j1}, {j2, j3}});
                Matrix J2 = new Matrix(new double[][]{{j4}, {j5}});
                Matrix B = new Matrix(new double[][]{{x}, {y}});
                Matrix Bk = B.minus(J1.inverse().times(J2));

                x = Bk.get(0, 0);
                y = Bk.get(1, 0);
            }


            // Linear Least-Squares Estimation

//
//            double[][] ad = new double[3][2];
//            ad[0][0] = x[0] - x[3];
//            ad[1][0] = x[1] - x[3];
//            ad[2][0] = x[2] - x[3];
//            ad[0][1] = y[0] - y[3];
//            ad[1][1] = y[1] - y[3];
//            ad[2][1] = y[2] - y[3];
//
//            double[][] bd = new double[3][1];
//            bd[0][0] = 0.5*(x[0]*x[0] - x[3]*x[3] + y[0]*y[0] - y[3]*y[3] + r[3]*r[3] - r[0]*r[0]);
//            bd[1][0] = 0.5*(x[1]*x[1] - x[3]*x[3] + y[1]*y[1] - y[3]*y[3] + r[3]*r[3] - r[1]*r[1]);
//            bd[2][0] = 0.5*(x[2]*x[2] - x[3]*x[3] + y[2]*y[2] - y[3]*y[3] + r[3]*r[3] - r[2]*r[2]);
//
//            Matrix A = new Matrix(ad);
//            Matrix b = new Matrix(bd);
//            Matrix u = (A.transpose().times(A)).inverse().times(A.transpose().times(b));
//
//            double px = u.get(0, 0);
//            double py = u.get(1, 0);



            //

            // (a - x)^2 + (b - y)^2 = r^2

            // ax^2 + bx + c = y

            // y =  b - sqrt(-(a^2) + 2ax + r^2 - x^2)
            // y = -b + sqrt(-(a^2) + 2ax + r^2 - x^2)

            // dF/da = (a - x) / sqrt(-(a^2) + 2ax + r^2 - x^2)
            // dF/db = 1

            // dF/da = (x - a) / sqrt(-(a^2) + 2ax + r^2 - x^2)
            // dF/db = -1



            //



//        if (beaconMap.size() == 4) {
//            double d = 5.0;
//            double i = 0.0;
//            double j = 5.0;
//
//            double r1 = beaconMap.get("FE:CC:38:AA:BE:B5").computeDistance();
//            double r2 = beaconMap.get("FD:65:28:71:80:C0").computeDistance();
//            double r3 = beaconMap.get("E4:3E:A0:63:BC:C6").computeDistance();
//            double r4 = beaconMap.get("E3:BF:2E:56:BF:B9").computeDistance();
//
//            double sr1 = Math.pow(r1, 2);
//            double sr2 = Math.pow(r2, 2);
//            double sr3 = Math.pow(r3, 2);
//            double sd = Math.pow(d, 2);
//            double si = Math.pow(i, 2);
//            double sj = Math.pow(j, 2);
//
//            double x = (sr1 - sr2 + sd) / (2 * d);
//            double y = ((sr1 - sr3 + si + sj) / (2 * j)) - ((i / j) * x);
//
//            if (x < -0.5 || x > 5.5 || y < -0.5 || y > 5.5) {
//                return;
//            }

//            double j = y;
//            y = x;
//            x = j;
//
            boolean avg = true;

            if (prevX.size() == SMOOTHING_RANGE) {
                if (Math.sqrt(Math.pow(x - prevAvgX, 2) + Math.pow(y - prevAvgY, 2)) > 1.0) {
                    avg = false;
                }

                if (avg) {
                    prevX.removeLast();
                    prevY.removeLast();
                }
            }

            double avgX = 0.0;
            double avgY = 0.0;

            if (!avg) {
                avgX = prevAvgX;
                avgY = prevAvgY;
            } else {
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

                String xString = new DecimalFormat("#.##").format(avgX);
                String yString = new DecimalFormat("#.##").format(avgY);

                String avgXString = new DecimalFormat("#.##").format((int)(avgX / (22/4)));
                String avgYString = new DecimalFormat("#.##").format((int)(avgY / (4.5/2)));

                text = "(" + xString + ", " + yString + ")";
                text += " - (" + avgXString + ", " + avgYString + ")";
                positionTextView.setText(text);
            }
        }
    }
    
    private BluetoothAdapter.LeScanCallback createLeScanCallback() {
        // TODO: Remove unconnected beacons from beacon list after a certain period of time.

    	return new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi,
                                         byte[] scanRecord) {
                        Globals.log("RSSI: " + rssi);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String address = device.getAddress();

                                if (!knownBeacons.contains(address)) {
                                    return;
                                }

                                // Create Beacon if it doesn't exist.
                                if (!beaconMap.containsKey(address)) {
                                    beaconMap.put(address,
                                            new StickNFindBluetoothBeacon(device, null, null));
                                }

                                // Update beacon's signal strength.
                                beaconMap.get(address).setSignalStrength(rssi);
                                beaconList.clear();

                                // Update the list
                                for (String key: beaconMap.keySet()) {
                                    String distanceString =
                                            beaconMap.get(key).getFormattedDistance();
                                    int id = StickNFindBluetoothBeacon.beaconMap.get(key);
                                    beaconList.add(id + ": " + distanceString);
                                }

                                beaconAdapter.notifyDataSetChanged();
                                updatePosition();
                            }
                        });
                    }
                };
    }

    /**
     * Assumes beacons placed at 1, 2, 4, and 5 meters.
     * "FE:CC:38:AA:BE:B5" - 1
     * "FD:65:28:71:80:C0" - 2
     * "E4:3E:A0:63:BC:C6" - 4
     * "E3:BF:2E:56:BF:B9" - 5
     */
    private void calibrate() {
        Map<Double, Double> distanceMap = new HashMap<Double, Double>();

        try {
            distanceMap.put(1.0, ((StickNFindBluetoothBeacon) beaconMap.get(knownBeacons.get(0))).getAverageRssi());
            distanceMap.put(2.0, ((StickNFindBluetoothBeacon) beaconMap.get(knownBeacons.get(1))).getAverageRssi());
            distanceMap.put(4.0, ((StickNFindBluetoothBeacon) beaconMap.get(knownBeacons.get(2))).getAverageRssi());
            distanceMap.put(5.0, ((StickNFindBluetoothBeacon) beaconMap.get(knownBeacons.get(3))).getAverageRssi());

            PositionCalculator.calibrate(distanceMap);
        } catch (NullPointerException e) {
            Globals.log("Cannot calibrate without all four beacons.");
        }
    }

    private void scanLeDevice() {
    	// Create a new LeScanCallback for every scan.
    	leScanCallback = createLeScanCallback();
    	
        // Stops scanning after a pre-defined scan period.
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            bluetoothAdapter.stopLeScan(leScanCallback);
            Globals.log("Scan: " + scans);
            scanLeDevice();
            }
        }, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(leScanCallback);
        scans++;
    }
}
