package com.developer.myapplication.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.developer.myapplication.listener.IStartServiceListener;
import com.developer.myapplication.object.AudioCall;
import com.developer.myapplication.util.Const;
import com.developer.myapplication.util.LogUtils;

import java.io.IOException;

public class ClientClass extends Thread{

    private BluetoothSocket mSocket;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler;
    private IStartServiceListener mIStartService;
    private SendReceiveService mSendReceiveService;

    public ClientClass(BluetoothDevice device, Handler handler, IStartServiceListener startServiceListener){
        mIStartService = startServiceListener;
        mHandler = handler;
        try {
            LogUtils.showLog("Client constructor");
            mSocket = device.createInsecureRfcommSocketToServiceRecord(Const.DEVICE_UUID);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        LogUtils.showLog("Client run");
        try{
            mSocket.connect();
            Message message = Message.obtain();
            message.what = Const.STATE_CONNECTED;
            LogUtils.showLog("Client run accept");
            mHandler.sendMessage(message);

            mIStartService.startSendReceiveService(mSocket);
            mSendReceiveService = new SendReceiveService(mSocket, mHandler);
            mSendReceiveService.start();
            AudioCall call = new AudioCall(mSendReceiveService);
            call.startMic();
        }catch (IOException e){
            e.printStackTrace();
            Message message = Message.obtain();
            LogUtils.showLog("Client run false");
            message.what = Const.STATE_CONNECTION_FAILED;
        }

    }
}
