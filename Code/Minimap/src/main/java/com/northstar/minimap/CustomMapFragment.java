//John Paul Mardelli
//Last updated November 19th, 2013

package com.northstar.minimap;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.map.BluetoothLELocationSource;
import com.northstar.minimap.map.BoundaryLocationSource;
import com.northstar.minimap.map.Map;
import com.northstar.minimap.map.Table;
import com.northstar.minimap.map.UserPositionListener;

/**
 * Code gotten from: http://www.matt-reid.co.uk/blog_post.php?id=93
 */
public class CustomMapFragment extends Fragment implements UserPositionListener {

	// Google and Android objects
    private MapView mapView;
    private GoogleMap googleMap;
    private Projection proj;
    private BoundaryLocationSource locSource;
    
    private MapActivity parentAct;
    private Map map;
    private List<LatLngBounds> boundBoxes = new ArrayList<LatLngBounds>();
    private LatLngBounds mapBounds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        locSource = new BluetoothLELocationSource();
    	
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();//needed to get the map to display immediately

        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {}
        
        googleMap = mapView.getMap();
        
        //Initial zoom for projection
        googleMap.moveCamera(CameraUpdateFactory.zoomTo((float) 15.0));

        //I added this. Use this to get rid of googles map stuff and put our custom stuff
        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        
        //Set up location stuff
        googleMap.setMyLocationEnabled(true);

        googleMap.setLocationSource(locSource);

        //customTP = new CustomTileProvider(filename, height, width);
        //overlayTOps = new TileOverlayOptions()
        //overlayTOps.tileProvider(customTP);
        //googleMap.addTileOverlay(overlayTOps);

        //Make reference to parent for listener
        parentAct = (MapActivity)this.getActivity();
        
        //Set up Global Layout listener to get projection at right time
        OnGlobalLayoutListener ready = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				
				//Remove listener
				mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				
				//Grab and set the projection
				setProjection();
				
				//Inform activity we are ready to process map.
				parentAct.processMap();
			}
        	
        };
        
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(ready);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        locSource.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locSource.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onUserPositionChanged(Position userPosition) {
        Position userMapPosition = MapActivity.toMapPosition(userPosition);
        LatLng userLatLng = proj.fromScreenLocation(userMapPosition.toPoint());
        Location userLocation = new Location("");
        userLocation.setLatitude(userLatLng.latitude);
        userLocation.setLongitude(userLatLng.longitude);
        userLocation.setAccuracy(100);
        locSource.setLocation(userLocation);
    }
    
    private void setProjection() {
    	proj = googleMap.getProjection();
    }
    
    public void setMap(Map map){
    	googleMap.clear();
    	
    	this.map = map;
    	locSource.setMap(this.map);
		
    	drawBeaconMarkers();
    	processTables();
    	
    	//Temporary location set
    	Location location = new Location("TempProviderWhatever");
    	LatLng coordinate = proj.fromScreenLocation(new Point(0, 0));
    	location.setLatitude(coordinate.latitude);
        location.setLongitude(coordinate.longitude);
        location.setAccuracy(100);
    	locSource.setLocation(location);

        parentAct.setUserPositionListener(this);
    }
    
    private void drawBeaconMarkers(){
    	
    	List<IBeacon> beacons = map.getBeacons();
    	for(int i = 0; i < beacons.size(); i++){
    		
    		IBeacon beacon = beacons.get(i);
    		
    		MarkerOptions beaconMarkerOptions = new MarkerOptions();
    		
    		//Set the position of the marker
    		LatLng markerLoc = proj.fromScreenLocation(new Point((int)beacon.getPosition().getX(), (int)beacon.getPosition().getY()));
    		beaconMarkerOptions = beaconMarkerOptions.position(markerLoc);
    		
    		//Set the title of the marker
    		beaconMarkerOptions = beaconMarkerOptions.title(beacon.getID());
    		
    		googleMap.addMarker(beaconMarkerOptions);
    	}
    }
    
    private void processTables(){
    	List<Table> tables = map.getTables();
    	for(int i = 0; i < tables.size(); i++){
    		Table table = tables.get(i);
    		
    		storeBoundBoxes(table);
    		drawTables(table);
    	}	
    }
    
    private void storeBoundBoxes(Table table){
    	LatLng neCorner = proj.fromScreenLocation(new Point((int)table.getPosition().getX() + table.getWidth(),
    								 						(int)table.getPosition().getY()));
    	LatLng swCorner = proj.fromScreenLocation(new Point((int)table.getPosition().getX(),
    								 						(int)table.getPosition().getY() + table.getHeight()));
    	LatLngBounds bound = new LatLngBounds(swCorner, neCorner);
    	boundBoxes.add(bound);
    }
    
    private void drawTables(Table table){
        
    	double heightInc = table.getHeight()/table.getHeightSubdivisions();
    	double widthInc = table.getWidth()/table.getWidthSubdivisions();
    	
    	for(int wSub = 0; wSub < table.getWidthSubdivisions(); wSub++){
    		for(int hSub = 0; hSub < table.getHeightSubdivisions(); hSub++){
    			
    			PolygonOptions tableSquare = new PolygonOptions();
    			
    			double leftX = (table.getPosition().getX() + 
    							widthInc * wSub);
    			double rightX = (table.getPosition().getX() + 
								 widthInc * (wSub + 1));
    			double topY = (table.getPosition().getY() + 
							   heightInc * hSub);
    			double bottomY = (table.getPosition().getY() + 
						   		  heightInc * (hSub + 1));
    			
    			
    			LatLng topLeft = proj.fromScreenLocation(new Point((int)leftX, (int)topY));
    			LatLng bottomLeft = proj.fromScreenLocation(new Point((int)leftX, (int)bottomY));
    			LatLng bottomRight = proj.fromScreenLocation(new Point((int)rightX, (int)bottomY));
    			LatLng topRight = proj.fromScreenLocation(new Point((int)rightX, (int)topY));
    			
    			tableSquare = tableSquare.add(topLeft, bottomLeft, bottomRight, topRight, topLeft);
    			
    			tableSquare = tableSquare.strokeColor(Color.RED);
    			
    			tableSquare = tableSquare.strokeWidth(4);
    			
    			googleMap.addPolygon(tableSquare);
    		}
    	}
    }

}
