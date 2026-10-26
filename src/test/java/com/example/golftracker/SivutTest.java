// MVC-rooli: testi. Varmistaa, että sivut ja tyylitiedosto näkyvät oikeille käyttäjille.
package com.example.golftracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SivutTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void omaLoginSivuNakyyIlmanKirjautumistaJaLinkittaaRekisteroitymiseen() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kirjaudu sisään")))
                .andExpect(content().string(containsString("href=\"/rekisteroidy\"")));
    }

    @Test
    void rekisteroitymissivuLinkittaaLoginiin() throws Exception {
        mockMvc.perform(get("/rekisteroidy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/login\"")));
    }

    @Test
    void tyylitiedostoOnSallittuIlmanKirjautumista() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());
    }

    @Test
    void kirjautumatonOhjataanOmalleLoginSivulle() throws Exception {
        mockMvc.perform(get("/pelaajat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void pelaajalistaNayttaaNavigaationJaTilastot() throws Exception {
        mockMvc.perform(get("/pelaajat").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kirjaudu ulos")))
                .andExpect(content().string(containsString("Keskiarvo pariin")))
                .andExpect(content().string(containsString("Sinä")));
    }

    @Test
    void omaPelaajakorttiOnYlimpana() throws Exception {
        // "user" on pelaaja Valtteri. Testidatassa Palin on luotu ensin, mutta Valtterin kortin pitää tulla ensimmäisenä.
        String html = mockMvc.perform(get("/pelaajat").with(user("user").roles("USER")))
                .andReturn().getResponse().getContentAsString();

        assertThat(html.indexOf(">Valtteri<")).isPositive().isLessThan(html.indexOf(">Palin<"));
    }

    @Test
    void juuriosoiteOhjaaPelaajalistaan() throws Exception {
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(redirectedUrl("/pelaajat"));
    }
}
