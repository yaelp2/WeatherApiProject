package pro.myApp;


import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;


public class App 
{
	
	private Vertx vertx;
	private WebClient client;
	private HttpServer server;
	private Router router;
	private CityIdHandler cities;
	
	
    public static void main( String[] args )
    {
    	
    	App weatherApp = new App();
    	weatherApp.start();
    	
  
    }
	
	public App() {
		vertx = Vertx.vertx();
		client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(80).setDefaultHost("api.openweathermap.org"));
		server = vertx.createHttpServer();
		router = Router.router(vertx);
		cities = new CityIdHandler();
	}
	
	public void start() {
		healthcheckRoute();
		helloRoute();
		currentWeatherRoute();
		forecastsRoute();
		listenToRoute();
		System.out.println("Application is running...");
		
	}
	
	private void healthcheckRoute() {
    	router.route("/healthcheck").handler(routingContext -> {
    		HttpServerResponse response = routingContext.response();
    		response.putHeader("contexnt-type","text/plain");
    		response.end("Hello World!");

    	});
	}
	
	private void helloRoute() {
    	router.route("/hello").handler(routingContext -> {
    		String name = routingContext.request().getParam("name");
    		HttpServerResponse response = routingContext.response();
    		response.putHeader("contexnt-type","text/plain");
    		if(name != null) {
    			response.end("Hello "+name);
    		}
    		else {
    			response.end("Please fill name parameter");
    		}
    		
    	});
	}
	
	private void currentWeatherRoute() {
    	router.route("/currentforecasts").handler(routingContext -> {
    		String country = routingContext.request().getParam("country");
    		String city = routingContext.request().getParam("city");
    		long id;
    		if(country != null && city != null) {
    			id = cities.findCityId(country, city);
    			if(id != -1) {
    	    	    client.get("/data/2.5/weather?id="+id+"&units=metric&appid=e38f373567e83d2ba1b6928384435689").as(BodyCodec.string()).send(ar -> {
    	    	        if(ar.succeeded()) {
    	    	            HttpResponse<String> response = ar.result();
    	    	            System.out.println("Got HTTP response body");
    	    	            JSONObject res = null;
    	    	            JSONObject ans = new JSONObject();
    	    	            
    	    	            try {
    							res = new JSONObject(response.body());
    							ans.put("country", country.toUpperCase());
    							ans.put("city", city);
    							String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    							ans.put("date", date);
    		
    							
    						} catch (JSONException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    	    	            
    	    	            if(res != null) {
    	    	            	try {
    								double temp = ((JSONObject) res.get("main")).getDouble("temp");
    								int humid = ((JSONObject) res.get("main")).getInt("temp");		
    								ans.put("temp", temp);
    								ans.put("humidity", humid);
    								
    								routingContext.response().end(ans.toString());
    	
    								System.out.println(ans);
    							} catch (JSONException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							}
    	    	            }
    	    	        }
    	    	        else {
    	    	            ar.cause().printStackTrace();
    	    	        }
    	    	    });
    			}
    			else {
    				routingContext.response().end("At least one of the following is incorrect:\ncountry\ncity");
    			}
    	    
    		}
    		else {
    			routingContext.response().end("At least one of the following is missing:\ncountry\ncity");
    		}
    	});
	}
	
	private void forecastsRoute() {
    	router.route("/forecasts").handler(routingContext -> {
    		String country = routingContext.request().getParam("country");
    		String city = routingContext.request().getParam("city");
    		String days_str = routingContext.request().getParam("days");
    		if(days_str  != null && city != null && country != null){
    			
    			if(isNumeric(days_str)) {	
        			int days = Integer.parseInt(days_str);
        			
        			if(days <= 5) {
                		long id = cities.findCityId(country, city);
                		
                		if(id != -1) {
                	    	client.get("/data/2.5/forecast?id="+id+"&units=metric&appid=e38f373567e83d2ba1b6928384435689").as(BodyCodec.string()).send(ar -> {
                	    		
                	    		if(ar.succeeded()) {
                    	            System.out.println("Got HTTP response body");
                    	            HttpResponse<String> response = ar.result();
                    	            JSONObject res = null;
                    	            JSONObject ans = new JSONObject();
                    	            try {
										res = new JSONObject(response.body());
										ans.put("forecast", forecastAvgGroupByDate(days, res));
	            						System.out.println(ans);
	            						routingContext.response().end(ans.toString());
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

                	    		}
                	    		else { //GET request failed
                	    			ar.cause().printStackTrace();
                	    		}
                	    	});
                		}
                		else { //id == -1
                			routingContext.response().end("At least one of the following parameters is incorrect:\ncountry\ncity");
                		}
        			}
        			else { //days is greater than 5
        				routingContext.response().end("Cannot forcast more than 5 days ahead.\nPlease change days parameter");
        			}

 
    			}
           		else { //days parameter is empty
        			routingContext.response().end("Days value is not a number");
        		}

    		}

    		else {  //city/country/days is null
    			routingContext.response().end("At least one of the following parameters is missing:\ndays\ncity\ncountry");
    		}

    	});
	}
	
	private void listenToRoute() {
		server.requestHandler(router).listen(8080);
	}
	
	
    private static boolean isNumeric(String str) { 
  	  try {  
  	    Double.parseDouble(str);  
  	    return true;
  	  } catch(NumberFormatException e){  
  	    return false;  
  	  }  
  	}
  
	private static JSONArray forecastAvgGroupByDate(int days, JSONObject res) {
	    JSONArray objs = new JSONArray();
	    try {
				JSONArray jsonArr = ((JSONArray)res.get("list"));
		String prev_day;
		String cur_day;
		int index;
		int j = 1;
		int i = 1;
		int countSameDay = 1;
		JSONObject json = new JSONObject(jsonArr.getString(0));
		prev_day = json.get("dt_txt").toString();
		index = prev_day.indexOf(' ');
		prev_day = prev_day.substring(0,index);
		double dayTemp = ((JSONObject)(json.get("main"))).getDouble("temp");
		double minTemp = ((JSONObject)(json.get("main"))).getDouble("temp_min");
		double maxTemp = ((JSONObject)(json.get("main"))).getDouble("temp_max");
		DecimalFormat digitsAfterPoint = new DecimalFormat("##.##");
		
		while(i <= days && j < jsonArr.length()) {
			json = new JSONObject(jsonArr.getString(j));
			cur_day = json.get("dt_txt").toString();
			index = cur_day.indexOf(' ');
			cur_day = cur_day.substring(0,index);
			if(cur_day.equals(prev_day)) {
				dayTemp = dayTemp + ((JSONObject)(json.get("main"))).getDouble("temp");
				minTemp = minTemp + ((JSONObject)(json.get("main"))).getDouble("temp_min");
				maxTemp = maxTemp + ((JSONObject)(json.get("main"))).getDouble("temp_max");
				++countSameDay;
			}
			else {
				++i;
				JSONObject temp = new JSONObject();
				temp.put("date", prev_day);
				temp.put("dayTemp", digitsAfterPoint.format(dayTemp/countSameDay));
				temp.put("minTemp", digitsAfterPoint.format(minTemp/countSameDay));
				temp.put("maxTemp", digitsAfterPoint.format(maxTemp/countSameDay));
						objs.put(temp);
						dayTemp = 0;
						minTemp = 0;
						maxTemp = 0;
						countSameDay = 1;
						prev_day = cur_day;
						
					}
					++j;
					
				}
	  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return objs;
	      
	}
	
	

    

        
        
            
    
}
