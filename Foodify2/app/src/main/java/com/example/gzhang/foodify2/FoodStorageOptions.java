package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GZhang on 2017-12-02.
 */

public class FoodStorageOptions extends Activity {

    ListView foodStorageListView;
    TextView titleTextView;
    ArrayList<String> foodStorageList,
                      expiryDateList;

    int STORAGE_OPTIONS_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        titleTextView = (TextView)findViewById(R.id.titleTextView);
        titleTextView.setText("Storage Options");

        Intent intent = getIntent();
        String foodName = intent.getStringExtra("FoodName");

        Bundle extra = intent.getBundleExtra("extra");
        foodStorageList = (ArrayList<String>)extra.getSerializable("StorageOptions");
        expiryDateList = (ArrayList<String>)extra.getSerializable("ExpiryDates");

        foodStorageListView = (ListView)findViewById(R.id.optionsListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, foodStorageList);
        foodStorageListView.setAdapter(adapter);
        foodStorageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View itemView, int itemPosition, long itemId)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ExpiryDate", expiryDateList.get(itemPosition));
                setResult(STORAGE_OPTIONS_REQUEST, returnIntent);
                finish();
            }
        });
    }

}
