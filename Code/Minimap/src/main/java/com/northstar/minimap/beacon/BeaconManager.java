package com.northstar.minimap.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.northstar.minimap.bluetooth.LeScanCallbackProvider;
import com.northstar.minimap.Position;
import com.northstar.minimap.PositionCalculator;
import com.northstar.minimap.map.UserPositionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 4/11/14.
 */
public class BeaconManager implements LeScanCallbackProvider {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;

    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private LeScanCallbackProvider leScanCallbackProvider;
    private Map<Integer, Position> beaconLocations;
    private Map<Integer, IBeacon> beaconMap;
    private PositionCalculator positionCalculator;
    private UserPositionListener userPositionListener;

    private void init(Activity activity) {
        this.activity = activity;

        leScanCallbackProvider = this;
        positionCalculator = new PositionCalculator();

        // Initialize beacons
        StickNFindBluetoothBeacon.initBeaconIdMap();
        beaconMap = new HashMap<Integer, IBeacon>();

        // Initialize hardcoded beacon locations;
        beaconLocations = new HashMap<Integer, Position>();
        beaconLocations.put(1, new Position(0.0, 0.0));
        beaconLocations.put(2, new Position(PositionCalculator.GRID_WIDTH, 0.0));
        beaconLocations.put(3, new Position(0.0, PositionCalculator.GRID_HEIGHT));
        beaconLocations.put(4, new Position(PositionCalculator.GRID_WIDTH,
                PositionCalculator.GRID_HEIGHT));

        //Initialize bluetooth adapter and associated list view.

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BeaconManager(Activity activity) {
        init(activity);
        scanLeDevice();
    }

    public BeaconManager(Activity activity, LeScanCallbackProvider leScanCallbackProvider) {
        init(activity);
        this.leScanCallbackProvider = leScanCallbackProvider;
        scanLeDevice();
    }

    public BeaconManager(Activity activity, UserPositionListener userPositionListener) {
        init(activity);
        this.userPositionListener = userPositionListener;
        scanLeDevice();
    }

    public BeaconManager(Activity activity, LeScanCallbackProvider leScanCallbackProvider,
                         UserPositionListener userPositionListener) {
        this(activity, userPositionListener);
        this.leScanCallbackProvider = leScanCallbackProvider;
        scanLeDevice();
    }

    public BluetoothAdapter.LeScanCallback createLeScanCallback() {
        final BeaconManager beaconManager = this;

        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi,
                                 byte[] scanRecord) {
                beaconManager.onLeScan(device, rssi, scanRecord);
            }
        };
    }

    public Map<Integer, IBeacon> getBeaconMap() {
        return beaconMap;
    }

    public Map<Integer, StickNFindBluetoothBeacon> getSnfBeaconMap() {
        Map<Integer, StickNFindBluetoothBeacon> map =
                new HashMap<Integer, StickNFindBluetoothBeacon>();

        for (Integer key: beaconMap.keySet()) {
            map.put(key, (StickNFindBluetoothBeacon) beaconMap.get(key));
        }

        return map;
    }

    public List<IBeacon> getBeaconList() {
        return new ArrayList(beaconMap.values());
    }

    public List<StickNFindBluetoothBeacon> getSnfBeaconList() {
        return new ArrayList(getSnfBeaconMap().values());
    }

    public void onLeScan(final BluetoothDevice device, final int rssi,
                         byte[] scanRecord) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String address = device.getAddress();

                if (!StickNFindBluetoothBeacon.beaconIdMap.containsValue(address)) {
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

                updateUserPosition();
            }
        });
    }

    private void scanLeDevice() {
        // Create a new LeScanCallback for every scan.
        leScanCallback = leScanCallbackProvider.createLeScanCallback();

        // Stops scanning after a pre-defined scan period.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.stopLeScan(leScanCallback);
                scanLeDevice();
            }
        }, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(leScanCallback);
    }

    public void setUserPositionListener(UserPositionListener userPositionListener) {
        this.userPositionListener = userPositionListener;
    }

    private void updateUserPosition() {
        if (beaconMap.size() == 4) {
            Position userPosition = positionCalculator.multilaterate(getBeaconList());

            if (userPositionListener != null) {
                userPositionListener.onUserPositionChanged(userPosition);
            }
        }
    }
}
