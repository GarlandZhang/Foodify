package com.example.gzhang.foodify2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.input.SearchClause;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.model.output_info.ConceptOutputInfo;
import clarifai2.dto.prediction.Concept;
import clarifai2.dto.search.SearchInputsResult;

public class MainActivity extends AppCompatActivity {

    Button mButton,
            camButton;
    EditText mEdit;
    Header[] headers;
    ListView mListView;

    int CAMERA_PIC_REQUEST = 0,
        OPTIONS_REQUEST = 1;

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

        mEdit = (EditText)findViewById(R.id.fruitInput);
        mButton = (Button)findViewById(R.id.submitButton);
        camButton = (Button)findViewById(R.id.cameraButton);

        mButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        String fruitInput = mEdit.getText().toString();
                        new ExpirationRetriever().execute(fruitInput);
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

        mListView = (ListView) findViewById(R.id.listView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView listView, View itemView, int itemPosition, long itemId)
            {
                System.out.println( "You selected: " + listView.getItemAtPosition( itemPosition));
                Intent intent = new Intent(getBaseContext(), MainMenu.class);

                Header header = headers[itemPosition];

                intent.putExtra("FruitName", header.elems.get(itemPosition));

                //for now just assume its just counter
                intent.putExtra("expiryDate1", headers[1].elems.get(itemPosition));
                startActivity(intent);
            }
        });

    }

    private class ExpirationRetriever extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            webScrape(params[0]);
            return null;
        }

        public void webScrape(String fruitInput) {
            String query = "how+long+do+" + fruitInput + "+last";

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

            System.out.println(eatbydateLink);

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

                    System.out.println(stringHeader);
                }

                //get text in elmeent
                for (int i = 1; i < rows.size(); i = i + 1) {
                    Element row = rows.get(i);
                    Elements tds = row.getElementsByTag("TD");

                    if( tds.get(0).text().contains("last")) {
                        headers[0].elems.add(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("last")));
                        System.out.println(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("last")));
                    }
                    else if( tds.get(0).text().contains("lasts")){
                        headers[0].elems.add(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("lasts")));
                        System.out.println(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("lasts")));
                    }

                    for (int j = 1; j < tds.size(); j++) {
                        headers[j].elems.add(tds.get(j).text());
                        System.out.println(tds.get(j).text());
                    }
                }
            } catch (IOException ioe) {
                Toast.makeText(getApplicationContext(), fruitInput + " does not exist", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> firstHeaderElems = headers[0].elems;
            String[] foodTypes = new String[firstHeaderElems.size()];

            for( int i = 0; i < firstHeaderElems.size(); i = i + 1 ){
                foodTypes[ i ] = firstHeaderElems.get(i);
                System.out.println("Food Types: " + foodTypes[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                    android.R.layout.simple_list_item_1, foodTypes);
            mListView.setAdapter(adapter);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {

            System.out.println( "HERE NOW " );

            Bitmap image = (Bitmap) data.getExtras().get("data");
            // do whatever you want with the image now

            new RetrievePredictionsTask().execute( image );
        }
        else if(requestCode == OPTIONS_REQUEST){

            mEdit.setText(data.getExtras().getString("foodName"));
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
                        System.out.println(concepts.get(j).name());
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

            Intent optionsIntent = new Intent(getBaseContext(), PossibleOptions.class);
            optionsIntent.putExtra("extra", extra);
            startActivityForResult(optionsIntent, OPTIONS_REQUEST);

            /*
            if( aVoid.get(0).equals("citrus") || aVoid.get(0).equals("juice"))
            {
                mEdit.setText("orange");
            }
            else {
                mEdit.setText(aVoid.get(0));
            }
            */
        }
    }
}
