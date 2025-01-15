package com.sanikshomemade.phonetomouse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class PrefUtils {

    private static SharedPreferences getPrefsFile(Context ct) {
        String prefFileName = ct.getResources().getString(R.string.prefs_file);
        return ct.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    static public void SaveDeviceInfo(Context ct, String deviceName, String macValue)
    {
        SharedPreferences.Editor editor = getPrefsFile(ct).edit();
        editor.putString(ct.getResources().getString(R.string.last_paired_device_name), deviceName);
        editor.putString(ct.getResources().getString(R.string.last_paired_mac), macValue);
        editor.apply();
    }

    static public <T> T getValueFromPrefs(Context ct, int keyStringResId, T defValue)
    {
        String key = ct.getResources().getString(keyStringResId);
        if(defValue instanceof String) {
            return (T)getPrefsFile(ct).getString(key, "");
        }
        else if(defValue instanceof Integer) {
            return (T)((Integer)getPrefsFile(ct).getInt(key, (int)defValue));
        }
        else { //bool
            return (T)((Boolean)getPrefsFile(ct).getBoolean(key, (boolean)defValue));
        }
    }

    static public SharedPreferences.Editor getEditor(Context ct) {
        return getPrefsFile(ct).edit();
    }

    static public String GetLanguageCodeToOverrideTo(Context app) {
        int langIndexFromPrefs = getValueFromPrefs(app, R.string.preferred_language, -1);
        if(langIndexFromPrefs <0) { return  ""; }


        String crtContextLang = MyApp.MyMultilangCompatActivity.getLocaleFromRes(app.getResources()).getLanguage();
        String langCodePrefs = app.getResources().getStringArray(R.array.languages)[langIndexFromPrefs].toLowerCase();

        return crtContextLang.equals(langCodePrefs)? "" : langCodePrefs;
    }

    static public Context GetOverriddenLanguageContext(String lang, Context context) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);

        Configuration conf = context.getResources().getConfiguration();
        conf.setLocale(myLocale);

        return context.createConfigurationContext(conf);
    }
}
