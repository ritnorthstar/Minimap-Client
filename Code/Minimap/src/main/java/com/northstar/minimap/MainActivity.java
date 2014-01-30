package com.northstar.minimap;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.InetAddress;
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
        ipTextbox.setText("0.0.0.0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void submitIP(View view) {
        Intent mapIntent = new Intent(this, DrawerActivity.class);
        EditText ipTextbox = (EditText) findViewById(R.id.server_ip);
        String serverIP = ipTextbox.getText().toString();
        String ipErrorMessage = "no error!";

        try {
            Globals state = (Globals)getApplicationContext();
            state.comm.setServerIP(InetAddress.getByName(serverIP));
            mapIntent.putExtra(IP_ERROR_MESSAGE, ipErrorMessage);
            startActivity(mapIntent);
        } catch(Exception e) {
            // Incorrect IP address format
            ipErrorMessage = e.getMessage();

            TextView errorText = (TextView) findViewById(R.id.ip_error_text_view);
            errorText.setText("\"" + serverIP +"\" isn't a valid IP address.\nIt should be something like \"192.168.1.1\"");
            errorText.setTextColor(Color.RED);
            ipTextbox.setText("");
        }
    }
}
