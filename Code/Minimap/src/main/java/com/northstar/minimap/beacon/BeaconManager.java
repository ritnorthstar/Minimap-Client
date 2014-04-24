package com.northstar.minimap.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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

    private int beaconsScanned = 0;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;

    private final List<IBeacon> beacons;

    private Activity activity;
    private BeaconListener beaconListener;
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

        beaconMap = new HashMap<Integer, IBeacon>();


        //Initialize bluetooth adapter and associated list view.

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BeaconManager(Activity activity, List<IBeacon> beacons) {
        init(activity);
        scanLeDevice();
        this.beacons = beacons;
    }

    public BeaconManager(Activity activity, List<IBeacon> beacons,
                         LeScanCallbackProvider leScanCallbackProvider) {
        init(activity);
        this.leScanCallbackProvider = leScanCallbackProvider;
        scanLeDevice();
        this.beacons = beacons;
    }

    public BeaconManager(Activity activity, List<IBeacon> beacons,
                         UserPositionListener userPositionListener) {
        init(activity);
        this.userPositionListener = userPositionListener;
        scanLeDevice();
        this.beacons = beacons;
    }

    public BeaconManager(Activity activity, List<IBeacon> beacons,
                         LeScanCallbackProvider leScanCallbackProvider,
                         UserPositionListener userPositionListener) {
        this(activity, beacons, userPositionListener);
        this.leScanCallbackProvider = leScanCallbackProvider;
        scanLeDevice();
    }

    public void calibrate(Position calibrationPosition) {
        for (StickNFindBluetoothBeacon beacon: getSnfBeaconList()) {
            double d = calibrationPosition.distance(beacon.getPosition());
            beacon.calibrate(d);
        }
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
                    Log.d("BT-SCAN-UNKNOWN", device.getAddress() + " " + rssi);
                    return;
                }

                int number = StickNFindBluetoothBeacon.getBeaconNumber(address);
                Log.d("BT-SCAN-KNOWN", number + " " + device.getAddress() + " " + rssi);

                beaconsScanned++;

                // Add beacon if it doesn't exist.
                if (!beaconMap.containsKey(number)) {
                    for (IBeacon beacon: beacons) {
                        if (beacon.getNumber() == number) {
                            beaconMap.put(number, beacon);
                        }
                    }
                }

                // Update beacon's signal strength.
                IBeacon beacon = beaconMap.get(number);

                if (beacon == null) {
                    return;
                }

                beacon.setSignalStrength(rssi);
                ((StickNFindBluetoothBeacon) beacon).computeMedianRssi();

                if (beaconListener != null) {
                    beaconListener.onBeaconDistanceChanged(beacon, beacon.computeDistance());
                }

                Log.d("BT-REAL-SCAN", number + " " + rssi + " " + beacon.computeDistance());

                updateUserPosition();

                boolean isInProximityZone = (beacon.computeUnaveragedDistance() <
                        StickNFindBluetoothBeacon.PROXIMITY_ZONE_RANGE);
                if (isInProximityZone != beacon.isInProximityZone()) {
                    beacon.setInProximityZone(isInProximityZone);

                    if (beaconListener != null) {
                        beaconListener.onBeaconInProximityZoneChanged(beacon, isInProximityZone);
                    }

                    Log.d("BT-COMPASS", beacon.getNumber() + "");
                }
            }
        });
    }

    public void restartBluetooth() {
        Log.d("BT-BLUE-OFF", bluetoothAdapter.disable() + "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("BT-BLUE-ON", bluetoothAdapter.enable() + "");
            }
        }, 500);
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
                beaconsScanned = 0;
            }
        }, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(leScanCallback);
    }

    public void setBeaconListener(BeaconListener beaconListener) {
        this.beaconListener = beaconListener;
    }

    public void setUserPositionListener(UserPositionListener userPositionListener) {
        this.userPositionListener = userPositionListener;
    }

    private void updateUserPosition() {
        if (beaconMap.size() >= 4) {
            Position userPosition = positionCalculator.multilaterate(getBeaconList());

            if (userPositionListener != null && userPosition != null) {
                userPositionListener.onUserPositionChanged(userPosition);
            }
        }
    }
}
