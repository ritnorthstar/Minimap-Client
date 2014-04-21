package com.northstar.minimap;

import com.devsmart.android.ui.HorizontalListView;
import com.northstar.minimap.itinerary.Itinerary;
import com.northstar.minimap.itinerary.ItineraryPoint;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ItineraryFragment extends Fragment {
	
	private MapActivity mapActivity;
	private HorizontalListView itineraryListView;
	private Itinerary itinerary;
	
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
        mapActivity = (MapActivity)activity;
    }
	
	public void setItinerary(Itinerary itinerary){
		this.itinerary = itinerary;
		itineraryListView.setAdapter(itineraryAdapter);
		itineraryListView.setOnItemClickListener(itinerarySelector);
	}
	
	private OnItemClickListener itinerarySelector = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			ItineraryPoint currentPoint = itinerary.getItineraryPoint(position);
			mapActivity.setCurrentItineraryPoint(currentPoint);
		}
		
	};
	
	private BaseAdapter itineraryAdapter = new BaseAdapter() {
		
		@Override
        public int getCount() {
            return itinerary.getCount();
        }

		@Override
		public Object getItem(int position) {
			return itinerary.getItineraryPoint(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			View retVal = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_list_item, null);
			TextView title = (TextView) retVal.findViewById(R.id.title);
            title.setText(itinerary.getItineraryPoint(position).getName());
            
            return retVal;
		}
		
	};
	
}
