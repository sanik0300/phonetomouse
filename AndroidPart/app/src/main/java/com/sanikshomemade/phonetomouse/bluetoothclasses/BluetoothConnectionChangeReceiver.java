package com.sanikshomemade.phonetomouse.bluetoothclasses;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sanikshomemade.phonetomouse.MouseFakingManager;
import com.sanikshomemade.phonetomouse.MyApp;

public class BluetoothConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) { return; }

        int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
        switch (bluetoothState) {
            case BluetoothAdapter.STATE_TURNING_OFF:
                if(BtUtils.ConnectionExists()) {
                    BtUtils.SendBytes(MouseFakingManager.GetDisconnectConfirmationBytes(false, false));
                    BtUtils.Disconnect(context);
                    System.out.println("disconnecting because bluetooth tutu");
                }
                break;
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_OFF:
                MyApp.currentBtDependentActivity.OnBluetoothStatusChanged(bluetoothState==BluetoothAdapter.STATE_ON);
                break;
        }
    }
}
