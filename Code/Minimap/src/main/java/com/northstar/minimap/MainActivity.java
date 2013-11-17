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
    private DrawerLayout drawerLayout;
    private ListView leftDrawerListView;
    private ListView rightDrawerListView;
    private String[] leftDrawerItems;
    private String[] rightDrawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftDrawerItems = getResources().getStringArray(R.array.left_drawer_items);
        rightDrawerItems = getResources().getStringArray(R.array.right_drawer_items);

        leftDrawerListView = (ListView) findViewById(R.id.left_drawer);
        leftDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, leftDrawerItems));

        rightDrawerListView = (ListView) findViewById(R.id.right_drawer);
        rightDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, rightDrawerItems));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        leftDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        rightDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_open_right_drawer:
                if(!drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.openDrawer(Gravity.RIGHT);
                else
                    drawerLayout.closeDrawer(Gravity.RIGHT);

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void submitIP(View view) {
        Intent mapIntent = new Intent(this, MapActivity.class);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            drawerLayout.closeDrawer(leftDrawerListView);
            drawerLayout.closeDrawer(rightDrawerListView);
        }
    }
}
