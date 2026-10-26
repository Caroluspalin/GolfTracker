// MVC-rooli: sovelluksen käynnistys ja esimerkkidatan alustus, ei varsinainen Model/View/Controller.
// Tämä luokka käynnistää Springin ja luo testikäyttäjät KayttajaServicen sekä kierrokset KierrosRepositoryn kautta.
package com.example.golftracker;

import com.example.golftracker.model.Kierros;
import com.example.golftracker.model.Pelaaja;
import com.example.golftracker.model.Rooli;
import com.example.golftracker.repository.KayttajaRepository;
import com.example.golftracker.repository.KierrosRepository;
import com.example.golftracker.service.KayttajaService;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

// Yhdistää Spring Bootin asetukset, automaattimääritykset ja komponenttien etsinnän tästä paketista.
@SpringBootApplication
public class GolftrackerApplication {

	public static void main(String[] args) {
		// Spring luo sovelluskontekstin, etsii controllerit ja repositoryt sekä käynnistää palvelimen.
		SpringApplication.run(GolftrackerApplication.class, args);
	}

	// @Bean rekisteröi palautetun olion Springin hallintaan; CommandLineRunner suoritetaan käynnistyksen jälkeen.
	// Repositoryt ja servicet annetaan parametrina, koska Spring osaa luoda ja injektoida ne.
	// @ConditionalOnProperty: bean luodaan vain, jos asetus golftracker.demo-data on true
	// (dev-profiilissa aina, prod-profiilissa vain jos DEMO_DATA=true).
	@Bean
	@ConditionalOnProperty(name = "golftracker.demo-data", havingValue = "true")
	public CommandLineRunner demoData(KayttajaService kayttajaService, KayttajaRepository kayttajaRepo,
									  KierrosRepository kRepo) {
		return args -> {
			// Jos käyttäjiä on jo (esim. pysyvä tietokanta), ei luoda testidataa uudelleen.
			if (kayttajaRepo.count() > 0) {
				return;
			}

			// rekisteroi() luo jokaiselle käyttäjälle oman Pelaajan ja hashaa salasanan BCryptillä.
			Pelaaja palin = kayttajaService.rekisteroi("admin", "admin", "Palin", Rooli.ADMIN).getPelaaja();
			Pelaaja valtteri = kayttajaService.rekisteroi("user", "user", "Valtteri", Rooli.USER).getPelaaja();
			Pelaaja mikko = kayttajaService.rekisteroi("mikko", "mikko", "Mikko", Rooli.USER).getPelaaja();
			kayttajaService.rekisteroi("aino", "aino", "Aino", Rooli.USER);

			kRepo.save(uusiKierros(palin, LocalDate.of(2026, 9, 20), "Tali", 72, 88));
			kRepo.save(uusiKierros(palin, LocalDate.of(2026, 10, 7), "Master Golf", 72, 79));
			kRepo.save(uusiKierros(palin, LocalDate.of(2026, 10, 18), "Espoo Golf", 71, 83));
			kRepo.save(uusiKierros(valtteri, LocalDate.of(2026, 9, 28), "Tali", 72, 85));
			kRepo.save(uusiKierros(valtteri, LocalDate.of(2026, 10, 12), "Hirsala", 72, 81));
			kRepo.save(uusiKierros(mikko, LocalDate.of(2026, 10, 3), "Master Golf", 72, 95));
		};
	}

	// Apumetodi, ettei samoja settereitä tarvitse toistaa jokaiselle kierrokselle
	private static Kierros uusiKierros(Pelaaja pelaaja, LocalDate pvm, String kentta, int par, int lyonnit) {
		Kierros k = new Kierros();
		k.setPelaaja(pelaaja);
		k.setPvm(pvm);
		k.setKentta(kentta);
		k.setPar(par);
		k.setLyonnit(lyonnit);
		return k;
	}

}
