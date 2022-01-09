package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationResource extends JpaRepository<Operation, String> {
    List<Operation> findByAccount_Id(String idAccount);

    Optional<Operation> findByAccount_IdAndCard_NumberAndId(String idAccount, String number, String idOperation);

    List<Operation> findByAccount_IdAndCard_Number(String idAccount, String number);

    Optional<Operation> findByAccount_IdAndId(String idAccount, String id1);

    List<Operation> findByAccount_IdAndCategoryAndIbancreditorAndCountry(String idAccount, String category, String ibanCreditor, String country);

    List<Operation> findByAccount_IdAndIbancreditorAndCountry(String idAccount, String ibanCreditor, String country);

    List<Operation> findByAccount_IdAndCategoryAndCountry(String idAccount, String category, String country);

    List<Operation> findByAccount_IdAndCategoryAndIbancreditor(String idAccount, String category, String ibanCreditor);

    List<Operation> findByAccount_IdAndCategory(String idAccount, String category);

    List<Operation> findByAccount_IdAndIbancreditor(String idAccount, String ibanCreditor);

    List<Operation> findByAccount_IdAndCountry(String idAccount, String country);

}
