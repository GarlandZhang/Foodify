package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by GZhang on 2017-12-02.
 */

public class MainMenu extends Activity {

    ListView foodItemsListView;
    /*
    ListView expiredFoodListView,
    expirationDeadlineListView;

/*    ArrayList<String> foodNames;
    ArrayList<String> expiryDates;
    ArrayList<String> expirationDeadlines;
*/

    ArrayList<FoodItem> foods;

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

        foodItemsListView = findViewById(R.id.foodItemsListView);
/*
        expiredFoodListView = (ListView) findViewById(R.id.expiredFoodListView);
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
        expirationDeadlineListView.setAdapter(deadlineAdapter);*/

        Intent thisIntent = getIntent();

        if(thisIntent.getParcelableArrayListExtra("foods") != null){
           foods = thisIntent.getParcelableArrayListExtra("foods");

           FoodAdapter foodAdapter = new FoodAdapter(this, foods);
           foodItemsListView.setAdapter( foodAdapter );
        }
    }

    public void deleteRow(View view) {

    }
}
