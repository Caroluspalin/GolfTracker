// MVC-rooli: testi, ei osa sovelluksen Model/View/Controller-kerroksia. Varmistaa sovelluskontekstin käynnistymisen.
package com.example.golftracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest käynnistää testissä Spring Boot -sovelluskontekstin, jotta kokonaisuus latautuu kuten sovelluksessa.
@SpringBootTest
class GolftrackerApplicationTests {

	// @Test merkitsee JUnitille ajettavan testimetodin; epäonnistuva kontekstin käynnistys kaataa testin.
	@Test
	void contextLoads() {
	}

}
