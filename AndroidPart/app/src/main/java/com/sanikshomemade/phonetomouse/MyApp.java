package com.sanikshomemade.phonetomouse;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BtUtils;

import java.util.Locale;

public class MyApp extends Application {
    static public BluetoothDependentCompatActivity currentBtDependentActivity = null;
    @Override
    protected void attachBaseContext(Context base) {
        String langcode = PrefUtils.GetLanguageCodeToOverrideTo(base);
        if(langcode.isEmpty()) {
            super.attachBaseContext(base);
            return;
        }
        super.attachBaseContext(PrefUtils.GetOverriddenLanguageContext(langcode, base));
    }

    public static abstract class MyMultilangCompatActivity extends AppCompatActivity {

        protected int getResIdForActionBarText() { return -1;}

        @Override
        protected void attachBaseContext(Context newBase) {
            String langcode = PrefUtils.GetLanguageCodeToOverrideTo(newBase);
            if(langcode.isEmpty()) {
                super.attachBaseContext(newBase);
                return;
            }
            super.attachBaseContext(PrefUtils.GetOverriddenLanguageContext(langcode, newBase));
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ActionBar ab = getSupportActionBar();
            if(ab==null) { return; }
            ab.setTitle(this.getResIdForActionBarText());
        }

        static public Locale getLocaleFromRes(Resources res) {
            if(Build.VERSION.SDK_INT >= 24) {
                return res.getConfiguration().getLocales().get(0);
            }
            return res.getConfiguration().locale;
        }
    }




    public static abstract class BluetoothDependentCompatActivity extends MyMultilangCompatActivity {
        public abstract void OnBluetoothStatusChanged(boolean connected);

        @Override
        protected void onResume() {
            super.onResume();
            MyApp.currentBtDependentActivity = this;
            if(!BtUtils.IsBluetoothEnabled(this)) {
                OnBluetoothStatusChanged(false);
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            MyApp.currentBtDependentActivity = null;
        }
    }

}
