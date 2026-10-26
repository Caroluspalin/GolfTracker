// MVC-rooli: Model (apuluokka). Kuvaa käyttäjän roolin, joka tallennetaan Kayttaja-tauluun.
package com.example.golftracker;

// enum = kiinteä joukko vaihtoehtoja. Käyttäjä on aina joko USER tai ADMIN, ei mitään muuta.
// Spring Security odottaa rooleille etuliitettä "ROLE_", joten USER näkyy sille muodossa ROLE_USER.
public enum Rooli {
    USER,
    ADMIN
}
