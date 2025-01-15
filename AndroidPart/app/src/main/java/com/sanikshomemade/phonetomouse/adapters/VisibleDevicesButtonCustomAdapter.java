package com.sanikshomemade.phonetomouse.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageButton;
import com.sanikshomemade.phonetomouse.R;
import com.sanikshomemade.phonetomouse.activities.BluetoothConfigActivity;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BtUtils;

import java.util.List;

public class VisibleDevicesButtonCustomAdapter<T> extends BluetoothListCustomAdapter<T> {

    public VisibleDevicesButtonCustomAdapter(Context ctx, int resourceID, List<BluetoothDevice> devices) {
        super(ctx, resourceID, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        AppCompatImageButton _imageButton = v.findViewById(R.id.pair_request_btn);

        _imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _devices.get(position).createBond();

                BtUtils.RefreshPairedDevices(v.getContext());
                ListView otherListView = ((BluetoothConfigActivity)v.getContext()).findViewById(R.id.list_paired);
                ((BaseAdapter)otherListView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(v.getContext().getApplicationContext(), "There will be pair request in a few seconds", Toast.LENGTH_LONG).show();
                VisibleDevicesButtonCustomAdapter.this._devices.remove(position);
                VisibleDevicesButtonCustomAdapter.this.notifyDataSetChanged();
            }
        });
        return v;
    }
}
