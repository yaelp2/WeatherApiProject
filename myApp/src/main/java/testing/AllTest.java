package testing;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pro.myApp.App;

@RunWith(Suite.class)
@SuiteClasses({
    HealthcheckTest.class,
    HelloTest1.class,
    currentWeatherTest1.class
})
class AllTest {

	@Test
	void test() {
		App test = new App();
		test.start();
		
	}

}
