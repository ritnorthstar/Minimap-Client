//John Paul Mardelli
//Last updated November 9th, 2013

package com.northstar.minimap.bluetooth;

import android.bluetooth.BluetoothAdapter;

/**
 * Class to provide a container for the BluetoothAdapter. Like the
 * adapter, this will also be a singleton, but will add on
 * bluetooth setup and functions that specifically fit our needs.
 */
public class BluetoothAccess {

    //Eager initialization for now, since bluetooth is our only tech.
    private static BluetoothAccess instance = new BluetoothAccess();

    private static BluetoothAdapter adapter;

    private static String deviceBTName;
    private static String deviceBTAddress;

    /**
     * Initialize this class by initializing the wrapped adapter.
     */
    private BluetoothAccess(){
        adapter = BluetoothAdapter.getDefaultAdapter();

        //Check if bluetooth is even supported on device.
        if(adapter != null){
            if (adapter.isEnabled()) {
                deviceBTName = adapter.getName();
                deviceBTAddress = adapter.getAddress();
            }
            else{
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * Method to grab the singleton of this class
     * @return the single instance of this class.
     */
    public static BluetoothAccess getInstance() {
        return instance;
    }

    /**
     * Method to get the adapter this class is wrapping to make calls
     * directly to it.
     * Shouldn't be used; all calls we make should be made into
     * functions.
     * @return
     */
    public BluetoothAdapter getAdapter(){
        return adapter;
    }

    public String getName(){
        return deviceBTName;
    }

    public String getAddress(){
        return deviceBTAddress;
    }

    public void startBLEScan(){
        //adapter.startLeScan();
    }

    public void stopBLEScan(){

    }

}
