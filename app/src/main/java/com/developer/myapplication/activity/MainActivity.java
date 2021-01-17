package com.developer.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.developer.myapplication.R;
import com.developer.myapplication.adapter.ListDeviceAdapter;
import com.developer.myapplication.fragment.ListDeviceFragment;
import com.developer.myapplication.fragment.MainViewFragment;
import com.developer.myapplication.listener.IMainViewListener;
import com.developer.myapplication.object.DeviceItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainViewListener {

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_GRANT_PERMISSON = 200;

    private Toolbar mToolbar;
    private ArrayList<DeviceItem> mDeviceList = new ArrayList<DeviceItem>();
    private BluetoothAdapter mBluetoothAdapter;
    private ListDeviceAdapter mListDeviceAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), false);
                // Add it to our adapter
                mDeviceList.add(newDevice);
                mListDeviceAdapter.notifyDataSetChanged();
            }
        }
    };
    private String[] mPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO};

    private boolean isGrantLocation = false;
    private boolean isGrantRecord = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_GRANT_PERMISSON:
                isGrantRecord = isGrantLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(!isGrantRecord || !isGrantLocation){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, mPermissions, REQUEST_GRANT_PERMISSON);

        initComponent();
        initMainView();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    private void initMainView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_context, new MainViewFragment(this))
                .commit();
    }

    /**
     * Create start component
     */
    private void initComponent() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(mToolbar);
    }

    /**
     * Function to check is Bluetooth enabled?
     * @return
     */
    public boolean isBluetoothEnable(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            //Action to show a dialog for user enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        } else {
            // Bluetooth is enabled
            return true;
        }
    }

    @Override
    public void connectToDivice() {
        if(isBluetoothEnable()){
            findDivice();
        }
    }

    /**
     * Function to Find Divice by BlueTooth
     */
    private void findDivice() {
        mListDeviceAdapter = new ListDeviceAdapter(this, mDeviceList);
        ListDeviceFragment listDeviceFragment = new ListDeviceFragment(mListDeviceAdapter);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_context, listDeviceFragment)
                .addToBackStack(null)
                .commit();
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}