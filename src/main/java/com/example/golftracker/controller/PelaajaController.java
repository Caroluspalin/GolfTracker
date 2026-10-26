// MVC-rooli: Controller. Hakee pelaajien yhteenvedot PelaajaServicen kautta ja antaa ne pelaajalista-näkymälle.
package com.example.golftracker.controller;

import com.example.golftracker.service.KayttajaService;
import com.example.golftracker.service.PelaajaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

// @Controller merkitsee luokan web-pyyntöjä käsitteleväksi MVC-ohjaimeksi.
@Controller
public class PelaajaController {

    // Spring injektoi riippuvuudet konstruktorin kautta; näin controller ei rakenna tietokantakerrosta itse.
    private final PelaajaService pelaajaService;
    private final KayttajaService kayttajaService;

    public PelaajaController(PelaajaService pelaajaService, KayttajaService kayttajaService) {
        this.pelaajaService = pelaajaService;
        this.kayttajaService = kayttajaService;
    }

    // Juuriosoite ohjataan pelaajalistaan, ettei käyttäjä näe tyhjää virhesivua.
    @GetMapping("/")
    public String etusivu() {
        return "redirect:/pelaajat";
    }

    // @GetMapping yhdistää GET-pyynnön /pelaajat-osoitteeseen.
    // Kirjautuneen käyttäjän nimi ja onAdmin tulevat Modeliin NavigaatioAdvicesta.
    @GetMapping("/pelaajat")
    public String pelaajalista(Model model, Authentication authentication) {
        // Oman pelaajan id:n avulla oma kortti nostetaan ylimmäksi ja näkymä näyttää muokkaus- ja poistonapit
        // vain omille kierroksille. Napit ovat vain käytettävyyttä: varsinainen oikeustarkistus tehdään KierrosServicessä.
        Long omaPelaajaId = kayttajaService.haeKirjautunut(authentication).getPelaaja().getId();
        model.addAttribute("omaPelaajaId", omaPelaajaId);
        model.addAttribute("yhteenvedot", pelaajaService.yhteenvedot(omaPelaajaId));

        return "pelaajalista";
    }
}
