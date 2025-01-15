package com.sanikshomemade.phonetomouse.bluetoothclasses;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.res.TypedArrayUtils;
import com.sanikshomemade.phonetomouse.MouseFakingManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public abstract class BluetoothListeningRunnable implements Runnable {

    protected BluetoothSocket socket;
    Handler uiHandler;
    byte[] _readBuffer = new byte[9]; //sizeof(int)*2 + one for command enum code

    public BluetoothListeningRunnable(Handler uiHandler)
    {
        this.uiHandler = uiHandler;
    }

    @Override
    public void run() {
        InputStream inputStream= null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.println("Error when creating input stream");
            return;
        }
        Message msg;
        while (true) {
            try {
                inputStream.read(_readBuffer);
                switch (MouseFakingManager.Commands.fromOrdinal((int)_readBuffer[0])) {
                    case Disconnect:
                        msg = uiHandler.obtainMessage();
                        msg.what = _readBuffer[0];
                        uiHandler.sendMessage(msg);
                        break;
                    case Ratio:
                        msg = uiHandler.obtainMessage();
                        msg.what = _readBuffer[0];

                        byte[] ratioNumbersData = new byte[8];
                        System.arraycopy(_readBuffer, 1, ratioNumbersData, 0, ratioNumbersData.length);
                        IntBuffer intBuf = ByteBuffer.wrap(ratioNumbersData).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
                        int[] arr = new int[intBuf.remaining()];
                        intBuf.get(arr);
                        msg.obj = arr;
                        uiHandler.sendMessage(msg);
                        continue;
                        //break; (unreachable)
                    default:
                        continue;
                        //break; (unreachable)
                }

                System.out.println("feedback-listening-thread ended");
                Thread.currentThread().interrupt();
                break;

            }
            catch (IOException e) { continue; }
        }
    }
}