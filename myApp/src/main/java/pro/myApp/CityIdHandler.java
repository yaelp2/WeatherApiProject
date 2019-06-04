package pro.myApp;

import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.vertx.core.Vertx;

public class CityIdHandler {
	private static HashMap<String,Long> map;
	
	
	public CityIdHandler() {
		map = new HashMap<String,Long>();
		Vertx vertx = Vertx.vertx();
		JSONParser parser = new JSONParser();	
		vertx.fileSystem().readFile("src/main/resources/cities.json", result -> {
			if(result.succeeded()) {
				try {
					org.json.simple.JSONArray jsonArr;
					jsonArr = (JSONArray) parser.parse(result.result().toString());
					org.json.simple.JSONObject json;
					String key;
					for(int i=0; i<jsonArr.size(); ++i) {
							json = (org.json.simple.JSONObject) jsonArr.get(i);
							key = json.get("country").toString().toLowerCase()+json.get("name").toString().toLowerCase();
							map.put(key, (Long)json.get("id"));

						}
					
					
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
		String key = country.toLowerCase()+city.toLowerCase();
		if(map.containsKey(key)) {
			return map.get(key);
		}
		return -1;
			


	}
	
	
	
}
