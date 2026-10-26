// MVC-rooli: Service (tietoturva). Spring Security kysyy tältä luokalta kirjautuvan käyttäjän tiedot tietokannasta.
package com.example.golftracker.service;

import com.example.golftracker.model.Kayttaja;
import com.example.golftracker.repository.KayttajaRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Kun käyttäjä lähettää login-lomakkeen, Spring Security kutsuu loadUserByUsername()-metodia,
// vertaa lomakkeen salasanaa palautettuun hashiin PasswordEncoderilla (BCrypt) ja päästää sisään, jos ne täsmäävät.
// Koska tämä on ainoa UserDetailsService-bean, Spring ottaa sen käyttöön automaattisesti.
@Service
public class KayttajaUserDetailsService implements UserDetailsService {

    private final KayttajaRepository kayttajaRepository;

    public KayttajaUserDetailsService(KayttajaRepository kayttajaRepository) {
        this.kayttajaRepository = kayttajaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Kayttaja kayttaja = kayttajaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Käyttäjää ei löydy: " + username));

        // Muunnetaan oma Kayttaja-entiteetti Spring Securityn ymmärtämään UserDetails-muotoon.
        // roles("USER") lisää automaattisesti ROLE_-etuliitteen.
        return User.withUsername(kayttaja.getUsername())
                .password(kayttaja.getSalasanaHash())
                .roles(kayttaja.getRooli().name())
                .build();
    }
}
