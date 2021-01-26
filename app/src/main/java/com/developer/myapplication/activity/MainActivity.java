package com.developer.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.developer.myapplication.R;
import com.developer.myapplication.adapter.ListDeviceAdapter;
import com.developer.myapplication.fragment.ListDeviceFragment;
import com.developer.myapplication.fragment.MainViewFragment;
import com.developer.myapplication.listener.IDeviceItemListener;
import com.developer.myapplication.listener.IMainViewListener;
import com.developer.myapplication.object.DeviceItem;
import com.developer.myapplication.service.ClientClass;
import com.developer.myapplication.service.ServerClass;
import com.developer.myapplication.util.Const;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IMainViewListener, IDeviceItemListener {

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_GRANT_PERMISSON = 200;

    private Toolbar mToolbar;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;
    private ListDeviceAdapter mListDeviceAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
//                    DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), false);
                    // Add it to our adapter
                    mDeviceList.add(device);
                    mListDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    private String[] mPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.RECORD_AUDIO};

    private boolean isGrantLocation = false;
    private boolean isGrantRecord = false;
    private OutputStream outputStream = null;
    private InputStream inStream = null;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.STATE_LISTENING:
                    break;
                case Const.STATE_CONNECTING:
                    break;
                case Const.STATE_CONNECTED:
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    break;
                case Const.STATE_CONNECTION_FAILED:
                    break;
                case Const.STATE_MESSAGE_RECEIVED:
                    break;

            }
            return true;
        }
    });

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
    public void startTalkService() {
        //Check other android bluetooth device is paired
        if(isBluetoothEnable()){
            try {
                init();
            } catch (IOException e){

            }
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
        mDeviceList.clear();
        mListDeviceAdapter = new ListDeviceAdapter(this, mDeviceList, this);
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

    private void init() throws IOException {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

                if(bondedDevices.size() > 0 && checkAndroidDevice(bondedDevices)) {
                    Toast.makeText(this, "Android is Paired", Toast.LENGTH_SHORT).show();
//                    Object[] devices = (Object []) bondedDevices.toArray();
//                    BluetoothDevice device = (BluetoothDevice) devices[0];
//                    ParcelUuid[] uuids = device.getUuids();
//                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
//                    socket.connect();
//                    outputStream = socket.getOutputStream();
//                    inStream = socket.getInputStream();
                } else {
                    Toast.makeText(this, "Khong co ket noi nao. Go to setting and connect!", Toast.LENGTH_SHORT).show();
//                    goToBluetoothSetting();
                }
            }
        }
    }

    private void goToBluetoothSetting() {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    private boolean checkAndroidDevice(Set<BluetoothDevice> bondedDevices){
        for(BluetoothDevice bluetoothDevice: bondedDevices){
            int deviceClass = bluetoothDevice.getBluetoothClass().getDeviceClass();
            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING && deviceClass == BluetoothClass.Device.PHONE_SMART){
                //TODO: Co the giu lai bien BluetoothDevice o day
                return true;
            }
        }
        return false;
    }

    @Override
    public void connectSocketBluetooth(BluetoothDevice device) {
        //TODO ket noi 2 thiet bi voi nhau
        ServerClass serverClass = new ServerClass(mHandler);
        serverClass.start();
        ClientClass clientClass = new ClientClass(device, mHandler);
        clientClass.start();
    }
}