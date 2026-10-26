// MVC-rooli: Service. Sisältää käyttäjiin liittyvän sovelluslogiikan: rekisteröinnin ja kirjautuneen käyttäjän haun.
package com.example.golftracker.service;

import com.example.golftracker.model.Kayttaja;
import com.example.golftracker.model.Pelaaja;
import com.example.golftracker.model.Rooli;
import com.example.golftracker.repository.KayttajaRepository;
import com.example.golftracker.repository.PelaajaRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service-kerros on controllerin ja repositoryn välissä: controller hoitaa HTTP:n, service säännöt, repository tietokannan.
@Service
public class KayttajaService {

    private final KayttajaRepository kayttajaRepository;
    private final PelaajaRepository pelaajaRepository;
    private final PasswordEncoder passwordEncoder;

    public KayttajaService(KayttajaRepository kayttajaRepository, PelaajaRepository pelaajaRepository,
                           PasswordEncoder passwordEncoder) {
        this.kayttajaRepository = kayttajaRepository;
        this.pelaajaRepository = pelaajaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean onkoKaytossa(String username) {
        return kayttajaRepository.existsByUsername(username);
    }

    // Luo uuden käyttäjän ja hänelle oman Pelaaja-profiilin.
    // @Transactional: molemmat tallennukset onnistuvat yhdessä tai kumpikaan ei jää tietokantaan.
    @Transactional
    public Kayttaja rekisteroi(String username, String salasana, String pelaajanNimi, Rooli rooli) {
        Pelaaja pelaaja = pelaajaRepository.save(new Pelaaja(pelaajanNimi));
        // encode() tekee salasanasta BCrypt-hashin, johon on leivottu mukaan satunnainen suola.
        String hash = passwordEncoder.encode(salasana);
        return kayttajaRepository.save(new Kayttaja(username, hash, rooli, pelaaja));
    }

    // Hakee tietokannasta sen Kayttajan, joka on kirjautunut sisään (Authentication.getName() = username).
    public Kayttaja haeKirjautunut(Authentication authentication) {
        return kayttajaRepository.findByUsername(authentication.getName()).orElseThrow();
    }

    // Tarkistaa roolin Spring Securityn Authentication-oliosta (sama tarkistus kuin aiemmin PelaajaControllerissa).
    public static boolean onAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
