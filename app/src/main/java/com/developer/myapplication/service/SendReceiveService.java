package com.developer.myapplication.service;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.developer.myapplication.util.Const;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SendReceiveService extends Thread{
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Handler mHandler;

    public SendReceiveService(BluetoothSocket socket, Handler handler){
        mHandler = handler;
        mBluetoothSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = socket.getInputStream();
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInputStream = tempIn;
        mOutputStream = tempOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1600];
        int bytes;
        while (true) {
            try {
                bytes = mInputStream.read(buffer);
                mHandler.obtainMessage(Const.STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
