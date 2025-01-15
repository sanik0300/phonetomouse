package com.sanikshomemade.phonetomouse.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public abstract class BluetoothListCustomAdapter<T> extends BaseAdapter {

    List<BluetoothDevice> _devices;
    LayoutInflater lInflater;
    int resid;

    public BluetoothListCustomAdapter(Context ctx, int resourceID,  List<BluetoothDevice> devices) {
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this._devices = devices;
        this.resid = resourceID;
    }

    @Override
    public int getCount() {
        return _devices.size();
    }

    @Override
    public Object getItem(int position) {
        return _devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = lInflater.inflate(resid, null);

        TextView tv = result.findViewById(android.R.id.text1);

        BluetoothDevice device = _devices.get(position);
        String tvContent = device.getName()==null? device.getAddress() : device.getName();
        tv.setText(tvContent);

        return  result;
    }
}
