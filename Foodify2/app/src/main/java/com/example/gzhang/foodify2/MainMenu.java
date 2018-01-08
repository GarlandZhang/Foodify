package com.example.gzhang.foodify2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by GZhang on 2017-12-02.
 */

public class MainMenu extends Activity {

    ListView foodItemsListView;
    FoodAdapter foodAdapter;

    Button backButton;

    ArrayList<FoodItem> foods;

    int CURRENT_FOOD_LIST_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        foodItemsListView = findViewById(R.id.foodItemsListView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: when I implement delete button, i need to pass back updated list
                finish();
            }
        });

        Intent thisIntent = getIntent();

        if(thisIntent.getParcelableArrayListExtra("foods") != null){
           foods = thisIntent.getParcelableArrayListExtra("foods");

           foodAdapter = new FoodAdapter(this, foods);
           foodItemsListView.setAdapter( foodAdapter );
        }
    }

    public void deleteRow(View view) {

        System.out.println("PRESSED");
    }
}
