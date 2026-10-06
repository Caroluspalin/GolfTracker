// MVC-rooli: Controller. Hakee pelaajat PelaajaRepositoryn kautta ja antaa ne pelaajalista-näkymälle.
package com.example.golftracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

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
    public String pelaajalista(Model model, Authentication authentication) {
        model.addAttribute("pelaajat", pelaajaRepository.findAll());

        // Tarkistetaan onko kirjautuneella käyttäjällä ADMIN-rooli
        boolean onAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("onAdmin", onAdmin);
        model.addAttribute("kayttaja", authentication.getName());

        return "pelaajalista";
    }
}