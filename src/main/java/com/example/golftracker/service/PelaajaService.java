// MVC-rooli: Service. Kokoaa pelaajalistan näkymää varten: jokaiselle pelaajalle yhteenveto kierroksista.
package com.example.golftracker.service;

import java.util.List;

import com.example.golftracker.dto.PelaajaYhteenveto;
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
    @Transactional(readOnly = true)
    public List<PelaajaYhteenveto> yhteenvedot() {
        return pelaajaRepository.findAll().stream()
                .map(p -> new PelaajaYhteenveto(p, p.getKierrokset()))
                .toList();
    }
}
