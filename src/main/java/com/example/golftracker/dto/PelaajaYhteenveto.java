// MVC-rooli: Model (näkymää varten koottu olio). Pelaajan tilastot: kierrosten määrä, paras tulos ja keskiarvo suhteessa pariin.
package com.example.golftracker.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.example.golftracker.model.Kierros;
import com.example.golftracker.model.Pelaaja;

// Tämä EI ole @Entity: tilastoja ei tallenneta tietokantaan, vaan ne lasketaan aina kierroksista.
// Näin tilasto ei voi koskaan olla ristiriidassa kierrosten kanssa.
public class PelaajaYhteenveto {

    private final Pelaaja pelaaja;
    private final int kierroksia;
    private final Kierros parasKierros;
    private final Double keskiarvoPariin;

    // Kierrokset annetaan erikseen, jotta luokkaa on helppo testata ilman tietokantaa.
    public PelaajaYhteenveto(Pelaaja pelaaja, List<Kierros> kierrokset) {
        this.pelaaja = pelaaja;
        this.kierroksia = kierrokset.size();

        // Paras tulos = pienin ero pariin. Pelkät lyönnit eivät ole reilu vertailu, koska kenttien par vaihtelee.
        // Tasatilanteessa pienempi lyöntimäärä voittaa.
        this.parasKierros = kierrokset.stream()
                .min(Comparator.comparingInt(Kierros::getEroPariin).thenComparing(Kierros::getLyonnit))
                .orElse(null);

        // Stream-API: mapToInt muuttaa jokaisen kierroksen luvuksi, average() laskee keskiarvon.
        // Ilman kierroksia keskiarvoa ei ole, joten silloin arvo on null.
        this.keskiarvoPariin = kierrokset.isEmpty() ? null
                : kierrokset.stream().mapToInt(Kierros::getEroPariin).average().getAsDouble();
    }

    public Pelaaja getPelaaja() { return pelaaja; }
    public int getKierroksia() { return kierroksia; }
    public Kierros getParasKierros() { return parasKierros; }
    public Double getKeskiarvoPariin() { return keskiarvoPariin; }

    // Keskiarvo näytettäväksi suomalaisittain yhdellä desimaalilla, esim. "+9,7" tai "-1,5".
    public String getKeskiarvoTeksti() {
        if (keskiarvoPariin == null) return "–";
        String luku = String.format(Locale.forLanguageTag("fi-FI"), "%.1f", Math.abs(keskiarvoPariin));
        if (luku.equals("0,0")) return "E";
        return (keskiarvoPariin > 0 ? "+" : "-") + luku;
    }
}
