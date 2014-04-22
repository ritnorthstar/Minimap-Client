package com.northstar.minimap;

import com.northstar.minimap.beacon.BeaconListener;
import com.northstar.minimap.beacon.BeaconManager;
import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;
import com.northstar.minimap.map.UserPositionListener;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends Activity implements SensorEventListener {

    public static final String KEY_ENV = "ENV";

    public static final int ENV_PRODUCTION = 1;
    public static final int ENV_TEST = 2;

    public static final int MAP_HEIGHT = 600;
    public static final int MAP_WIDTH = 600;

    private float[] gravityValues;
    private float[] magneticValues;

    private BeaconListener beaconListener;
    private BeaconManager beaconManager;
    private Bundle configBundle;
    private List<IBeacon> beacons;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager sensorManager;
    private UserPositionListener userPositionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize beacon ids
        StickNFindBluetoothBeacon.initBeaconIdMap();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            configBundle = this.getIntent().getExtras();
            switch (configBundle.getInt(KEY_ENV)) {
                case ENV_TEST:
                    initTestEnvironment();
                    break;
                case ENV_PRODUCTION:
                    processMap();
                    break;
                default:
                    initTestEnvironment();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleBeaconCirclesMenuItem:
                beaconListener.setBeaconCirclesVisible(!beaconListener.getBeaconCirclesVisible());
                break;
            case R.id.restartBluetoothMenuItem:
                beaconManager.restartBluetooth();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravityValues = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values;
                break;
            default:
                break;
        }

        if (gravityValues != null && magneticValues != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                if (userPositionListener != null) {
                    userPositionListener.onUserAzimuthChanged(orientation[0]);
                }
            }
        }
    }

    public void calibrate(Position calibrationPosition) {
        beaconManager.calibrate(calibrationPosition);
        Toast.makeText(this, R.string.calibrated, Toast.LENGTH_SHORT).show();
    }

    private void initTestEnvironment() {
        Position[] positions = new Position[] {
                new Position(0.0, 0.0),
                new Position(PositionCalculator.GRID_WIDTH, 0.0),
                new Position(0.0, PositionCalculator.GRID_HEIGHT),
                new Position(PositionCalculator.GRID_WIDTH, PositionCalculator.GRID_HEIGHT)
        };

        beacons = new ArrayList<IBeacon>();
        for (int i = 1; i <= 4; i++) {
            beacons.add(new StickNFindBluetoothBeacon(
                    i, StickNFindBluetoothBeacon.beaconIdMap.get(i), positions[i - 1]));
        }

        beaconManager = new BeaconManager(this, beacons);
    }
    
    public void processMap() {
        CustomMapFragment mapFrag =
                (CustomMapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        if (configBundle != null) {
            switch (configBundle.getInt(KEY_ENV)) {
                case ENV_TEST:
                    mapFrag.setMap(testMap());
                    break;
                case ENV_PRODUCTION:
                    Globals state = (Globals)getApplicationContext();
                    CallbackListener l = new MapCallback(this);
                    state.comm.getMapsJson(l);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void setMap() {
    	CustomMapFragment mapFrag = (CustomMapFragment)getFragmentManager().findFragmentById(R.id.map_fragment);
    	Globals state = (Globals)getApplicationContext();
    	String jsonMap = state.comm.mapJson;

    	Map URLMap = new MapBuilder().getMap(jsonMap);
        beaconManager = new BeaconManager(this, URLMap.getBeacons());
        mapFrag.setMap(URLMap);
    }
    
    
    private Map testMap(){
    	Map testMap = new Map();
        
        testMap.setMapID("0");

        List<Table> tables = new ArrayList<Table>();

        for (Table table: tables) {
            testMap.addTable(table);
        }

        for (IBeacon beacon: beacons) {
            testMap.addBeacon(beacon);
        }
        
        return testMap;
    }

    public void setBeaconListener(BeaconListener beaconListener) {
        this.beaconListener = beaconListener;
        beaconManager.setBeaconListener(beaconListener);
    }

    public void setUserPositionListener(UserPositionListener userPositionListener) {
        this.userPositionListener = userPositionListener;
        beaconManager.setUserPositionListener(userPositionListener);
    }

    public static Position toMapPosition(Position measuredPosition) {
        double x = measuredPosition.getX() / PositionCalculator.GRID_WIDTH * MAP_WIDTH;
        double y = measuredPosition.getY() / PositionCalculator.GRID_HEIGHT * MAP_HEIGHT;

        return new Position((int) Math.round(x), (int) Math.round(y));
    }

    public static Position toMeasuredPosition(Position mapPosition) {
        double x = mapPosition.getX() / MAP_WIDTH * PositionCalculator.GRID_WIDTH;
        double y = mapPosition.getY() / MAP_HEIGHT * PositionCalculator.GRID_HEIGHT;

        return new Position(x, y);
    }
}
