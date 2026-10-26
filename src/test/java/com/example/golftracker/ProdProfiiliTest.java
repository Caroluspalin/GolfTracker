// MVC-rooli: testi. Varmistaa, että prod-profiili lukee yhteystiedot ympäristömuuttujista eikä luo testitunnuksia.
package com.example.golftracker;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import com.example.golftracker.repository.KayttajaRepository;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// @ActiveProfiles("prod") käynnistää sovelluksen prod-asetuksilla. Oikeaa Supabasea ei testeissä käytetä,
// vaan DB_URL-muuttujaksi annetaan H2-muistikanta PostgreSQL-yhteensopivuustilassa.
// properties-arvot toimivat testissä samoin kuin ympäristömuuttujat: ${DB_URL} saa arvonsa täältä.
@SpringBootTest(properties = {
        "DB_URL=jdbc:h2:mem:prodtesti;MODE=PostgreSQL",
        "DB_USERNAME=sa",
        "DB_PASSWORD="
})
@ActiveProfiles("prod")
class ProdProfiiliTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Test
    void yhteysosoiteLuetaanYmparistomuuttujasta() {
        assertThat(((HikariDataSource) dataSource).getJdbcUrl()).isEqualTo("jdbc:h2:mem:prodtesti;MODE=PostgreSQL");
        assertThat(((HikariDataSource) dataSource).getMaximumPoolSize()).isEqualTo(5);
    }

    @Test
    void tuotannossaEiLuodaTestitunnuksia() {
        assertThat(kayttajaRepository.count()).isZero();
    }
}
