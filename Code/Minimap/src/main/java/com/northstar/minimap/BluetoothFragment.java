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
    private Activity activity;
    private ArrayAdapter<String> beaconAdapter;
    private Button calibrateButton;
    private Handler handler;
    private List<String> beaconList;
    private List<String> knownBeacons;
    private ListView beaconListView;
    private PositionCalculator positionCalculator;
    private TextView positionTextView;

    private Map<Integer, Position> beaconLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        beaconListView = (ListView) layout.findViewById(R.id.beacon_list);
        positionTextView = (TextView) layout.findViewById(R.id.positionTextView);

        beaconList = new ArrayList<String>();
        beaconAdapter = new ArrayAdapter<String>(activity, R.layout.list_beacon, beaconList);
        beaconListView.setAdapter(beaconAdapter);

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }



    /**
     * Updates the indication of the user position based on the current location of the beacons.
     */
//    private void updateUserPosition() {
//        if (beaconMap.size() == 4) {
//            IBeacon[] beacons = new IBeacon[] {
//                    beaconMap.get(1), beaconMap.get(2), beaconMap.get(3), beaconMap.get(4)
//            };
//
//            Position userLocation = positionCalculator.multilaterate(beacons);
//
//            String xString = new DecimalFormat("#.##").format(userLocation.getX());
//            String yString = new DecimalFormat("#.##").format(userLocation.getY());
//
//            // Strings for 5 x 5 square setup
//            String quadrantXString = new DecimalFormat("#.##").format(
//                    (int)(userLocation.getX() / (5/2)));
//            String quadrantYString = new DecimalFormat("#.##").format(
//                    (int)(userLocation.getY() / (5/2)));
//
//            String text = "(" + xString + ", " + yString + ")";
//            text += " - (" + quadrantXString + ", " + quadrantYString + ")";
//            positionTextView.setText(text);
//        }
//    }
//
//    private void updateListView() {
//        beaconList.clear();
//
//        // Update the list
//        for (int key: beaconMap.keySet()) {
//            String distanceString =
//                    beaconMap.get(key).getFormattedDistance();
//            beaconList.add(key + ": " + distanceString);
//        }
//
//        beaconAdapter.notifyDataSetChanged();
//        updateUserPosition();
//    }
    

}
