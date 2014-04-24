package com.northstar.minimap;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.northstar.minimap.Globals;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    public final static String IP_ERROR_MESSAGE = "com.northstar.minimap.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText ipTextbox = (EditText) findViewById(R.id.server_ip);
        
        Globals state = (Globals)getApplicationContext();
        
        state.comm.setDataHolder(state.data);

        ipTextbox.setText("67.247.162.224:9000");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        launchTestMap();
        return super.onOptionsItemSelected(item);
    }

    public void launchSelectionActivity(View view) {
        Globals state = (Globals)getApplicationContext();
        Intent selectionIntent = new Intent(this, SelectionActivity.class);
        EditText ipTextbox = (EditText) findViewById(R.id.server_ip);
        String serverIP = ipTextbox.getText().toString();
        if (!serverIP.startsWith("http://")) {
            serverIP = "http://" + serverIP;
        }
        String ipErrorMessage = "no error!";
        state.log(serverIP);

        try {
            state.comm.setServerIP(new URL(serverIP));

            Bundle bundle = new Bundle();
            bundle.putString(IP_ERROR_MESSAGE, ipErrorMessage);
            selectionIntent.putExtras(bundle);

            startActivity(selectionIntent);
        } catch(Exception e) {
            // Incorrect IP address format
            ipErrorMessage = e.getMessage();

            TextView errorText = (TextView) findViewById(R.id.ip_error_text_view);
            errorText.setText("\"" + serverIP +"\" isn't a valid IP address.\nIt should be something like \"10.0.2.2:9000\"");
            errorText.setTextColor(Color.RED);
            ipTextbox.setText("");
        }
    }

    public void launchTestMap() {
        Bundle bundle = new Bundle();
        bundle.putInt(MapActivity.KEY_ENV, MapActivity.ENV_TEST);
    	Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);
    }
}
