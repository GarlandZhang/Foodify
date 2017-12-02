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
		
		String fruit_input = "strawberries",
				query = "how+long+do+" + fruit_input + "+last";
		
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
		
		for(Element link: links){
			String url = link.attr("href");
			
			if( url.contains("eatbydate")){
				eatbydateLink = "http://" + url.substring(url.lastIndexOf("www."), url.lastIndexOf('/')+1);
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
