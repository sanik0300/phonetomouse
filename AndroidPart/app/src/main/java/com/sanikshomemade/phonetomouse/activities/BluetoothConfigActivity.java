package com.sanikshomemade.phonetomouse.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import com.sanikshomemade.phonetomouse.MyApp;
import com.sanikshomemade.phonetomouse.adapters.VisibleDevicesButtonCustomAdapter;
import com.sanikshomemade.phonetomouse.adapters.PairedDevicesCheckboxCustomAdapter;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BluetoothDevicesFindingReceiver;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BtUtils;
import com.sanikshomemade.phonetomouse.R;
import com.sanikshomemade.phonetomouse.bluetoothclasses.MyBleScanCallback;

import java.util.LinkedList;
import java.util.List;

public class BluetoothConfigActivity extends MyApp.BluetoothDependentCompatActivity {

    BluetoothDevicesFindingReceiver _receiver=new BluetoothDevicesFindingReceiver(this);
    BluetoothLeScanner _scanner;
    ScanCallback callback = new MyBleScanCallback(this);
    List<BluetoothDevice> oldVisibleList = null,
                          currentVisibleList =new LinkedList<>();

    ProgressBar discovProgressBar;
    int _max_seconds_progress;
    Handler discoveryCompletedHandler = new Handler(Looper.myLooper());
    Runnable onDiscCompletedRunnable = () -> endDiscovery(false);
    Thread waitMaxTimeForDiscovery;
    boolean discoveryRunsCurrently=false;

    @Override
    protected int getResIdForActionBarText() {
        return R.string.bt_action_text;
    }

    Runnable progressIndicRunnable = new Runnable() {
        @Override
        public void run() {
            while (discovProgressBar.getProgress() < _max_seconds_progress) {
                try {
                    Thread.sleep(1000);
                    discovProgressBar.incrementProgressBy(1);
                }
                catch(InterruptedException ie) {
                    System.out.println("aaa exception");
                }
            }
            discoveryCompletedHandler.post(onDiscCompletedRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoth_config);

        Button beginDisc = this.findViewById(R.id.discovery_btn);
        Button cancelDisc = this.findViewById(R.id.cancel_disc_btn);
        Button stopDisc = this.findViewById(R.id.stop_disc_btn);
        Button showPairedBtn = this.findViewById(R.id.paired_btn);
        discovProgressBar = this.findViewById(R.id.progress_discovery);
        _max_seconds_progress = getResources().getInteger(R.integer.progress_seconds_max);

        beginDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothConfigActivity.this.BeginDiscovery();
            }
        });

        View.OnClickListener endListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BluetoothConfigActivity) v.getContext()).endDiscovery(v.equals(cancelDisc));
            }
        };
        stopDisc.setOnClickListener(endListener);
        cancelDisc.setOnClickListener(endListener);

        BaseAdapter adapter1 = new PairedDevicesCheckboxCustomAdapter<>(this, R.layout.custom_list_item_checkbox,
                BtUtils.getListOfPaired(), BtUtils.getTargetDeviceIndex());
        ((ListView)this.findViewById(R.id.list_paired)).setAdapter(adapter1);

        showPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtUtils.RefreshPairedDevices(v.getContext());
                adapter1.notifyDataSetChanged();
            }
        });

        ListView lv2 = this.findViewById(R.id.list_visible);
        BaseAdapter adapter2 = new VisibleDevicesButtonCustomAdapter<>(this, R.layout.custom_list_item_button, currentVisibleList);
        lv2.setAdapter(adapter2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(discoveryRunsCurrently) {
            endDiscovery(true);
        }
    }

    public void OnVisibleDeviceDiscovered(BluetoothDevice device) {
        if(currentVisibleList.contains(device)) {return;}

        currentVisibleList.add(device);

        ListView lv = this.findViewById(R.id.list_visible);
        ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
    }
    private void toggleDiscoveryGroupVisibility(boolean discoveryRunning) {
        this.findViewById(R.id.progress_discovery).setVisibility(discoveryRunning? View.VISIBLE : View.INVISIBLE);
        this.findViewById(R.id.discovery_btn).setVisibility(discoveryRunning? View.INVISIBLE : View.VISIBLE);

        this.findViewById(R.id.discovery_btn).setEnabled(!discoveryRunning);
        this.findViewById(R.id.cancel_disc_btn).setEnabled(discoveryRunning);
        this.findViewById(R.id.stop_disc_btn).setEnabled(discoveryRunning);
    }

    private void BeginDiscovery() {
        discoveryRunsCurrently = true;

        if(oldVisibleList == null) {
            oldVisibleList = new LinkedList<>();
        }
        else {
            oldVisibleList.clear();
        }
        oldVisibleList.addAll(currentVisibleList);
        currentVisibleList.clear();

        ((BaseAdapter)((ListView)this.findViewById(R.id.list_visible)).getAdapter()).notifyDataSetChanged();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(_receiver, filter);

        BtUtils.BeginBluetoothDiscovery(this);

        if(_scanner==null) {
            _scanner=BtUtils.getScanner(this);
        }
        _scanner.startScan(callback);

        toggleDiscoveryGroupVisibility(true);
        waitMaxTimeForDiscovery = new Thread(progressIndicRunnable);
        waitMaxTimeForDiscovery.start();
    }
    private void endDiscovery(boolean cancelProgress) {
        discoveryRunsCurrently = false;

        if(cancelProgress) {
            currentVisibleList.clear();
            currentVisibleList.addAll(oldVisibleList);
            oldVisibleList = null;

            ListView lv = this.findViewById(R.id.list_visible);
            currentVisibleList.clear();
            ((BaseAdapter)lv.getAdapter()).notifyDataSetChanged();
        }
        waitMaxTimeForDiscovery.interrupt();;
        discovProgressBar.setProgress(0);
        toggleDiscoveryGroupVisibility(false);

        _scanner.stopScan(callback);
        BtUtils.CancelBluetoothDiscovery(this);
        try {
            unregisterReceiver(_receiver);
        }
        catch (IllegalArgumentException e) {}
    }

    @Override
    public void OnBluetoothStatusChanged(boolean connected) {
        if(!connected && discoveryRunsCurrently) {
            endDiscovery(false);
        }
        Button pairedBtn = this.findViewById(R.id.paired_btn),
                discoveryBtn = this.findViewById(R.id.discovery_btn);
        pairedBtn.setEnabled(connected);
        discoveryBtn.setEnabled(connected);

        ListView lv1 = this.findViewById(R.id.list_visible);
        int childrencount = lv1.getChildCount();
        for(int i=0; i<childrencount; i++) {
            View pairBtn = lv1.getChildAt(i).findViewById(R.id.pair_request_btn);
            pairBtn.setEnabled(connected);
        }
    }
}