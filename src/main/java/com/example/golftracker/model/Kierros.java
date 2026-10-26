// MVC-rooli: Model. Kierros kuvaa tietokannan yhtä golfkierrosta ja liittyy yhteen Pelaaja-olioon.
package com.example.golftracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

// @Entity kertoo JPA:lle, että tämän luokan olioita tallennetaan tietokantatauluun.
@Entity
public class Kierros {

    // @Id on pääavain; Long voi olla null ennen tallennusta, kun taas long olisi oletuksena 0.
    @Id
    // @GeneratedValue pyytää tietokantaa luomaan id:n; IDENTITY vastaa tyypillisesti identity-/autoincrement-saraketta.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @DateTimeFormat auttaa Springiä lukemaan päivämäärän esimerkiksi HTML-lomakkeesta muodossa yyyy-MM-dd.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    // Bean Validation -annotaatiot tarkistetaan, kun controllerissa on @Valid. Virheviesti näytetään lomakkeessa.
    @NotNull(message = "Päivämäärä on pakollinen")
    @PastOrPresent(message = "Päivämäärä ei voi olla tulevaisuudessa")
    private LocalDate pvm;

    // @NotBlank hylkää sekä tyhjän että pelkkiä välilyöntejä sisältävän tekstin.
    @NotBlank(message = "Kenttä on pakollinen")
    private String kentta;

    // Integer (eikä int), jotta tyhjä kenttä on null eikä 0: silloin @NotNull antaa selkeän virheen
    // ja uuden kierroksen lomake näkyy tyhjänä.
    @NotNull(message = "Lyönnit on pakollinen")
    @Min(value = 18, message = "Lyöntejä vähintään 18")
    @Max(value = 200, message = "Lyöntejä enintään 200")
    private Integer lyonnit;

    @NotNull(message = "Par on pakollinen")
    @Min(value = 54, message = "Par vähintään 54")
    @Max(value = 80, message = "Par enintään 80")
    private Integer par;

    // @ManyToOne: monella kierroksella voi olla sama pelaaja; vierasavain on siis kierros-taulussa.
    @ManyToOne
    // @JoinColumn nimeää vierasavainsarakkeen. JPA tallentaa tähän pelaajan id:n (pelaaja_id).
    // nullable = false: kierros kuuluu aina jollekin pelaajalle.
    @JoinColumn(name = "pelaaja_id", nullable = false)
    private Pelaaja pelaaja;

    // JPA tarvitsee julkisen tai suojatun tyhjän konstruktorin, jotta se voi luoda olion tietokantarivistä.
    public Kierros() {}

    public Long getId() { return id; }
    public LocalDate getPvm() { return pvm; }
    public void setPvm(LocalDate pvm) { this.pvm = pvm; }
    public String getKentta() { return kentta; }
    public void setKentta(String kentta) { this.kentta = kentta; }
    public Integer getLyonnit() { return lyonnit; }
    public void setLyonnit(Integer lyonnit) { this.lyonnit = lyonnit; }
    public Integer getPar() { return par; }
    public void setPar(Integer par) { this.par = par; }
    public Pelaaja getPelaaja() { return pelaaja; }
    public void setPelaaja(Pelaaja pelaaja) { this.pelaaja = pelaaja; }
    public void setId(Long id) { this.id = id; }
}