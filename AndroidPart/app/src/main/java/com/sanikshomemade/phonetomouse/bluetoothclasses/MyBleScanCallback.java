package com.sanikshomemade.phonetomouse.bluetoothclasses;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import com.sanikshomemade.phonetomouse.activities.BluetoothConfigActivity;

import java.util.List;

public class MyBleScanCallback extends ScanCallback {

    private BluetoothConfigActivity _parentActivity;
    public MyBleScanCallback(BluetoothConfigActivity activity) { _parentActivity = activity; }
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        _parentActivity.OnVisibleDeviceDiscovered(result.getDevice());
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for(ScanResult res : results) {
            _parentActivity.OnVisibleDeviceDiscovered(res.getDevice());
        }
    }
}
