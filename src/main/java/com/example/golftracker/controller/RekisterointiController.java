// MVC-rooli: Controller. Näyttää rekisteröitymislomakkeen ja luo uuden käyttäjän KayttajaServicen avulla.
package com.example.golftracker.controller;

import com.example.golftracker.dto.RekisterointiLomake;
import com.example.golftracker.model.Rooli;
import com.example.golftracker.service.KayttajaService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RekisterointiController {

    private final KayttajaService kayttajaService;

    public RekisterointiController(KayttajaService kayttajaService) {
        this.kayttajaService = kayttajaService;
    }

    // GET: näyttää tyhjän rekisteröitymislomakkeen
    @GetMapping("/rekisteroidy")
    public String lomake(Model model) {
        model.addAttribute("lomake", new RekisterointiLomake());
        return "rekisteroidy";
    }

    // POST: @Valid tarkistaa RekisterointiLomakkeen annotaatiot, ja virheet kerätään BindingResultiin.
    // BindingResultin pitää olla heti validoitavan parametrin perässä, muuten Spring heittää poikkeuksen.
    @PostMapping("/rekisteroidy")
    public String rekisteroidy(@Valid @ModelAttribute("lomake") RekisterointiLomake lomake,
                               BindingResult tulos) {
        // Tarkistukset, joita ei voi tehdä pelkillä annotaatioilla, lisätään käsin rejectValue()-metodilla.
        if (lomake.getSalasana() != null && !lomake.getSalasana().equals(lomake.getSalasanaUudelleen())) {
            tulos.rejectValue("salasanaUudelleen", "eiTasmaa", "Salasanat eivät täsmää");
        }
        if (lomake.getUsername() != null && kayttajaService.onkoKaytossa(lomake.getUsername())) {
            tulos.rejectValue("username", "varattu", "Käyttäjänimi on jo käytössä");
        }

        // Virheiden kanssa näytetään sama lomake uudelleen; Thymeleaf näyttää virheilmoitukset kenttien vieressä.
        if (tulos.hasErrors()) {
            return "rekisteroidy";
        }

        // Uusi käyttäjä saa aina USER-roolin; adminiksi ei voi rekisteröityä itse.
        kayttajaService.rekisteroi(lomake.getUsername(), lomake.getSalasana(), lomake.getNimi(), Rooli.USER);
        return "redirect:/login?rekisteroity";
    }
}
