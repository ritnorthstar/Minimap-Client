package com.northstar.minimap;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import android.os.Build;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String error = intent.getStringExtra(MainActivity.IP_ERROR_MESSAGE);

        Globals state = (Globals)getApplicationContext();

        TextView ipDisplay = new TextView(this);
        ipDisplay.setTextSize(24);
        ipDisplay.setText(error);

        if(state.comm.getServerIP() != null)
        {
            ipDisplay.setTextSize(40);
            ipDisplay.setText(state.comm.getServerIP().toString());
        }
        else
        {
            ipDisplay.setTextSize(24);
            ipDisplay.setText(error);
        }

        setContentView(ipDisplay);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }
    }

}
