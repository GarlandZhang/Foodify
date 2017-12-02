package com.example.gzhang.foodify2;

import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    EditText mEdit;
    Header[] headers;
    ListView mListView;

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

        mButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        String fruitInput = mEdit.getText().toString();
                        new ExpirationRetriever().execute(fruitInput);
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
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

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

                headers[0].elems.add(tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("last")));
                System.out.println( tds.get(0).text().toString().substring(0, tds.get(0).text().toString().indexOf("last")));
                for (int j = 1; j < tds.size(); j++) {
                    headers[j].elems.add(tds.get(j).text());
                    System.out.println(tds.get(j).text());
                }
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
}
