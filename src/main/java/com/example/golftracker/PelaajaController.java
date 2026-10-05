// MVC-rooli: Controller. Hakee pelaajat PelaajaRepositoryn kautta ja antaa ne pelaajalista-näkymälle.
package com.example.golftracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller merkitsee luokan web-pyyntöjä käsitteleväksi MVC-ohjaimeksi.
@Controller
public class PelaajaController {

    // Spring injektoi repositoryn konstruktorin kautta; näin controller ei rakenna tietokantakerrosta itse.
    private final PelaajaRepository pelaajaRepository;

    public PelaajaController(PelaajaRepository pelaajaRepository) {
        this.pelaajaRepository = pelaajaRepository;
    }

    // @GetMapping yhdistää GET-pyynnön /pelaajat-osoitteeseen.
    // findAll() tuottaa JPA:n kautta pelaajien SELECT-kyselyn, ja tulos annetaan Thymeleafille.
    @GetMapping("/pelaajat")
    public String pelaajalista(Model model) {
        model.addAttribute("pelaajat", pelaajaRepository.findAll());
        // Palautettu nimi ratkaisee templates/pelaajalista.html-tiedoston.
        return "pelaajalista";
    }
}