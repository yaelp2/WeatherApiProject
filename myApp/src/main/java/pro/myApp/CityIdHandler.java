package pro.myApp;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.vertx.core.Vertx;

public class CityIdHandler {
	private org.json.simple.JSONArray jsonArr; 
	Vertx vertx = Vertx.vertx();
	
	
	public CityIdHandler() {
		JSONParser parser = new JSONParser();
		vertx.fileSystem().readFile("target/cities.json", result -> {
			if(result.succeeded()) {
				try {
					jsonArr = (JSONArray) parser.parse(result.result().toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println(result.cause());
			}
		});
		
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
