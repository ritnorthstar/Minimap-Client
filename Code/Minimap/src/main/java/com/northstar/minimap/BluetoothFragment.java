package com.northstar.minimap;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BluetoothFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;

    private boolean scanning = false;
    private int scans = 0;

    private Activity activity;
    private ArrayAdapter<String> beaconAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Button calibrateButton;
    private Handler handler;
    private List<String> beaconList;
    private List<String> knownBeacons;
    private ListView beaconListView;
    private PositionCalculator positionCalculator;
    private TextView positionTextView;
    private Map<Integer, IBeacon> beaconMap;
    private Map<Integer, Position> beaconLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        beaconListView = (ListView) layout.findViewById(R.id.beacon_list);
        positionTextView = (TextView) layout.findViewById(R.id.positionTextView);
        calibrateButton = (Button) layout.findViewById(R.id.calibrateButton);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calibrate();
            }
        });

        initBeacons();
        initBluetooth();

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        initBluetooth();
    }

    /**
     * Assumes beacons placed at 1 (#1) and 5 (#4) meters.
     */
    private void calibrate() {
        Map<Double, Double> distanceMap = new HashMap<Double, Double>();

        try {
            distanceMap.put(1.0, ((StickNFindBluetoothBeacon) beaconMap.get(
                    knownBeacons.get(0))).getAverageRssi());
            distanceMap.put(5.0, ((StickNFindBluetoothBeacon) beaconMap.get(
                    knownBeacons.get(3))).getAverageRssi());

            positionCalculator.calibrate(distanceMap);
        } catch (NullPointerException e) {
            Globals.log("Cannot calibrate without all four beacons.");
        }
    }

    /**
     * Initialize known beacons and locations.
     */
    private void initBeacons() {
        StickNFindBluetoothBeacon.initBeaconIdMap();
        positionCalculator = new PositionCalculator();

        beaconMap = new HashMap<Integer, IBeacon>();
        beaconLocations = new HashMap<Integer, Position>();
        beaconLocations.put(1, new Position(0.0, 0.0));
        beaconLocations.put(2, new Position(5.0, 0.0));
        beaconLocations.put(3, new Position(0.0, 5.0));
        beaconLocations.put(4, new Position(5.0, 5.0));

        knownBeacons = new ArrayList<String>();
        for (Integer number: StickNFindBluetoothBeacon.beaconIdMap.keySet()) {
            knownBeacons.add(StickNFindBluetoothBeacon.beaconIdMap.get(number));
        }
    }

    /**
     * Initialize bluetooth adapter and associated list view.
     */
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

        if (!scanning) {
            scanning = true;
            scanLeDevice();
        }

        return true;
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

    /**
     * Updates the indication of the user position based on the current location of the beacons.
     */
    private void updateUserPosition() {
        if (beaconMap.size() == 4) {
            IBeacon[] beacons = new IBeacon[] {
                    beaconMap.get(1), beaconMap.get(2), beaconMap.get(3), beaconMap.get(4)
            };

            Position userLocation = positionCalculator.multilaterate(beacons);

            String xString = new DecimalFormat("#.##").format(userLocation.getX());
            String yString = new DecimalFormat("#.##").format(userLocation.getY());

            // Strings for 5 x 5 square setup
            String quadrantXString = new DecimalFormat("#.##").format(
                    (int)(userLocation.getX() / (5/2)));
            String quadrantYString = new DecimalFormat("#.##").format(
                    (int)(userLocation.getY() / (5/2)));

            String text = "(" + xString + ", " + yString + ")";
            text += " - (" + quadrantXString + ", " + quadrantYString + ")";
            positionTextView.setText(text);
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

                                int number = StickNFindBluetoothBeacon.getBeaconNumber(address);

                                // Create Beacon if it doesn't exist.
                                if (!beaconMap.containsKey(number)) {
                                    beaconMap.put(number,
                                            new StickNFindBluetoothBeacon(
                                                    device, address, beaconLocations.get(number)));
                                }

                                // Update beacon's signal strength.
                                beaconMap.get(number).setSignalStrength(rssi);
                                beaconList.clear();

                                // Update the list
                                for (int key: beaconMap.keySet()) {
                                    String distanceString =
                                            beaconMap.get(key).getFormattedDistance();
                                    beaconList.add(key + ": " + distanceString);
                                }

                                beaconAdapter.notifyDataSetChanged();
                                updateUserPosition();
                            }
                        });
                    }
                };
    }
}
