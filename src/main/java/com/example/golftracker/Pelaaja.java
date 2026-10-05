// MVC-rooli: Model. Pelaaja vastaa tietokannan pelaajariviä ja kokoaa yhteen pelaajan Kierros-oliot.
package com.example.golftracker;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

// @Entity merkitsee luokan JPA:lle tallennettavaksi; Hibernate luo tai päivittää vastaavan taulun.
@Entity
public class Pelaaja {

    // @Id on taulun yksilöivä pääavain; Long voi olla null ennen ensimmäistä tallennusta.
    @Id
    // @GeneratedValue pyytää tietokantaa antamaan uuden id:n (IDENTITY = identity/autoincrement-tyyli).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nimi;

    // @OneToMany kuvaa suhteen: yhdellä pelaajalla voi olla monta kierrosta.
    // mappedBy kertoo, että omistava puoli on Kierros.pelaaja; erillistä liitostaulua ei tarvita,
    // vaan pelaaja_id-vierasavain sijaitsee kierros-taulussa. ALL välittää pelaajan tallennuksen ja poiston kierroksille.
    @OneToMany(mappedBy = "pelaaja", cascade = CascadeType.ALL)
    private List<Kierros> kierrokset;

    // JPA käyttää tyhjää konstruktoria luodessaan Pelaaja-olion tietokantarivistä.
    public Pelaaja() {
    }

    public Pelaaja(String nimi) {
        this.nimi = nimi;
    }

    public Long getId() {
        return id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public List<Kierros> getKierrokset() {
        return kierrokset;
    }
}