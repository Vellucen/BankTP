package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountResource extends JpaRepository<Account, String> {
}
