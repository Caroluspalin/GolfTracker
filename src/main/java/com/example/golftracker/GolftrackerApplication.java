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
        Pelaaja palin = pRepo.save(new Pelaaja("Palin"));
        Pelaaja valtteri = pRepo.save(new Pelaaja("Valtteri"));
        Pelaaja mikko = pRepo.save(new Pelaaja("Mikko"));
        pRepo.save(new Pelaaja("Aino"));

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
