package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GZhang on 2017-12-02.
 */

public class CameraFoodOptions extends Activity {

    ListView optionsListView;
    ArrayList<String> optionsList;

    int FOOD_OPTIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        Bundle extra = getIntent().getBundleExtra("extra");
        optionsList = (ArrayList<String>)extra.getSerializable("FoodOptions");

        optionsListView = (ListView)findViewById(R.id.optionsListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, optionsList);
        optionsListView.setAdapter(adapter);
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View itemView, int itemPosition, long itemId)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("FoodName", listView.getItemAtPosition(itemPosition).toString());
                setResult(FOOD_OPTIONS_REQUEST, returnIntent);
                finish();
            }
        });
    }

}
