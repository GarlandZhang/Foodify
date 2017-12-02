package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by GZhang on 2017-12-02.
 */

public class MainMenu extends Activity {

    String headerName;
    String expiryDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        Intent intent = getIntent();
        headerName = intent.getStringExtra("HeaderName");
        expiryDate = intent.getStringExtra("expiryDate1");
    }
}
