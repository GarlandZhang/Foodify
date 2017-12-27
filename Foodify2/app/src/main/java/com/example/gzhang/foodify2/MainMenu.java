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

        expiredFoodListView = (ListView)findViewById(R.id.expiredFoodListView);
        expiredDateListView = (ListView)findViewById(R.id.expiredDateListView);
        expirationDeadlineListView = (ListView) findViewById(R.id.expirationDeadlineListView);

        Bundle extra = getIntent().getBundleExtra("extra");
        foodNames = (ArrayList<String>)extra.getSerializable("FoodNames");
        expiryDates = (ArrayList<String>)extra.getSerializable("ExpiryDates");
        expirationDeadlines = (ArrayList<String>)extra.getSerializable("ExpirationDeadlines");

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, foodNames);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, expiryDates);

        ArrayAdapter<String> deadlineAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, expirationDeadlines);

        expiredFoodListView.setAdapter( nameAdapter );
        expiredDateListView.setAdapter( dateAdapter );
        expirationDeadlineListView.setAdapter( deadlineAdapter );

       /* System.out.println( almostExpired() );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Intent resultIntent = new Intent(this, MainMenu.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

       scheduleNotification( this, 3000, 0);
       System.out.println("DONE");
    }



    private boolean almostExpired(){

        return (getExpiryDate(expiryDate).getTime() - today.getTime()) / 3600 <= 1;
    }


    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle( "My title")
                .setContentText( "Almost Expired!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.notification_icon)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, MainMenu.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);*/
    }
/*
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        Bundle extra = new Bundle();
        extra.putSerializable("FoodNames", foodNames);
        extra.putSerializable("ExpiryDates", expiryDates);
        extra.putSerializable("ExpirationDeadlines",expirationDeadlines);
        returnIntent.putExtra( "extra", extra);
        setResult(CURRENT_FOOD_LIST_REQUEST, returnIntent);
        finish();
    }*/
}
