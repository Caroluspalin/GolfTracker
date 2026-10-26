// MVC-rooli: testi. Varmistaa, että kierrosten oikeustarkistukset toimivat palvelimella myös suorilla pyynnöillä.
package com.example.golftracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// @AutoConfigureMockMvc antaa MockMvc-olion, jolla voi lähettää HTTP-pyyntöjä sovellukselle ilman oikeaa palvelinta.
// Pyynnöt kulkevat Spring Securityn läpi kuten oikeassa sovelluksessa.
// @Transactional perutaan (rollback) jokaisen testin jälkeen, joten testit eivät sotke toistensa dataa.
// Testit käyttävät käynnistyksessä luotua dataa: "user" = pelaaja Valtteri, "mikko" = pelaaja Mikko, "admin" = Palin.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class KierrosOikeudetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KierrosRepository kierrosRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    // Apumetodi: hakee testidatasta ensimmäisen annetun pelaajan kierroksen
    private Kierros kierrosPelaajalle(String nimi) {
        return kierrosRepository.findAll().stream()
                .filter(k -> k.getPelaaja().getNimi().equals(nimi))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void kayttajaEiVoiAvataToisenKierroksenMuokkausta() throws Exception {
        Kierros mikonKierros = kierrosPelaajalle("Mikko");

        // user() kirjaa pyynnön sisään kyseisellä käyttäjällä ja roolilla
        mockMvc.perform(get("/kierros/muokkaa/" + mikonKierros.getId()).with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void kayttajaEiVoiTallentaaToisenKierrostaSuorallaPostilla() throws Exception {
        Kierros mikonKierros = kierrosPelaajalle("Mikko");

        // Hyökkääjä lähettää lomakkeen käsin toisen kierroksen id:llä
        mockMvc.perform(post("/kierros/tallenna").with(user("user").roles("USER")).with(csrf())
                        .param("id", mikonKierros.getId().toString())
                        .param("pvm", "2026-10-01")
                        .param("kentta", "Huijattu")
                        .param("par", "72")
                        .param("lyonnit", "60"))
                .andExpect(status().isForbidden());

        assertThat(kierrosRepository.findById(mikonKierros.getId()).orElseThrow().getKentta())
                .isEqualTo("Master Golf");
    }

    @Test
    void kayttajaEiVoiPoistaaToisenKierrosta() throws Exception {
        Kierros mikonKierros = kierrosPelaajalle("Mikko");

        mockMvc.perform(post("/kierros/poista/" + mikonKierros.getId()).with(user("user").roles("USER")).with(csrf()))
                .andExpect(status().isForbidden());

        assertThat(kierrosRepository.existsById(mikonKierros.getId())).isTrue();
    }

    @Test
    void kayttajaVoiMuokataOmaaKierrostaan() throws Exception {
        Kierros oma = kierrosPelaajalle("Valtteri");

        mockMvc.perform(post("/kierros/tallenna").with(user("user").roles("USER")).with(csrf())
                        .param("id", oma.getId().toString())
                        .param("pvm", "2026-10-01")
                        .param("kentta", "Kytäjä")
                        .param("par", "72")
                        .param("lyonnit", "80"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pelaajat"));

        assertThat(kierrosRepository.findById(oma.getId()).orElseThrow().getKentta()).isEqualTo("Kytäjä");
    }

    @Test
    void uusiKierrosLiitetaanOmaanPelaajaanVaikkaPyynnossaOlisiToinenPelaaja() throws Exception {
        Long mikonPelaajaId = kayttajaRepository.findByUsername("mikko").orElseThrow().getPelaaja().getId();

        mockMvc.perform(post("/kierros/tallenna").with(user("user").roles("USER")).with(csrf())
                        .param("pelaajaId", mikonPelaajaId.toString())
                        .param("pelaaja.id", mikonPelaajaId.toString())
                        .param("pvm", "2026-10-01")
                        .param("kentta", "Testikenttä")
                        .param("par", "72")
                        .param("lyonnit", "90"))
                .andExpect(status().is3xxRedirection());

        Long omaPelaajaId = kayttajaRepository.findByUsername("user").orElseThrow().getPelaaja().getId();
        assertThat(kierrosRepository.findAll()).filteredOn(k -> k.getKentta().equals("Testikenttä"))
                .singleElement()
                .satisfies(k -> assertThat(k.getPelaaja().getId()).isEqualTo(omaPelaajaId));
    }

    @Test
    void adminVoiPoistaaKenenTahansaKierroksen() throws Exception {
        Kierros mikonKierros = kierrosPelaajalle("Mikko");

        mockMvc.perform(post("/kierros/poista/" + mikonKierros.getId()).with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(kierrosRepository.existsById(mikonKierros.getId())).isFalse();
    }

    @Test
    void poistoGetPyynnollaEiOnnistu() throws Exception {
        Kierros oma = kierrosPelaajalle("Valtteri");

        // Vanha GET-linkki ei enää poista mitään: GET-metodia ei ole tälle osoitteelle (405 Method Not Allowed)
        mockMvc.perform(get("/kierros/poista/" + oma.getId()).with(user("user").roles("USER")))
                .andExpect(status().isMethodNotAllowed());

        assertThat(kierrosRepository.existsById(oma.getId())).isTrue();
    }

    @Test
    void poistoIlmanCsrfTokeniaHylataan() throws Exception {
        Kierros oma = kierrosPelaajalle("Valtteri");

        mockMvc.perform(post("/kierros/poista/" + oma.getId()).with(user("user").roles("USER")))
                .andExpect(status().isForbidden());

        assertThat(kierrosRepository.existsById(oma.getId())).isTrue();
    }

    @Test
    void virheellinenKierrosNaytetaanLomakkeessaVirheineen() throws Exception {
        long ennen = kierrosRepository.count();

        mockMvc.perform(post("/kierros/tallenna").with(user("user").roles("USER")).with(csrf())
                        .param("pvm", "2999-01-01")
                        .param("kentta", "")
                        .param("par", "100")
                        .param("lyonnit", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("kierroslomake"))
                .andExpect(model().attributeHasFieldErrors("kierros", "pvm", "kentta", "par", "lyonnit"));

        assertThat(kierrosRepository.count()).isEqualTo(ennen);
    }

    @Test
    void rekisteroityminenLuoKayttajanJaPelaajan() throws Exception {
        mockMvc.perform(post("/rekisteroidy").with(csrf())
                        .param("username", "uusi")
                        .param("nimi", "Uusi Pelaaja")
                        .param("salasana", "salasana1")
                        .param("salasanaUudelleen", "salasana1"))
                .andExpect(redirectedUrl("/login?rekisteroity"));

        Kayttaja uusi = kayttajaRepository.findByUsername("uusi").orElseThrow();
        assertThat(uusi.getRooli()).isEqualTo(Rooli.USER);
        assertThat(uusi.getPelaaja().getNimi()).isEqualTo("Uusi Pelaaja");
        // Salasana ei saa olla tietokannassa selväkielisenä
        assertThat(uusi.getSalasanaHash()).isNotEqualTo("salasana1").startsWith("$2");
    }

    @Test
    void rekisteroityminenVarattuKayttajanimiHylataan() throws Exception {
        mockMvc.perform(post("/rekisteroidy").with(csrf())
                        .param("username", "admin")
                        .param("nimi", "Huijari")
                        .param("salasana", "salasana1")
                        .param("salasanaUudelleen", "salasana1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("lomake", "username"));
    }
}
