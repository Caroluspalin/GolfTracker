package com.example.golftracker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class KierrosController {

    private final KierrosRepository kierrosRepository;
    private final PelaajaRepository pelaajaRepository;

    public KierrosController(KierrosRepository kierrosRepository, PelaajaRepository pelaajaRepository) {
        this.kierrosRepository = kierrosRepository;
        this.pelaajaRepository = pelaajaRepository;
    }

    // Näyttää tyhjän lomakkeen
    @GetMapping("/kierros/uusi")
    public String uusiKierros(Model model) {
        model.addAttribute("kierros", new Kierros());
        model.addAttribute("pelaajat", pelaajaRepository.findAll());
        return "kierroslomake";
    }

    // Tallentaa lomakkeen tiedot
    @PostMapping("/kierros/tallenna")
    public String tallenna(@ModelAttribute Kierros kierros, @RequestParam Long pelaajaId) {
        Pelaaja pelaaja = pelaajaRepository.findById(pelaajaId).orElseThrow();
        kierros.setPelaaja(pelaaja);
        kierrosRepository.save(kierros);
        return "redirect:/pelaajat";
    }
}