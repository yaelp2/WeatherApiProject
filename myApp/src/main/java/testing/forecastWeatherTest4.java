package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;


class forecastWeatherTest4 {

	@Test
	void test() {

		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://localhost:8080/forecasts?country=il&city=tel%20aviv&days=6");
		HttpResponse response;
		String res = null;
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String ans = rd.readLine();
			while((res = rd.readLine()) != null) {
				ans = ans +"\n"+res;
			}
			assertEquals("Cannot forcast more than 5 days ahead.\nPlease change days parameter",ans);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
