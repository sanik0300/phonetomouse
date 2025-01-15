package com.sanikshomemade.phonetomouse.gesturedetection;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class GoToActivityListener implements View.OnClickListener {
    Class<?> typeOfActivityToStart;
    Activity activThatStartsOther;

    public  GoToActivityListener(Activity act1, Class<?> act2Type) {
        activThatStartsOther = act1;
        typeOfActivityToStart = act2Type;
    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent(activThatStartsOther, typeOfActivityToStart);
        activThatStartsOther.startActivity(it);
    }
}
