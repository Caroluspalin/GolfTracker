// MVC-rooli: Service. Kokoaa pelaajalistan näkymää varten: jokaiselle pelaajalle yhteenveto kierroksista.
package com.example.golftracker.service;

import java.util.Comparator;
import java.util.List;

import com.example.golftracker.dto.PelaajaYhteenveto;
import com.example.golftracker.model.Pelaaja;
import com.example.golftracker.repository.PelaajaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PelaajaService {

    private final PelaajaRepository pelaajaRepository;

    public PelaajaService(PelaajaRepository pelaajaRepository) {
        this.pelaajaRepository = pelaajaRepository;
    }

    // readOnly = true kertoo, että metodi vain lukee dataa. Transaktio pitää tietokantayhteyden auki,
    // jotta pelaajan kierrokset (OneToMany, ladataan laiskasti eli vasta tarvittaessa) voidaan lukea.
    // Kirjautuneen käyttäjän oma pelaaja järjestetään listan ensimmäiseksi.
    @Transactional(readOnly = true)
    public List<PelaajaYhteenveto> yhteenvedot(Long omaPelaajaId) {
        return pelaajaRepository.findAll().stream()
                // sorted() on vakaa: muiden pelaajien keskinäinen järjestys säilyy ennallaan.
                // Vertailuavain false (= oma) tulee ennen arvoa true.
                .sorted(Comparator.comparing((Pelaaja p) -> !p.getId().equals(omaPelaajaId)))
                .map(p -> new PelaajaYhteenveto(p, p.getKierrokset()))
                .toList();
    }
}
