// MVC-rooli: Repository. Tämä rajapinta tarjoaa Kierros-entiteetin tietokantaoperaatiot controllerille ja muulle sovellukselle.
package com.example.golftracker;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository antaa valmiit CRUD-operaatiot: esimerkiksi findAll() vastaa suunnilleen kyselyä SELECT ... FROM kierros.
// Toinen tyyppiparametri Long kertoo, että Kierros-entiteetin pääavain on Long-tyyppinen.
public interface KierrosRepository extends JpaRepository<Kierros, Long> {
}