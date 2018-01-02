package com.ocrsdk;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ResultsActivity extends Activity {

    String outputPath;
    TextView tv;

    private int RECEIPT_FOOD_LIST_REQUEST = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = new TextView(this);
        setContentView(tv);

        String imageUrl = "unknown";

        Bundle extras = getIntent().getExtras();
        if( extras != null) {
            imageUrl = extras.getString("IMAGE_PATH" );
            outputPath = extras.getString( "RESULT_PATH" );
        }

        // Starting recognition process
        new AsyncProcessTask(this).execute(imageUrl, outputPath);
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally {
                fis.close();
            }

            displayMessage(contents.toString());

        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    public void displayMessage( String text )
    {
        tv.post( new MessagePoster( text ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    public void returnResults(Boolean success) {
        if (!success)
        return;
        try {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);

            ArrayList<String> receiptFoods = new ArrayList<String>();
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    //contents.append(text).append(System.getProperty("line.separator"));
                    receiptFoods.add(text.substring(0, text.indexOf(' ')));
                }
            } finally {
                fis.close();
            }

            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("receiptFoods", receiptFoods);
            setResult(RECEIPT_FOOD_LIST_REQUEST, returnIntent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MessagePoster implements Runnable {
        public MessagePoster( String message )
        {
            _message = message;
        }

        public void run() {
            tv.append( _message + "\n" );
            setContentView( tv );
        }

        private final String _message;
    }
}
