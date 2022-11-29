package com.dd.surf.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.surf.R;

public class SettingButton extends RelativeLayout {

    private String text;
    private int icon;
    TextView settingText;
    ImageView settingImage;


    public SettingButton(Context context) {
        super(context);
    }

    public SettingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingButton);
        text = ta.getString(R.styleable.SettingButton_text);
        icon = ta.getResourceId(R.styleable.SettingButton_icon,0);
        ta.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_setting_button, this, true);
        settingText = findViewById(R.id.setting_text);
        settingImage = findViewById(R.id.setting_image);
        settingText.setText(text);
        settingImage.setImageResource(icon);
    }

    public SettingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SettingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
