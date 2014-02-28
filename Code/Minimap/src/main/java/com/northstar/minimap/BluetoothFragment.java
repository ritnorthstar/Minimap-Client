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

/**
 * Created by Chris on 12/9/13.
 */
public class BluetoothFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;

    public static final int SMOOTHING_RANGE = 3;

    private boolean scanning = false;
    private int scans = 0;

    private Activity activity;
    private ArrayAdapter<String> beaconAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Handler handler;
    private List<String> beaconList;
    private ListView beaconListView;
    private ProgressBar positionProgressBar;
    private TextView positionTextView;
    private Map<String, IBeacon> beaconMap;

    private Map<String, PointF> beaconLocations;

    private LinkedList<Double> previousPositions;

    private List<String> knownBeacons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        beaconListView = (ListView) layout.findViewById(R.id.beacon_list);
        positionTextView = (TextView) layout.findViewById(R.id.positionTextView);
        positionProgressBar = (ProgressBar) layout.findViewById(R.id.positionProgressBar);
        previousPositions = new LinkedList<Double>();
        initBluetooth();

        beaconMap = new HashMap<String, IBeacon>();

        beaconLocations = new HashMap<String, PointF>();
        beaconLocations.put("FE:CC:38:AA:BE:B5", new PointF(0.0f, 0.0f));
        beaconLocations.put("FD:65:28:71:80:C0", new PointF(1.5f, 0.0f));
        beaconLocations.put("E4:3E:A0:63:BC:C6", new PointF(0.0f, 3.7f));
        beaconLocations.put("E3:BF:2E:56:BF:B9", new PointF(0.0f, 3.7f));

        knownBeacons = new ArrayList<String>();
        knownBeacons.add("FE:CC:38:AA:BE:B5");
        knownBeacons.add("FD:65:28:71:80:C0");
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

    private void updatePosition() {
//        if (leftBeacon == null || rightBeacon == null) {
//            return;
//        }
//
//        double leftPosition = Math.min(20, leftBeacon.computeDistance());
//        double rightPosition = BEACON_DISTANCE - Math.min(20, rightBeacon.computeDistance());
//        double uncertainty = rightPosition - leftPosition;
//        double position = ((leftPosition + rightPosition) / 2.0) / BEACON_DISTANCE * 100;
//
//        double smoothedPosition = 0.0;
//
//        if (previousPositions.size() == SMOOTHING_RANGE) {
//            previousPositions.removeFirst();
//        }
//
//        previousPositions.addLast(position);
//
//        for (double previousPosition: previousPositions) {
//            smoothedPosition += previousPosition;
//        }
//
//        smoothedPosition /= previousPositions.size();
//
//        int zone = (int)(smoothedPosition / 25);
//
//        String uncertaintyString = new DecimalFormat("#.##").format(uncertainty);
//        String text = Integer.toString(zone) + " (" + Integer.toString((int) smoothedPosition) + "%) [" + uncertaintyString + "]";

        String text = Integer.toString(beaconMap.size());

        if (beaconMap.size() == 50) {
            double d = 1.5;
            double i = 0.0;
            double j = 5.0;

            double r1 = beaconMap.get("FE:CC:38:AA:BE:B5").computeDistance();
            double r2 = beaconMap.get("FD:65:28:71:80:C0").computeDistance();
            double r3 = beaconMap.get("E4:3E:A0:63:BC:C6").computeDistance();
            double r4 = beaconMap.get("").computeDistance();

            double sr1 = Math.pow(r1, 2);
            double sr2 = Math.pow(r2, 2);
            double sr3 = Math.pow(r3, 2);
            double sd = Math.pow(d, 2);
            double si = Math.pow(i, 2);
            double sj = Math.pow(j, 2);

            double x = (sr1 - sr2 + sd) / (2 * d);
            double y = ((sr1 - sr3 + si + sj) / (2 * j)) - ((i / j) * x);

            String xString = new DecimalFormat("#.##").format(x);
            String yString = new DecimalFormat("#.##").format(y);

            text = "(" + xString + ", " + yString + ")";
        }

        positionTextView.setText(text);
        //positionProgressBar.setProgress((int) smoothedPosition);
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
                                    beaconList.add(key + ": " + distanceString);
                                }

                                beaconAdapter.notifyDataSetChanged();
                                updatePosition();
                            }
                        });
                    }
                };
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
