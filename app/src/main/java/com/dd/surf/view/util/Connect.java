package com.dd.surf.view.util;

import android.app.Application;

public class Connect extends Application {

    public String serverAddress;
    public int port = 2077;

    private boolean isConnect = false;

    public boolean isConnect() {
        return isConnect;
    }

    public boolean connectServer(){
        return false;
    }
}
