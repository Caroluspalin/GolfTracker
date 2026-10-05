// MVC-rooli: Model. Kierros kuvaa tietokannan yhtä golfkierrosta ja liittyy yhteen Pelaaja-olioon.
package com.example.golftracker;

import jakarta.persistence.*;
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
    private LocalDate pvm;
    private String kentta;
    private int lyonnit;
    private int par;

    // @ManyToOne: monella kierroksella voi olla sama pelaaja; vierasavain on siis kierros-taulussa.
    @ManyToOne
    // @JoinColumn nimeää vierasavainsarakkeen. JPA tallentaa tähän pelaajan id:n (pelaaja_id).
    @JoinColumn(name = "pelaaja_id")
    private Pelaaja pelaaja;

    // JPA tarvitsee julkisen tai suojatun tyhjän konstruktorin, jotta se voi luoda olion tietokantarivistä.
    public Kierros() {}

    public Long getId() { return id; }
    public LocalDate getPvm() { return pvm; }
    public void setPvm(LocalDate pvm) { this.pvm = pvm; }
    public String getKentta() { return kentta; }
    public void setKentta(String kentta) { this.kentta = kentta; }
    public int getLyonnit() { return lyonnit; }
    public void setLyonnit(int lyonnit) { this.lyonnit = lyonnit; }
    public int getPar() { return par; }
    public void setPar(int par) { this.par = par; }
    public Pelaaja getPelaaja() { return pelaaja; }
    public void setPelaaja(Pelaaja pelaaja) { this.pelaaja = pelaaja; }
}