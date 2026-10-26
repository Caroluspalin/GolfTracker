package com.example.golftracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Securityn asetukset: kuka pääsee minnekin. Käyttäjät luetaan tietokannasta KayttajaUserDetailsServicen kautta.
@Configuration
public class SecurityConfig {

    // Säännöt: mitkä osoitteet vaativat mitäkin oikeuksia
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                // Rekisteröitymissivulle pääsee ilman kirjautumista
                .requestMatchers("/rekisteroidy").permitAll()
                // Tyylit ja kuvat pitää sallia ilman kirjautumista, muuten login-sivu näkyisi ilman CSS:ää
                .requestMatchers("/css/**", "/img/**").permitAll()
                // Springin virhesivu (esim. 403/404) näytetään kaikille
                .requestMatchers("/error").permitAll()
                // Kaikki kirjautuneet saavat hallita kierroksia. Se, saako käyttäjä koskea juuri tähän kierrokseen,
                // tarkistetaan KierrosServicessä (oma kierros tai admin), koska URL-säännöt eivät tiedä kierroksen omistajaa.
                .requestMatchers("/kierros/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Oma login-sivu (KirjautumisController + login.html) oletussivun sijaan
                .loginPage("/login")
                .defaultSuccessUrl("/pelaajat", true)
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    // Salasanat tallennetaan hashattuina BCryptillä, ei selväkielisinä
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}