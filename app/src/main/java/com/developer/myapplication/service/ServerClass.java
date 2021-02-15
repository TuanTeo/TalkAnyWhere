package com.developer.myapplication.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.developer.myapplication.listener.IStartServiceListener;
import com.developer.myapplication.object.AudioCall;
import com.developer.myapplication.util.Const;
import com.developer.myapplication.util.LogUtils;

import java.io.IOException;

public class ServerClass extends Thread{
    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler;
    private IStartServiceListener mIStartService;
    private SendReceiveService mSendReceiveService;

    public ServerClass(Handler handler, IStartServiceListener startServiceListener){
        mIStartService = startServiceListener;
        mHandler = handler;
        try {
            LogUtils.showLog("Server constructor");
            serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(Const.APP_NAME, Const.DEVICE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        BluetoothSocket socket = null;
        LogUtils.showLog("Server run");

        while(socket == null){
            try{
                Message message = Message.obtain();
                message.what = Const.STATE_CONNECTING;
                socket = serverSocket.accept();
                LogUtils.showLog("Server run accept");
            }catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = Const.STATE_CONNECTION_FAILED;
                mHandler.sendMessage(message);
                LogUtils.showLog("Server run false");
            }
            if (socket != null){
                Message message = Message.obtain();
                message.what = Const.STATE_CONNECTED;
                LogUtils.showLog("Server run connected");
                mHandler.sendMessage(message);

                mIStartService.startSendReceiveService(socket);
                mSendReceiveService = new SendReceiveService(socket, mHandler);
                mSendReceiveService.start();
                AudioCall call = new AudioCall(mSendReceiveService);
                call.startMic();
            }
        }
    }
}
