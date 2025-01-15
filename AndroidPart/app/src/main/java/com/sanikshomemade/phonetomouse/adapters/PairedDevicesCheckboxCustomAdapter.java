package com.sanikshomemade.phonetomouse.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import com.sanikshomemade.phonetomouse.PrefUtils;
import com.sanikshomemade.phonetomouse.R;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BtUtils;

import java.util.List;

public class PairedDevicesCheckboxCustomAdapter<T> extends BluetoothListCustomAdapter<T> {

    int selectedItem;
    public int getSelectedItem() {
        return selectedItem;
    }
    public PairedDevicesCheckboxCustomAdapter(Context ctx, int resourceID, List<BluetoothDevice> devices, int selectedItem) {
        super(ctx, resourceID, devices);
        this.selectedItem = selectedItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentItem = super.getView(position, convertView, parent);
        AppCompatCheckBox myCheckBox = currentItem.findViewById(R.id.is_saved_check);
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView myText = currentItem.findViewById(android.R.id.text1);
                if(isChecked) {
                    if(selectedItem >= 0) {
                        ListView parentList = (ListView)parent;
                        ViewGroup otherItem = (ViewGroup)parentList.getChildAt(selectedItem);
                        if(otherItem != null) {
                            AppCompatCheckBox otherCheck = otherItem.findViewById(R.id.is_saved_check);
                            otherCheck.setChecked(false);
                            otherCheck.setEnabled(true);
                        }
                    }
                    myText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    buttonView.setEnabled(false);
                    selectedItem = position;

                    BluetoothDevice device = _devices.get(position);
                    PrefUtils.SaveDeviceInfo(parent.getContext(),device.getName(), device.getAddress());
                    BtUtils.SelectDeviceToConnectByIndex(position);
                }
                else {
                    myText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        });

        if(position == selectedItem) {
            myCheckBox.setChecked(true);
        }
        return currentItem;
    }
}
