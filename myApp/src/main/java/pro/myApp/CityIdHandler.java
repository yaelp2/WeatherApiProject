package pro.myApp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CityIdHandler {
	private org.json.simple.JSONArray jsonArr; 
	
	
	public CityIdHandler() {
		JSONParser parser = new JSONParser();
		try {
			jsonArr = (JSONArray) parser.parse(new FileReader("C:\\Users\\pesso\\eclipse-workspace\\myApp\\src\\main\\java\\pro\\myApp\\cities.json"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long findCityId(String country, String city) {
		long id = -1;
		for(int i=0; i<jsonArr.size(); ++i) {
	
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) jsonArr.get(i);
			if(json.get("country").toString().toLowerCase().equals(country.toLowerCase()) && json.get("name").toString().toLowerCase().equals(city.toLowerCase())){
				id = (long)json.get("id");
				break;
			}
		
			
		}
		return id;
			


	}
	
	
	
}
