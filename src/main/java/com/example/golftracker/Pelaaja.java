package com.example.golftracker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity 
public class Pelaaja {

    @Id 
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String nimi;   

    public Pelaaja() {

    }

    public Pelaaja(String nimi) {
        this.nimi = nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getNimi() {
        return nimi;
    }

    public Long getId() {
        return id;
    }

}