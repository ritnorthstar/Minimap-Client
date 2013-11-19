//John Paul Mardelli
//Last updated November 19th, 2013

package com.northstar.minimap.map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.*;

/**
 * Code gotten from: http://www.matt-reid.co.uk/blog_post.php?id=93
 */
public class CustomMapFragment extends Fragment{

    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        //Perform any camera updates here

        //I added this. Use this to get rid of googles map stuff and put our custom stuff
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

        //customTP = new CustomTileProvider(filename, height, width);
        //overlayTOps = new TileOverlayOptions()
        //overlayTOps.tileProvider(customTP);
        //googleMap.addTileOverlay(overlayTOps);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
