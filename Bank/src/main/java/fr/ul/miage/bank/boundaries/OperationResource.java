package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationResource extends JpaRepository<Operation, String> {
}
