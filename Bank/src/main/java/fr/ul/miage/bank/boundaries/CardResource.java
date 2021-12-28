package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardResource extends JpaRepository<Card, String> {
}
