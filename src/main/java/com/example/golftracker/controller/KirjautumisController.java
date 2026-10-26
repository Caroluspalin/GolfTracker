// MVC-rooli: Controller. Näyttää oman login-sivun Spring Securityn oletussivun sijaan.
package com.example.golftracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller vain näyttää lomakkeen. Lomakkeen POST /login käsittelee Spring Security itse:
// se tarkistaa tunnukset KayttajaUserDetailsServicen avulla ja ohjaa onnistuessa /pelaajat-sivulle.
@Controller
public class KirjautumisController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
