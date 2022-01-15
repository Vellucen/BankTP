package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountResource extends JpaRepository<Account, String> {
    Account findByIban(String iban);

    boolean existsByIban(String iban);

    boolean existsByFirstnameAndLastname(String firstname, String lastname);

    Account findByFirstnameAndLastname(String firstname, String lastname);
}
