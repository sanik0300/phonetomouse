package com.sanikshomemade.phonetomouse.activities;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;
import com.sanikshomemade.phonetomouse.MouseFakingManager;
import com.sanikshomemade.phonetomouse.R;
import com.sanikshomemade.phonetomouse.bluetoothclasses.BtUtils;
import com.sanikshomemade.phonetomouse.gesturedetection.PseudoScreen;

public class TouchscreenHandler extends Handler {
    TouchscreenActivity ownerAct;
    public TouchscreenHandler(Looper looper, TouchscreenActivity ownerAct) {
        super(looper);
        this.ownerAct = ownerAct;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (MouseFakingManager.Commands.fromOrdinal(msg.what)) {
            case Disconnect:
                BtUtils.Disconnect(ownerAct);
                ownerAct.setConnectionRequested(false);
                ownerAct.showSingleTextOnScreen(R.string.screen_you_can_connect);
                ImageButton btn = ownerAct.findViewById(R.id.stop_start_button);
                btn.setImageResource(R.drawable.play);
                break;
            case Ratio:
                int[] screenParams = (int[])msg.obj;
                double passedRatio = (double)screenParams[0]/screenParams[1];
                PseudoScreen ps = ownerAct.findViewById(R.id.pseudo_screen);

                double viewW = ps.getWidth(), viewH = ps.getHeight(), viewRatio = viewW/viewH;

                ViewGroup.LayoutParams params = ps.getLayoutParams();
                if(viewRatio > passedRatio) {
                    //view is more stretched horizontally than the screen, decrease the view width
                    params.width = (int)(viewH*passedRatio);
                }
                else {
                    //view is too narrow to fit screen, decrease the view height
                    params.height = (int)(viewW/passedRatio);
                }
                ps.setLayoutParams(params);
                ps.requestLayout();
                break;
            case Down:
            case Up:
                boolean btnDownIsLeft = (boolean) msg.obj;
                ownerAct.IndicateSelectedPretendedButtonState(btnDownIsLeft,
                                                        msg.what == MouseFakingManager.Commands.Down.ordinal());
                break;
        }
    }
}
