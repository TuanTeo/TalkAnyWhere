package com.developer.myapplication.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.myapplication.R;
import com.developer.myapplication.object.DeviceItem;

import java.util.ArrayList;

public class ListDeviceFragment extends Fragment {

    private ListView mListDeviceRecyclerView;
    private ArrayList<DeviceItem> mDeviceList = new ArrayList<DeviceItem>();
    private BluetoothAdapter mBluetoothAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_device, container, false);
        mListDeviceRecyclerView = view.findViewById(R.id.recycle_list_divice);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), false);
                // Add it to our adapter
                mDeviceList.add(newDevice);
            }
        }
    };
}
