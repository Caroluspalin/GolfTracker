// MVC-rooli: Controller (apuluokka). Lisää jokaiseen näkymään navigaatiopalkin tarvitsemat tiedot.
package com.example.golftracker.controller;

import com.example.golftracker.service.KayttajaService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// @ControllerAdvice koskee kaikkia controllereita. Sen @ModelAttribute-metodit ajetaan ennen jokaista
// controller-metodia, ja palautettu arvo lisätään Modeliin. Näin jokaisen controllerin ei tarvitse lisätä samoja tietoja.
@ControllerAdvice
public class NavigaatioAdvice {

    // Kirjautuneen käyttäjän nimi navigaatiopalkkiin. Login- ja rekisteröitymissivulla käyttäjää ei ole, jolloin arvo on null.
    @ModelAttribute("kirjautunut")
    public String kirjautunut(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

    // Näytetäänkö Admin-merkki ja adminin toiminnot
    @ModelAttribute("onAdmin")
    public boolean onAdmin(Authentication authentication) {
        return authentication != null && KayttajaService.onAdmin(authentication);
    }
}
