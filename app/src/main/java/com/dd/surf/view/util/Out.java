package com.dd.surf.view.util;

import android.app.Activity;
import android.widget.Toast;

public class Out {
    public static void print(Activity activity, String msg){
        Toast.makeText(activity,msg,Toast.LENGTH_LONG).show();
    }

}
