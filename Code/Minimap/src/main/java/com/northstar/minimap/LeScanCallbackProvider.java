package com.northstar.minimap;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Chris on 4/11/14.
 */
public interface LeScanCallbackProvider {
    public BluetoothAdapter.LeScanCallback createLeScanCallback();
}
