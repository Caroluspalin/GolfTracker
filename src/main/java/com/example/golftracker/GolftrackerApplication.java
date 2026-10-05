// MVC-rooli: sovelluksen käynnistys ja esimerkkidatan alustus, ei varsinainen Model/View/Controller.
// Tämä luokka käynnistää Springin ja käyttää PelaajaRepositorya sekä KierrosRepositorya tietokantaan.
package com.example.golftracker;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Yhdistää Spring Bootin asetukset, automaattimääritykset ja komponenttien etsinnän tästä paketista.
@SpringBootApplication
public class GolftrackerApplication {

	public static void main(String[] args) {
		// Spring luo sovelluskontekstin, etsii controllerit ja repositoryt sekä käynnistää palvelimen.
		SpringApplication.run(GolftrackerApplication.class, args);
	}

	// @Bean rekisteröi palautetun olion Springin hallintaan; CommandLineRunner suoritetaan käynnistyksen jälkeen.
	// Repositoryt annetaan parametrina, koska Spring osaa luoda ja injektoida niiden toteutukset.
	@Bean
public CommandLineRunner demoData(PelaajaRepository pRepo, KierrosRepository kRepo) {
    return args -> {
        Pelaaja p1 = new Pelaaja("Palin");
        Pelaaja p2 = new Pelaaja("Valtteri");
        // save() tekee uusille olioille SQL INSERTin; id syntyy tietokannassa.
        pRepo.save(p1);
        pRepo.save(p2);

        // Kierros on Pelaajaan liittyvä rivi, joten tallennettaessa mukaan tulee pelaajan vierasavain.
        Kierros k1 = new Kierros();
        k1.setPvm(LocalDate.of(2026, 9, 20));
        k1.setKentta("Tali");
        k1.setPar(72);
        k1.setLyonnit(88);
        k1.setPelaaja(p1);
        // Vastaa käytännössä INSERTiä kierros-tauluun, pelaaja_id viittaa Palin-riviin.
        kRepo.save(k1);
    };
}

}
