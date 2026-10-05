// MVC-rooli: Repository. Tarjoaa Pelaaja-entiteetin tietokantaoperaatiot PelaajaControllerille.
package com.example.golftracker;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA luo toteutuksen automaattisesti. Esimerkiksi findAll() vastaa suunnilleen kyselyä SELECT ... FROM pelaaja,
// ja save() tekee uudelle pelaajalle INSERTin (olemassa olevalle riville päivityksen).
// JpaRepositoryn tyypit kertovat, että käsitellään Pelaaja-olioita, joiden id on Long.
public interface PelaajaRepository extends JpaRepository<Pelaaja, Long> {
}