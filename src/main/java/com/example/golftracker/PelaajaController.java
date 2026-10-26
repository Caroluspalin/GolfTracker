// MVC-rooli: Controller. Hakee pelaajat PelaajaRepositoryn kautta ja antaa ne pelaajalista-näkymälle.
package com.example.golftracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

// @Controller merkitsee luokan web-pyyntöjä käsitteleväksi MVC-ohjaimeksi.
@Controller
public class PelaajaController {

    // Spring injektoi riippuvuudet konstruktorin kautta; näin controller ei rakenna tietokantakerrosta itse.
    private final PelaajaRepository pelaajaRepository;
    private final KayttajaService kayttajaService;

    public PelaajaController(PelaajaRepository pelaajaRepository, KayttajaService kayttajaService) {
        this.pelaajaRepository = pelaajaRepository;
        this.kayttajaService = kayttajaService;
    }

    // @GetMapping yhdistää GET-pyynnön /pelaajat-osoitteeseen.
    // findAll() tuottaa JPA:n kautta pelaajien SELECT-kyselyn, ja tulos annetaan Thymeleafille.
    @GetMapping("/pelaajat")
    public String pelaajalista(Model model, Authentication authentication) {
        model.addAttribute("pelaajat", pelaajaRepository.findAll());

        model.addAttribute("onAdmin", KayttajaService.onAdmin(authentication));
        model.addAttribute("kayttaja", authentication.getName());
        // Oman pelaajan id:n avulla näkymä näyttää muokkaus- ja poistonapit vain omille kierroksille.
        // Tämä on vain käytettävyyttä: varsinainen oikeustarkistus tehdään KierrosServicessä.
        model.addAttribute("omaPelaajaId", kayttajaService.haeKirjautunut(authentication).getPelaaja().getId());

        return "pelaajalista";
    }
}
