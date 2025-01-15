package com.sanikshomemade.phonetomouse;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.HapticFeedbackConstants;
import android.view.View;

public class EffectsUtils {
    static private Vibrator _vbtr;
    static private Vibrator getVibrator(Context context) {
        if(_vbtr!=null) { return _vbtr; }

        if(Build.VERSION.SDK_INT >= 31) {
            _vbtr = ((VibratorManager)context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)).getDefaultVibrator();
        }
        else {
            _vbtr = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return _vbtr;
    }

    public static void VibrateOnLongPress(Activity context) {
        if(Build.VERSION.SDK_INT >= 26) {
            VibrationEffect effect = Build.VERSION.SDK_INT >=29?
                                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                                        : VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
            getVibrator(context).cancel();
            _vbtr.vibrate(effect);
        }
        else {
            View view = context.findViewById(android.R.id.content);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
    }

    public static void PlayConnectionChangedSound(Context ct, boolean conn)
    {
        MediaPlayer.create(ct, conn? R.raw.connect_sound : R.raw.disconnect_sound).start();
    }
}
