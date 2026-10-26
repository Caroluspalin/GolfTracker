// MVC-rooli: Model (lomakeolio). Kuljettaa rekisteröitymislomakkeen tiedot näkymän ja controllerin välillä.
package com.example.golftracker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Tämä EI ole @Entity: lomakkeessa on kenttiä (salasana selväkielisenä, salasanan toisto), joita ei tallenneta sellaisenaan.
// Erillinen lomakeolio (DTO) estää myös sen, että käyttäjä voisi lähettää lomakkeessa esim. kentän rooli=ADMIN.
public class RekisterointiLomake {

    // Validointiannotaatiot tarkistetaan, kun controller käyttää @Valid-annotaatiota.
    @NotBlank(message = "Käyttäjänimi on pakollinen")
    @Size(min = 3, max = 30, message = "Käyttäjänimen pituus 3–30 merkkiä")
    @Pattern(regexp = "[a-zA-Z0-9_.-]*", message = "Käytä vain kirjaimia a–z, numeroita ja merkkejä _ . -")
    private String username;

    @NotBlank(message = "Pelaajan nimi on pakollinen")
    @Size(max = 50, message = "Nimi saa olla enintään 50 merkkiä")
    private String nimi;

    @Size(min = 6, max = 100, message = "Salasanan pituus vähintään 6 merkkiä")
    private String salasana;

    private String salasanaUudelleen;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }
    public String getSalasana() { return salasana; }
    public void setSalasana(String salasana) { this.salasana = salasana; }
    public String getSalasanaUudelleen() { return salasanaUudelleen; }
    public void setSalasanaUudelleen(String salasanaUudelleen) { this.salasanaUudelleen = salasanaUudelleen; }
}
