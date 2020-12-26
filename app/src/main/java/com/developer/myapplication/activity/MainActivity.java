package com.developer.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.developer.myapplication.R;
import com.developer.myapplication.fragment.ListDeviceFragment;
import com.developer.myapplication.fragment.MainViewFragment;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 100;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        initMainView();
    }

    private void initMainView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_context, new MainViewFragment())
                .addToBackStack(null)
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


}