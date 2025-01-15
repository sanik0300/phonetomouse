package com.sanikshomemade.phonetomouse.gesturedetection;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.sanikshomemade.phonetomouse.activities.TouchscreenActivity;

public class MyTouchListener implements View.OnTouchListener {

    TouchscreenActivity.MyGestureDetector gestureDetector;
    private float lastTouchEventX=-1, lastTouchEventY=-1;
    public float GetLastTouchEventX() { return lastTouchEventX; }
    public float GetLastTouchEventY() { return lastTouchEventY; }
    public MyTouchListener(GestureDetector detector) {
        gestureDetector = (TouchscreenActivity.MyGestureDetector)detector;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int actionMasked = event.getActionMasked();
        if (actionMasked < 0 || actionMasked > 2) {
            return true;
        }
        lastTouchEventX = event.getX();
        lastTouchEventY = event.getY();
        if (PseudoScreen.class.isInstance(v)) {
            ((PseudoScreen) v).updateView();
        }

        if(actionMasked == MotionEvent.ACTION_UP) {
            //System.out.println("Action Up Caught");
            if(gestureDetector.getListener().getLongPress()) {
                //System.out.println("sending Mouse Up");
                gestureDetector.getListener().OnUpAfterScrollPressed(event.getX(), event.getY());
            }
        }

        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        if (actionMasked == MotionEvent.ACTION_MOVE && gestureDetector.getListener().getScrollBlocked()) {
            gestureDetector.getListener().unblockScroll();
            MotionEvent cancel = MotionEvent.obtain(event);
            cancel.setAction(MotionEvent.ACTION_CANCEL);
            gestureDetector.onTouchEvent(cancel);
            System.out.println("cancel!");
        }
        return true;
    }
}
