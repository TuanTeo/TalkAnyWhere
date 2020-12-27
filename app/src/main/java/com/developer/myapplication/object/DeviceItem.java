package com.developer.myapplication.object;

public class DeviceItem {
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mIsPaired;

    public DeviceItem() {
    }

    public DeviceItem(String deviceName, String deviceAddress, boolean isPaired) {
        this.mDeviceName = deviceName;
        this.mDeviceAddress = deviceAddress;
        this.mIsPaired = isPaired;
    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public void setmDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    public boolean ismIsPaired() {
        return mIsPaired;
    }

    public void setmIsPaired(boolean mIsPaired) {
        this.mIsPaired = mIsPaired;
    }
}
