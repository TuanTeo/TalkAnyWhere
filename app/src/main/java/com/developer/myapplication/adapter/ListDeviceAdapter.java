package com.developer.myapplication.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.myapplication.R;
import com.developer.myapplication.listener.IDeviceItemListener;
import com.developer.myapplication.object.DeviceItem;

import java.util.ArrayList;

public class ListDeviceAdapter extends RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<BluetoothDevice> mListDevice;
    private IDeviceItemListener mIDeviceItemListener;

    public ListDeviceAdapter(Context context, ArrayList<BluetoothDevice> listDevices, IDeviceItemListener deviceItemListener){
        mContext = context;
        mListDevice = listDevices;
        mIDeviceItemListener = deviceItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.find_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String deviceName = mListDevice.get(position).getName();
        String deviceAddress = mListDevice.get(position).getAddress();
        holder.getDeviceName().setText(deviceName + "\n" + deviceAddress);
    }

    @Override
    public int getItemCount() {
        return mListDevice.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDeviceName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDeviceName = itemView.findViewById(R.id.text_view_device_name);
            mDeviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIDeviceItemListener.connectSocketBluetooth(mListDevice.get(getAdapterPosition()));
                }
            });
        }

        public TextView getDeviceName() {
            return mDeviceName;
        }
    }
}
