package com.developer.myapplication.util;

import java.util.UUID;

public class Const {
    public static final String APP_NAME = "Talk Anywhere";

    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;
    public static final int STATE_MESSAGE_RECEIVED = 5;

    public static final UUID DEVICE_UUID = UUID.fromString("cbbfe0e1-f7f3-4206-84e0-84cbb3d09dfc");
}
