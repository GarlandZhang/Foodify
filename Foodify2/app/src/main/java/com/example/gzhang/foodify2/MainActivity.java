package com.example.gzhang.foodify2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ocrsdk.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;



public class MainActivity extends AppCompatActivity {

    Button mButton,
            camButton,
            currentListButton,
            receiptButton;
    EditText mEdit;
    Header[] headers;
    ListView mListView;

    int CAMERA_PIC_REQUEST = 0,
        FOOD_OPTIONS_REQUEST = 1,
        STORAGE_OPTIONS_REQUEST = 2,
        CURRENT_FOOD_LIST_REQUEST = 3,
        SELECT_PICTURE = 4;
/*
    ArrayList<String> foodNames,
                      expiryDates,
                      expirationDeadlines;
  */
    ArrayList<FoodItem> foods;

    public class Header{

        String headerName;
        ArrayList<String> elems;

        public Header() {
            headerName = "";
            elems = new ArrayList<String>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        foodNames = new ArrayList<String>();
        expiryDates = new ArrayList<String>();
        expirationDeadlines = new ArrayList<String>();*/
        foods = new ArrayList<FoodItem>();

        mEdit = (EditText)findViewById(R.id.foodInput);
        mButton = (Button)findViewById(R.id.submitButton);
        camButton = (Button)findViewById(R.id.cameraButton);
        currentListButton = (Button)findViewById(R.id.currentListButton);
        receiptButton = (Button) findViewById(R.id.receiptButton);

        mButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        String foodInput = mEdit.getText().toString();
                        new ExpirationRetriever().execute(foodInput);
                    }
                }
        );

        camButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                    }
                }
        );

        currentListButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        goToCurrentList();
                    }
                }
        );

        receiptButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        captureImageFromSdCard( null );
                    }
                }
        );

        mListView = (ListView) findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View itemView, int itemPosition, long itemId)
            {
                Header header = headers[itemPosition];

                ArrayList<String> storageOptions = new ArrayList<String>(),
                expiryDates = new ArrayList<String>();

                for(int i = 1; i < headers.length; i++){
                    storageOptions.add(headers[i].headerName);
                    expiryDates.add(headers[i].elems.get(itemPosition));
                }

                Bundle extra = new Bundle();
                extra.putSerializable("StorageOptions", storageOptions);
                extra.putSerializable("ExpiryDates", expiryDates);

                Intent storageOptionsIntent = new Intent(getBaseContext(), FoodStorageOptions.class);
                storageOptionsIntent.putExtra("FoodName", headers[0].elems.get(itemPosition));
                storageOptionsIntent.putExtra("extra", extra);

                startActivityForResult(storageOptionsIntent, STORAGE_OPTIONS_REQUEST);
            }
        });

        //save data
        if(savedInstanceState != null){
        /*    Bundle extra = savedInstanceState.getBundle("extra");
            foodNames = (ArrayList<String>)extra.getSerializable("FoodNames");
            expiryDates = (ArrayList<String>)extra.getSerializable("ExpiryDates");
            expirationDeadlines = (ArrayList<String>)extra.getSerializable("ExpirationDeadlines");*/


        }

        if(getPreferences(Context.MODE_PRIVATE).getStringSet("FoodSet", null) != null) {

            /*foodNames.addAll(getPreferences(Context.MODE_PRIVATE).getStringSet("FoodNames", null));
            expiryDates.addAll(getPreferences(Context.MODE_PRIVATE).getStringSet("ExpiryDates", null));
            expirationDeadlines.addAll(getPreferences(Context.MODE_PRIVATE).getStringSet("ExpirationDeadlines", null));*/

            Set<String> foodSet = getPreferences(Context.MODE_PRIVATE).getStringSet("FoodSet", null);

            for(String foodItemStr : foodSet){
                try{
                    JSONObject jsonObject = new JSONObject(foodItemStr);

                    String name = jsonObject.getString("name");
                    String expiryDate = jsonObject.getString("expiryDate");

                    FoodItem foodItem = new FoodItem(name, expiryDate);
                    foods.add(foodItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void captureImageFromSdCard( View view ) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, SELECT_PICTURE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
/*
        outState.putSerializable("FoodNames", foodNames);
        outState.putSerializable("ExpiryDates", expiryDates);
        outState.putSerializable("ExpirationDeadlines", expirationDeadlines);
*/
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void saveSettings(){
        /*Set<String> foodNamesSet = new HashSet<String>(foodNames);
        Set<String> expiryDatesSet = new HashSet<String>(expiryDates);
        Set<String> expirationDeadlinesSet = new HashSet<String>(expirationDeadlines);

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        editor.putStringSet("FoodNames", foodNamesSet);
        editor.putStringSet("ExpiryDates", expiryDatesSet);
        editor.putStringSet("ExpirationDeadlines", expirationDeadlinesSet);

        editor.commit();*/

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        Set<String> foodSet= new HashSet<String>();
        for(int i = 0; i < foods.size(); i++){
            foodSet.add(foods.get(i).getJSONObject().toString());
        }

        editor.putStringSet("FoodSet", foodSet);
        editor.commit();
    }

    @Override
    protected void onStop() {
        saveSettings();
        super.onStop();
    }

    private class ExpirationRetriever extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            webScrape(params[0]);
            return null;
        }

        public void webScrape(String foodInput) {
            String query = "how+long+do+" + foodInput + "+last";

            Document googleSearchDoc = null;

            //get google search results
            try {
                googleSearchDoc = Jsoup
                        .connect("http://www.google.com/search?q=" + query)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
                        .get();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            //using selector api here
            Elements links = googleSearchDoc.select("a[href]");

            String eatbydateLink = "";

            for (Element link : links) {
                String url = link.attr("href");

                if (url.contains("eatbydate")) {
                    eatbydateLink = "http://" + url.substring(url.lastIndexOf("www."), url.lastIndexOf('/') + 1);
                }
            }

            Document doc = null;

            //get page
            try {
                doc = Jsoup.connect(eatbydateLink).get();
                //get element by id
                Element table = doc.getElementById("unopened");
                Elements rows = table.getElementsByTag("TR");

                Elements rowHeaders = rows.get(0).getElementsByTag("TH");

                //create array of headers
                headers = new Header[rowHeaders.size()];

                for (int i = 0; i < rowHeaders.size(); i = i + 1) {
                    Element header = rowHeaders.get(i);
                    String stringHeader = header.text().toString();

                    headers[i] = new Header();
                    headers[i].headerName = stringHeader;
                }

                //get text in elmeent
                for (int i = 1; i < rows.size(); i = i + 1) {
                    Element row = rows.get(i);
                    Elements tds = row.getElementsByTag("TD");

                    if( tds.get(0).text().contains("last")) {
                        headers[0].elems.add(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("last")));
                    }
                    else if( tds.get(0).text().contains("lasts")){
                        headers[0].elems.add(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("lasts")));
                    }

                    for (int j = 1; j < tds.size(); j++) {
                        headers[j].elems.add(tds.get(j).text());
                    }
                }
            } catch (IOException ioe) {
                Toast.makeText(getApplicationContext(), foodInput + " does not exist", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> firstHeaderElems = headers[0].elems;
            String[] foodTypes = new String[firstHeaderElems.size()];

            for( int i = 0; i < firstHeaderElems.size(); i = i + 1 ){
                foodTypes[ i ] = firstHeaderElems.get(i);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                    android.R.layout.simple_list_item_1, foodTypes);
            mListView.setAdapter(adapter);
        }
    }

    private class ReceiptRecognitionRetriever extends AsyncTask<String,String,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            String inputFile = strings[0];
            String outputFile = strings[1];

            try {
                Client restClient = new Client();

                restClient.applicationId = "StillFresh";
                restClient.password = "mV3cmXCue7izcHAfX2OR8+H7";


                publishProgress( "Uploading image...");

                String language = "English"; // Comma-separated list: Japanese,English or German,French,Spanish etc.

                ReceiptSettings receiptSettings = new ReceiptSettings();
                receiptSettings.setReceiptCountry("Usa");

                publishProgress("Uploading..");

                // If you want to process business cards, uncomment this
			/*
			BusCardSettings busCardSettings = new BusCardSettings();
			busCardSettings.setLanguage(language);
			busCardSettings.setOutputFormat(BusCardSettings.OutputFormat.xml);
			Task task = restClient.processBusinessCard(filePath, busCardSettings);
			*/
                Task task = restClient.processReceipt(inputFile, receiptSettings);

                while( task.isTaskActive() ) {
                    // Note: it's recommended that your application waits
                    // at least 2 seconds before making the first getTaskStatus request
                    // and also between such requests for the same task.
                    // Making requests more often will not improve your application performance.
                    // Note: if your application queues several files and waits for them
                    // it's recommended that you use listFinishedTasks instead (which is described
                    // at http://ocrsdk.com/documentation/apireference/listFinishedTasks/).

                    Thread.sleep(5000);
                    publishProgress( "Waiting.." );
                    System.out.println("Waiting");
                    task = restClient.getTaskStatus(task.Id);
                }

                System.out.println("In between...");

                if( task.Status == Task.TaskStatus.Completed ) {
                    System.out.println("Downloading...");
                    publishProgress( "Downloading.." );
                    FileOutputStream fos = openFileOutput(outputFile,MODE_WORLD_READABLE);
                    System.out.println("Still Downloading...");
                    try {
                        restClient.downloadResult(task, fos);
                    } finally {
                        fos.close();
                    }

                    publishProgress( "Ready" );
                    System.out.println("Ready");
                } else if( task.Status == Task.TaskStatus.NotEnoughCredits ) {
                    System.out.println("ERROR");
                    throw new Exception( "Not enough credits to process task. Add more pages to your application's account." );
                } else {
                    System.out.println("ERROR #2");
                    throw new Exception( "Task failed" );
                }

                return true;
            } catch (Exception e) {
                final String message = "Error: " + e.getMessage();
                publishProgress( message);
                return false;
            }
        }
    }

    public class RetrievePredictionsTask extends AsyncTask<Bitmap, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Bitmap... voids) {
            Bitmap image = voids[0];
            ClarifaiClient cc = new ClarifaiBuilder("f4f11b38652648b8ab7b50df4db647f4")
                    // OPTIONAL. Allows customization of OkHttp by the user
                    .buildSync(); // or use .build() to get a Future<ClarifaiClient>

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            List<ClarifaiOutput<Concept>> predictionResults = cc.getDefaultModels().foodModel().predict()
                    .withInputs(
                            ClarifaiInput.forImage(ClarifaiImage.of(byteArray))
                    )
                    .executeSync().get();

            ArrayList<String> resultList = new ArrayList<String>();

            for (int i = 0; i < predictionResults.size(); i++) {


                ClarifaiOutput<Concept> clarifaiOutput = predictionResults.get(i);

                List<Concept> concepts = clarifaiOutput.data();

                if (concepts != null && concepts.size() > 0) {
                    for (int j = 0; j < concepts.size(); j++) {

                        resultList.add(concepts.get(j).name());
                    }
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> optionsList) {
            super.onPostExecute(optionsList);

            Bundle extra = new Bundle();
            extra.putSerializable("FoodOptions", optionsList);

            Intent optionsIntent = new Intent(getBaseContext(), CameraFoodOptions.class);
            optionsIntent.putExtra("extra", extra);
            startActivityForResult(optionsIntent, FOOD_OPTIONS_REQUEST);
        }
    }


    private int yearsToDays(int numYears){
        return 365 * numYears;
    }
    private int monthsToDays(int numMonths){
        return weeksToDays(4*numMonths);
    }
    private int weeksToDays(int numWeeks){
        return 7 * numWeeks;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {

            Bitmap image = (Bitmap) data.getExtras().get("data");
            // do whatever you want with the image now

            new RetrievePredictionsTask().execute( image );
        }
        else if(requestCode == FOOD_OPTIONS_REQUEST){

            mEdit.setText(data.getStringExtra("FoodName"));
        }
        else if(requestCode == STORAGE_OPTIONS_REQUEST){

            String expiryDate = data.getStringExtra("ExpiryDate");
            //expiryDates.add(expiryDate);

            Date expirationDeadline = getExpirationDeadline(expiryDate);

            //expirationDeadlines.add(new SimpleDateFormat("dd/MM/yyyy").format(expirationDeadline));

            String foodName = data.getStringExtra("FoodName");
            //foodNames.add(foodName);

            FoodItem foodItem = new FoodItem(foodName, new SimpleDateFormat("dd/MM/yyyy").format(expirationDeadline));
            foods.add(foodItem);

            //create notification
            createNotification(expirationDeadline, foodName);

            goToCurrentList();

            //startActivityForResult(currentFoodListIntent, CURRENT_FOOD_LIST_REQUEST);
        }
        else if(requestCode == CURRENT_FOOD_LIST_REQUEST){
            /*Bundle extra = getIntent().getBundleExtra("extra");
            foodNames = (ArrayList<String>)extra.getSerializable("FoodNames");
            expiryDates = (ArrayList<String>)extra.getSerializable("ExpiryDates");
            expirationDeadlines = (ArrayList<String>)extra.getSerializable("ExpirationDeadlines");*/
        }
        else if(requestCode == SELECT_PICTURE){
            Uri imageUri = data.getData();

            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cur = managedQuery(imageUri, projection, null, null, null);
            cur.moveToFirst();
            String imageFilePath = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));

            Intent results = new Intent( this, ResultsActivity.class);
            results.putExtra("IMAGE_PATH", imageFilePath);
            results.putExtra("RESULT_PATH", "result.txt");
            startActivity(results);
        }
    }

    private void goToCurrentList() {
        Intent currentFoodListIntent = new Intent(getBaseContext(), MainMenu.class);
        Bundle extra = new Bundle();
       /* extra.putSerializable("FoodNames", foodNames);
        extra.putSerializable("ExpiryDates", expiryDates);
        extra.putSerializable("ExpirationDeadlines",expirationDeadlines);
        currentFoodListIntent.putExtra("extra", extra);
*/
        currentFoodListIntent.putParcelableArrayListExtra("foods", foods);

        startActivity(currentFoodListIntent);
    }

    private Date getExpirationDeadline(String timeString) {

        int numUnits = 0;

        if( timeString.indexOf('-') != -1 ) {
            if(!timeString.equals("--")){
                numUnits = Integer.parseInt(timeString.substring(0, timeString.indexOf('-')));
            }
        }
        else{
            numUnits = Integer.parseInt(timeString.substring(0, timeString.indexOf(' ' )));
        }

        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.

        int time;
        if( timeString.contains("Day")){
            time = numUnits;
        }
        else if( timeString.contains("Week")){
            time = weeksToDays(numUnits);
        }
        else if( timeString.contains("Month")){
            time = monthsToDays(numUnits);
        }
        else if( timeString.contains("Year")){
            time = yearsToDays(numUnits);
        }
        else if(timeString.contains("Same day")){
            time = 0;
        }
        else{
            time = 0;
        }

        c.add(Calendar.DATE, time);

        return c.getTime();
    }


    private void createNotification(Date expirationDeadline, String foodName) {

        Intent i = new Intent(getApplicationContext(), ExpirationNotification.class);
        Bundle extra = new Bundle();
        extra.putString("FoodName", foodName);
        i.putExtra("extra", extra);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 3, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, expirationDeadline.getTime()-86400000, pi);

    }
}
