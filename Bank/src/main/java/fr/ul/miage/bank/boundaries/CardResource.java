package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardResource extends JpaRepository<Card, String> {
    List<Card> findByAccount_Id(String id);

    Optional<Card> findByAccount_IdAndNumber(String id, String number);

    Optional<Card> findByNumber(String number);

}
