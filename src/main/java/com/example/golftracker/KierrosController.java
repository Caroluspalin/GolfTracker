package com.example.golftracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// MVC-rooli: Controller. Hoitaa kierrosten lisäyksen, muokkauksen ja poiston.
@Controller
public class KierrosController {

    private final KierrosRepository kierrosRepository;
    private final PelaajaRepository pelaajaRepository;

    // Spring antaa repositoryt konstruktorin kautta (dependency injection)
    public KierrosController(KierrosRepository kierrosRepository, PelaajaRepository pelaajaRepository) {
        this.kierrosRepository = kierrosRepository;
        this.pelaajaRepository = pelaajaRepository;
    }

    // GET: näyttää tyhjän lomakkeen uutta kierrosta varten
    @GetMapping("/kierros/uusi")
    public String uusiKierros(Model model) {
        model.addAttribute("kierros", new Kierros());
        model.addAttribute("pelaajat", pelaajaRepository.findAll());
        return "kierroslomake";
    }

    // GET: näyttää saman lomakkeen valmiiksi täytettynä olemassa olevan kierroksen tiedoilla
    @GetMapping("/kierros/muokkaa/{id}")
    public String muokkaa(@PathVariable Long id, Model model) {
        model.addAttribute("kierros", kierrosRepository.findById(id).orElseThrow());
        model.addAttribute("pelaajat", pelaajaRepository.findAll());
        return "kierroslomake";
    }

    // POST: tallentaa lomakkeen. save() tekee INSERTin jos id on null, muuten UPDATEn.
    @PostMapping("/kierros/tallenna")
    public String tallenna(@ModelAttribute Kierros kierros, @RequestParam Long pelaajaId) {
        Pelaaja pelaaja = pelaajaRepository.findById(pelaajaId).orElseThrow();
        kierros.setPelaaja(pelaaja);
        kierrosRepository.save(kierros);
        return "redirect:/pelaajat";
    }

    // GET: poistaa kierroksen id:n perusteella (DELETE FROM kierros WHERE id = ?)
    @GetMapping("/kierros/poista/{id}")
    public String poista(@PathVariable Long id) {
        kierrosRepository.deleteById(id);
        return "redirect:/pelaajat";
    }
}