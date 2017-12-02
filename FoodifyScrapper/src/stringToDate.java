import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class stringToDate {	
	
	public static int monthsToDays(int numMonths){
		return weeksToDays(4*numMonths);
	}
	
	public static int weeksToDays(int numWeeks){
		return 7 * numWeeks;
	}
	
	public static void main(String[]args){
		
		String timeString = "2 Days";
		
		int numUnits = Integer.parseInt(timeString.substring(0,  timeString.indexOf(' ')));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		
		int time = 0;
		
		if( timeString.contains("Days")){
			time = numUnits;
		}
		else if( timeString.contains("Weeks")){
			time = weeksToDays(numUnits);
		}
		else if( timeString.contains("Months")){
			time = monthsToDays(numUnits);
		}
		
		c.add(Calendar.DATE, time); // Adding 5 days
		String output = sdf.format(c.getTime());
		System.out.println(output);
		
		System.out.println(Calendar.getInstance().getTime());
	}
}
