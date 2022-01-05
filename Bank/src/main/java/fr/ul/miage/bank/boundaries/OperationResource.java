package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationResource extends JpaRepository<Operation, String> {
    List<Operation> findByAccount_Id(String idAccount);

    Optional<Operation> findByAccount_IdAndCard_NumberAndId(String idAccount, String number, String idOperation);

    List<Operation> findByAccount_IdAndCard_Number(String idAccount, String number);

    Optional<Operation> findByAccount_IdAndId(String id, String id1);

}
