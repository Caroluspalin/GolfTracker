package com.example.golftracker;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

// MVC-rooli: Controller. Hoitaa kierrosten lisäyksen, muokkauksen ja poiston.
// Varsinainen logiikka ja oikeustarkistukset ovat KierrosServicessä; controller hoitaa HTTP-pyynnöt ja näkymät.
@Controller
public class KierrosController {

    private final KierrosService kierrosService;

    // Spring antaa servicen konstruktorin kautta (dependency injection)
    public KierrosController(KierrosService kierrosService) {
        this.kierrosService = kierrosService;
    }

    // @InitBinder säätää, miten lomakkeen kentät sidotaan Kierros-olioon.
    // Estetään pelaaja-kentän sitominen suoraan pyynnöstä: pelaaja asetetaan aina servicessä.
    @InitBinder("kierros")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("pelaaja", "pelaaja.*");
    }

    // GET: näyttää tyhjän lomakkeen uutta kierrosta varten
    @GetMapping("/kierros/uusi")
    public String uusiKierros(Model model, Authentication authentication) {
        model.addAttribute("kierros", new Kierros());
        lisaaLomakkeenTiedot(model, authentication, null);
        return "kierroslomake";
    }

    // GET: näyttää saman lomakkeen valmiiksi täytettynä. Toisen pelaajan kierroksesta tulee 403.
    @GetMapping("/kierros/muokkaa/{id}")
    public String muokkaa(@PathVariable Long id, Model model, Authentication authentication) {
        Kierros kierros = kierrosService.haeMuokattavaksi(id, authentication);
        model.addAttribute("kierros", kierros);
        lisaaLomakkeenTiedot(model, authentication, kierros.getPelaaja().getId());
        return "kierroslomake";
    }

    // POST: tallentaa lomakkeen. @Valid tarkistaa Kierros-luokan validointiannotaatiot (@Min, @NotBlank...).
    // pelaajaId on valinnainen, koska tavallisen käyttäjän lomakkeessa ei ole pelaajavalintaa.
    @PostMapping("/kierros/tallenna")
    public String tallenna(@Valid @ModelAttribute("kierros") Kierros kierros, BindingResult tulos,
                           @RequestParam(required = false) Long pelaajaId,
                           Model model, Authentication authentication) {
        // Muokatessa tarkistetaan oikeus heti, ettei toisen kierroksen id:llä pääse edes virheellisen lomakkeen näkymään.
        if (kierros.getId() != null) {
            kierrosService.haeMuokattavaksi(kierros.getId(), authentication);
        }
        if (KayttajaService.onAdmin(authentication) && pelaajaId == null) {
            tulos.reject("pelaajaPuuttuu", "Valitse pelaaja");
        }

        // Virheiden kanssa näytetään sama lomake uudelleen, ja käyttäjän syöttämät arvot säilyvät kentissä.
        if (tulos.hasErrors()) {
            lisaaLomakkeenTiedot(model, authentication, pelaajaId);
            return "kierroslomake";
        }

        kierrosService.tallenna(kierros, pelaajaId, authentication);
        return "redirect:/pelaajat";
    }

    // POST: poistaa kierroksen. Poisto on POST eikä GET, koska GET-pyyntöjen ei pidä muuttaa dataa:
    // POST-lomake vaatii CSRF-tokenin, joten toinen sivusto ei voi poistaa kierroksia käyttäjän tietämättä.
    @PostMapping("/kierros/poista/{id}")
    public String poista(@PathVariable Long id, Authentication authentication) {
        kierrosService.poista(id, authentication);
        return "redirect:/pelaajat";
    }

    // Lomakkeen yhteiset tiedot: admin näkee pelaajavalinnan, tavallinen käyttäjä ei.
    private void lisaaLomakkeenTiedot(Model model, Authentication authentication, Long valittuPelaajaId) {
        boolean onAdmin = KayttajaService.onAdmin(authentication);
        model.addAttribute("onAdmin", onAdmin);
        model.addAttribute("valittuPelaajaId", valittuPelaajaId);
        if (onAdmin) {
            model.addAttribute("pelaajat", kierrosService.kaikkiPelaajat());
        }
    }
}
