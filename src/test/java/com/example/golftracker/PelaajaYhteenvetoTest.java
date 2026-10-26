// MVC-rooli: testi. Yksikkötesti PelaajaYhteenvedon laskennalle – ei tarvitse Springiä eikä tietokantaa.
package com.example.golftracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import com.example.golftracker.dto.PelaajaYhteenveto;
import com.example.golftracker.model.Kierros;
import com.example.golftracker.model.Pelaaja;

import org.junit.jupiter.api.Test;

// Ilman @SpringBootTestia testi on tavallinen JUnit-testi: nopea, koska sovellusta ei käynnistetä.
class PelaajaYhteenvetoTest {

    private static Kierros kierros(String kentta, int par, int lyonnit) {
        Kierros k = new Kierros();
        k.setPvm(LocalDate.of(2026, 10, 1));
        k.setKentta(kentta);
        k.setPar(par);
        k.setLyonnit(lyonnit);
        return k;
    }

    @Test
    void laskeeMaaranParhaanJaKeskiarvon() {
        // Erot pariin: +16, +7, +12 → keskiarvo +11,7
        PelaajaYhteenveto y = new PelaajaYhteenveto(new Pelaaja("Testi"), List.of(
                kierros("Tali", 72, 88),
                kierros("Master Golf", 72, 79),
                kierros("Espoo Golf", 71, 83)));

        assertThat(y.getKierroksia()).isEqualTo(3);
        assertThat(y.getParasKierros().getKentta()).isEqualTo("Master Golf");
        assertThat(y.getKeskiarvoPariin()).isCloseTo(11.67, org.assertj.core.data.Offset.offset(0.01));
        assertThat(y.getKeskiarvoTeksti()).isEqualTo("+11,7");
    }

    @Test
    void parasTulosVertaaPariinEikaPelkkiinLyonteihin() {
        // 74 lyöntiä par 70 (+4) on huonompi kuin 75 lyöntiä par 73 (+2)
        PelaajaYhteenveto y = new PelaajaYhteenveto(new Pelaaja("Testi"), List.of(
                kierros("Lyhyt", 70, 74),
                kierros("Pitkä", 73, 75)));

        assertThat(y.getParasKierros().getKentta()).isEqualTo("Pitkä");
        assertThat(y.getParasKierros().getEroPariinTeksti()).isEqualTo("+2");
    }

    @Test
    void alleParinJaParinTekstit() {
        assertThat(kierros("A", 72, 70).getEroPariinTeksti()).isEqualTo("-2");
        assertThat(kierros("A", 72, 72).getEroPariinTeksti()).isEqualTo("E");

        PelaajaYhteenveto y = new PelaajaYhteenveto(new Pelaaja("Testi"), List.of(kierros("A", 72, 70)));
        assertThat(y.getKeskiarvoTeksti()).isEqualTo("-2,0");
    }

    @Test
    void ilmanKierroksiaEiTilastoja() {
        PelaajaYhteenveto y = new PelaajaYhteenveto(new Pelaaja("Testi"), List.of());

        assertThat(y.getKierroksia()).isZero();
        assertThat(y.getParasKierros()).isNull();
        assertThat(y.getKeskiarvoPariin()).isNull();
        assertThat(y.getKeskiarvoTeksti()).isEqualTo("–");
    }
}
