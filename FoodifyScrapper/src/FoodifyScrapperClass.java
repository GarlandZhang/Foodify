import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FoodifyScrapperClass {

	public static class Header{
		
		String headerName;
		ArrayList<String> elems;
		
		public Header(){
			headerName="";
			elems = new ArrayList<String>();
		}
	}
	
	public static void main(String[]args ){
		
		String fruit_input = "strawberries";
		
		Document doc = null;

		//get page
		try {
			doc = Jsoup.connect("http://www.eatbydate.com/fruits/fresh/how-long-do-strawberries-last-shelf-life-expiration-date/").get();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		//get element by id
		Element table = doc.getElementById("unopened");
		Elements rows = table.getElementsByTag("TR");
		
		Elements rowHeaders = rows.get(0).getElementsByTag("TH");
		
		//create array of headers
		Header[] headers = new Header[rowHeaders.size()];
		
		for(int i = 0; i < rowHeaders.size(); i = i + 1 ){
			Element header = rowHeaders.get(i);
			String stringHeader = header.text();
			
			headers[i] = new Header();
			headers[i].headerName = stringHeader;
			
			System.out.println(stringHeader);
		}
		
		//get text in elmeent
		for (int i = 1; i < rows.size(); i = i + 1) {
			Element row = rows.get(i);
			Elements tds = row.getElementsByTag("TD");
			
			for (int j = 0; j < tds.size(); j++) {
				headers[j].elems.add(tds.get(j).text());
				System.out.println(tds.get(j).text());
			}
		}
		
		System.out.println("DONE");
	}
}
