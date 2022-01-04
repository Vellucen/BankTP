package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationResource extends JpaRepository<Operation, String> {
    List<Operation> findByAccount_Id(String id);

    List<Operation> findByCard_Id(String id);
}
