package com.northstar.minimap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerActivity extends FragmentActivity implements CallbackListener {


    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ListView leftDrawerListView;
    private ListView rightDrawerListView;
    private String[] leftDrawerItems;
    private String[] rightDrawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        initDrawers();
        requestJson();
    }

    private void requestJson() {
        Globals state = (Globals)getApplicationContext();
        state.comm.getMapsJson(this);
    }

    public void jsonCallback() {
        TextView jsonDisplay = (TextView) findViewById(R.id.json_view);
        Globals.log("> In callback");
        Globals state = (Globals)getApplicationContext();
        Globals.log(">" + state.data.mapsJson);
        jsonDisplay.setText(state.data.mapsJson);
    }

    private void initDrawers() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        leftDrawerItems = getResources().getStringArray(R.array.left_drawer_items);
        leftDrawerListView = (ListView) findViewById(R.id.left_drawer);
        leftDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, leftDrawerItems));
        leftDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

        rightDrawerItems = getResources().getStringArray(R.array.right_drawer_items);
        rightDrawerListView = (ListView) findViewById(R.id.right_drawer);
        rightDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, rightDrawerItems));
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
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }

            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_open_right_drawer:
                if (!drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Toast.makeText(DrawerActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            drawerLayout.closeDrawer(leftDrawerListView);
            drawerLayout.closeDrawer(rightDrawerListView);
        }
    }
}
