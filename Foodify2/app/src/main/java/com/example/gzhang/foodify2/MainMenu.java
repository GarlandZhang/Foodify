package com.example.gzhang.foodify2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GZhang on 2017-12-02.
 */

public class MainMenu extends Activity {

    ListView expiredFoodListView,
    expiredDateListView,
    expirationDeadlineListView;

    String expiryDateItem;
    String headerName;
    String expiryDate;
    Date today;
    ArrayList<String> foodNames;
    ArrayList<String> expiryDates;
    ArrayList<String> expirationDeadlines;

    int CURRENT_FOOD_LIST_REQUEST = 3;

/*
    public class ExpiredFoods{
        String foodName;
        Date foodExpiryDate;

        public ExpiredFoods(String name, Date expiryDate){
            foodName = name;
            foodExpiryDate = expiryDate;
        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        expiredFoodListView = (ListView) findViewById(R.id.expiredFoodListView);
        expiredDateListView = (ListView) findViewById(R.id.expiredDateListView);
        expirationDeadlineListView = (ListView) findViewById(R.id.expirationDeadlineListView);

        Bundle extra = getIntent().getBundleExtra("extra");
        foodNames = (ArrayList<String>) extra.getSerializable("FoodNames");
        expiryDates = (ArrayList<String>) extra.getSerializable("ExpiryDates");
        expirationDeadlines = (ArrayList<String>) extra.getSerializable("ExpirationDeadlines");

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, foodNames);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, expiryDates);

        ArrayAdapter<String> deadlineAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, expirationDeadlines);

        expiredFoodListView.setAdapter(nameAdapter);
        expiredDateListView.setAdapter(dateAdapter);
        expirationDeadlineListView.setAdapter(deadlineAdapter);
    }
}
