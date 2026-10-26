// MVC-rooli: Service. Kierrosten sovelluslogiikka ja oikeustarkistukset: kuka saa lisätä, muokata ja poistaa minkäkin kierroksen.
package com.example.golftracker;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// Oikeustarkistus tehdään täällä palvelimella eikä pelkästään piilottamalla linkkejä näkymästä:
// käyttäjä voi aina kirjoittaa osoitteen (esim. /kierros/muokkaa/5) tai lähettää lomakkeen käsin.
@Service
public class KierrosService {

    private final KierrosRepository kierrosRepository;
    private final PelaajaRepository pelaajaRepository;
    private final KayttajaService kayttajaService;

    public KierrosService(KierrosRepository kierrosRepository, PelaajaRepository pelaajaRepository,
                          KayttajaService kayttajaService) {
        this.kierrosRepository = kierrosRepository;
        this.pelaajaRepository = pelaajaRepository;
        this.kayttajaService = kayttajaService;
    }

    // Pelaajalista adminin pudotusvalikkoa varten
    public List<Pelaaja> kaikkiPelaajat() {
        return pelaajaRepository.findAll();
    }

    // Admin saa käsitellä kaikkia kierroksia, tavallinen käyttäjä vain omaan Pelaajaansa liitettyjä.
    public boolean saaMuokata(Kierros kierros, Authentication authentication) {
        if (KayttajaService.onAdmin(authentication)) {
            return true;
        }
        Pelaaja oma = kayttajaService.haeKirjautunut(authentication).getPelaaja();
        return kierros.getPelaaja().getId().equals(oma.getId());
    }

    // Hakee kierroksen ja varmistaa oikeuden. Jos kierrosta ei ole → 404, jos se on toisen → 403.
    // AccessDeniedException on Spring Securityn poikkeus, jonka se muuttaa automaattisesti vastaukseksi 403 Forbidden.
    public Kierros haeMuokattavaksi(Long id, Authentication authentication) {
        Kierros kierros = kierrosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierrosta ei löydy"));
        if (!saaMuokata(kierros, authentication)) {
            throw new AccessDeniedException("Et voi käsitellä toisen pelaajan kierrosta");
        }
        return kierros;
    }

    // Tallentaa uuden tai muokatun kierroksen.
    // pelaajaId otetaan huomioon vain adminilla; tavallisen käyttäjän kierros liitetään aina hänen omaan Pelaajaansa,
    // vaikka lomakkeeseen olisi käsin lisätty jonkun muun pelaajan id.
    @Transactional
    public Kierros tallenna(Kierros kierros, Long pelaajaId, Authentication authentication) {
        // Muokkaus: tarkistetaan, että tietokannassa oleva kierros on käyttäjän oma (piilokentän id:hen ei voi luottaa).
        if (kierros.getId() != null) {
            haeMuokattavaksi(kierros.getId(), authentication);
        }

        Pelaaja pelaaja;
        if (KayttajaService.onAdmin(authentication)) {
            pelaaja = pelaajaRepository.findById(pelaajaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pelaajaa ei löydy"));
        } else {
            pelaaja = kayttajaService.haeKirjautunut(authentication).getPelaaja();
        }
        kierros.setPelaaja(pelaaja);

        // save() tekee INSERTin jos id on null, muuten UPDATEn.
        return kierrosRepository.save(kierros);
    }

    // Poistaa kierroksen, jos käyttäjällä on siihen oikeus (DELETE FROM kierros WHERE id = ?)
    @Transactional
    public void poista(Long id, Authentication authentication) {
        Kierros kierros = haeMuokattavaksi(id, authentication);
        kierrosRepository.delete(kierros);
    }
}
