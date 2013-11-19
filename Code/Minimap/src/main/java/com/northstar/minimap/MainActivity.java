package com.northstar.minimap;
import android.graphics.Color;
import android.provider.Settings;
import android.content.Intent;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    public final static String IP_ERROR_MESSAGE = "com.northstar.minimap.MESSAGE";

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            errorText.setText("\"" + serverIP + e.getMessage());
            errorText.setTextColor(Color.RED);
            ipTextbox.setText("");
        }
    }
}
