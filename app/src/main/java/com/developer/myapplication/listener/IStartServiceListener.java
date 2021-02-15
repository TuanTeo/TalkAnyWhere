package com.developer.myapplication.listener;

import android.bluetooth.BluetoothSocket;

public interface IStartServiceListener {
    void startSendReceiveService(BluetoothSocket socket);
}
