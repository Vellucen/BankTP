package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardResource extends JpaRepository<Card, String> {
    Card findByAccount_IdAndId(String id, String id1);
    List<Card> findByAccount_Id(String id);
}
