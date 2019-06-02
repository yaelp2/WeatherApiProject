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


class currentWeatherTest2 {

	@Test
	void test() {
//		App test = new App();
//		test.currentWeatherRoute();
//		test.listenToRoute();
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://localhost:8080/currentforecasts?country=ru&city=london");
		HttpResponse response;
		String res = null;
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String ans = rd.readLine();
			while((res = rd.readLine()) != null) {
				ans = ans +"\n"+res;
			}
			assertEquals("At least one of the following is incorrect:\ncountry\ncity",ans);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


