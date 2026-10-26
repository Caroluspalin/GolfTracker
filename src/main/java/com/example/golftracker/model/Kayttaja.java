// MVC-rooli: Model. Kayttaja on kirjautumistunnus (käyttäjänimi, salasana, rooli) ja liittyy yhteen Pelaaja-profiiliin.
package com.example.golftracker.model;

import jakarta.persistence.*;

// Kirjautumistiedot pidetään erillään Pelaajasta: Pelaaja kuvaa golfaajaa, Kayttaja sitä, kuka sovellukseen kirjautuu.
@Entity
public class Kayttaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true: tietokanta estää kaksi samannimistä käyttäjää.
    @Column(nullable = false, unique = true)
    private String username;

    // Tähän tallennetaan BCrypt-hash (esim. "$2a$10$..."), ei koskaan selväkielistä salasanaa.
    @Column(nullable = false)
    private String salasanaHash;

    // EnumType.STRING tallentaa roolin tekstinä ("USER"/"ADMIN"). Oletus (ORDINAL) tallentaisi numeron 0/1,
    // mikä menisi rikki, jos enumin järjestystä joskus muutettaisiin.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rooli rooli;

    // @OneToOne: yhdellä käyttäjällä on täsmälleen yksi pelaajaprofiili ja päinvastoin.
    // Vierasavain pelaaja_id on kayttaja-taulussa; unique = true varmistaa, ettei kahta käyttäjää liitetä samaan pelaajaan.
    @OneToOne
    @JoinColumn(name = "pelaaja_id", nullable = false, unique = true)
    private Pelaaja pelaaja;

    public Kayttaja() {}

    public Kayttaja(String username, String salasanaHash, Rooli rooli, Pelaaja pelaaja) {
        this.username = username;
        this.salasanaHash = salasanaHash;
        this.rooli = rooli;
        this.pelaaja = pelaaja;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSalasanaHash() { return salasanaHash; }
    public void setSalasanaHash(String salasanaHash) { this.salasanaHash = salasanaHash; }
    public Rooli getRooli() { return rooli; }
    public void setRooli(Rooli rooli) { this.rooli = rooli; }
    public Pelaaja getPelaaja() { return pelaaja; }
    public void setPelaaja(Pelaaja pelaaja) { this.pelaaja = pelaaja; }
}
