// MVC-rooli: Repository. Tarjoaa Kayttaja-entiteetin tietokantaoperaatiot kirjautumiselle ja rekisteröitymiselle.
package com.example.golftracker;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KayttajaRepository extends JpaRepository<Kayttaja, Long> {

    // Spring Data päättelee kyselyn metodin nimestä: SELECT ... FROM kayttaja WHERE username = ?
    // Optional kertoo, että käyttäjää ei välttämättä löydy.
    Optional<Kayttaja> findByUsername(String username);

    // Vastaa suunnilleen kyselyä SELECT COUNT(*) > 0 FROM kayttaja WHERE username = ?
    boolean existsByUsername(String username);
}
