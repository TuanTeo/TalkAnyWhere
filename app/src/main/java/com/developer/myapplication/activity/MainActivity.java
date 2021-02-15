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
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
import com.developer.myapplication.listener.IStartServiceListener;
import com.developer.myapplication.object.AudioCall;
import com.developer.myapplication.service.ClientClass;
import com.developer.myapplication.service.ServerClass;
import com.developer.myapplication.util.Const;
import com.developer.myapplication.util.LogUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements IMainViewListener, IDeviceItemListener, IStartServiceListener {

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_GRANT_PERMISSON = 200;

    private Toolbar mToolbar;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    private ListDeviceAdapter mListDeviceAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART) {
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
    private byte[] readBuff;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Const.STATE_LISTENING:
                    Toast.makeText(MainActivity.this, "Listening", Toast.LENGTH_SHORT).show();
                    break;
                case Const.STATE_CONNECTING:
                    Toast.makeText(MainActivity.this, "Connecting", Toast.LENGTH_SHORT).show();
                    break;
                case Const.STATE_CONNECTED:
                    break;
                case Const.STATE_CONNECTION_FAILED:
                    Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    break;
                case Const.STATE_MESSAGE_RECEIVED:
                    if(mAudioTrack != null){
                        readBuff = (byte[]) msg.obj;
                    }
                    dataReceived(true);
                    break;
            }
            return true;
        }
    });

    private boolean mReceived = false;
    private synchronized void dataReceived(boolean b) {
        mReceived = b;
    }
    private boolean isReceivedData(){
        return mReceived;
    }

    private AudioTrack mAudioTrack;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GRANT_PERMISSON) {
            isGrantRecord = isGrantLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(mToolbar);
    }

    /**
     * Function to check is Bluetooth enabled?
     */
    private boolean isBluetoothEnable(){
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

    @Override
    public void enableScanDevice() {
        /*Start ServerSocket when enable scan mode*/
        ServerClass serverClass = new ServerClass(mHandler, this);
        serverClass.start();

        /*Enable Scan mode*/
        Intent discoverableItent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableItent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(discoverableItent);
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

    @Override
    public void connectSocketBluetooth(BluetoothDevice device) {
        /*Connect to Socket server*/
        ClientClass clientClass = new ClientClass(device, mHandler, this);
        clientClass.start();
    }

    @Override
    public void startSendReceiveService(BluetoothSocket socket) {
        Thread receiveThread = new Thread(new Runnable() {

            @Override
            public void run() {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AudioCall.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, AudioCall.BUF_SIZE, AudioTrack.MODE_STREAM);
                mAudioTrack.play();
                LogUtils.showLog("AudioTrack is play");
                while (true) {
                    if(isReceivedData()) {
                        mAudioTrack.write(getReadBuff(), 0, AudioCall.BUF_SIZE);
                        dataReceived(false);
                    }
                }
            }
        });
        receiveThread.start();
    }

    private synchronized byte[] getReadBuff(){
        return readBuff;
    }
}