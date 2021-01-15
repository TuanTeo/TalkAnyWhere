package com.developer.myapplication.object;

/**
 * Bluetooth Device Model
 */
public class DeviceItem {
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mIsPaired;

    public DeviceItem(String deviceName, String deviceAddress, boolean isPaired) {
        this.mDeviceName = deviceName;
        this.mDeviceAddress = deviceAddress;
        this.mIsPaired = isPaired;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public void setDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    public boolean isIsPaired() {
        return mIsPaired;
    }

    public void setIsPaired(boolean mIsPaired) {
        this.mIsPaired = mIsPaired;
    }
}
