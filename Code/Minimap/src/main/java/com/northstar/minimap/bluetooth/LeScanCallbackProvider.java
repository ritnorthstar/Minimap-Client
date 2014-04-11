package com.northstar.minimap.bluetooth;

import android.bluetooth.BluetoothAdapter;

public interface LeScanCallbackProvider {
    public BluetoothAdapter.LeScanCallback createLeScanCallback();
}
