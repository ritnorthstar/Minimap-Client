package com.northstar.minimap;

import com.devsmart.android.ui.HorizontalListView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ItineraryFragment extends Fragment {
	
	private Activity activity;
	private HorizontalListView itineraryListView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_itinerary, container, false);
        
        itineraryListView = (HorizontalListView) layout.findViewById(R.id.itinerary_list);
        
        return layout;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
	
}
