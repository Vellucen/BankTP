package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankOperationAssembler;
import fr.ul.miage.bank.entities.*;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final OperationResource or;
    private final BankOperationAssembler assembler;
    private final OperationValidator validator;

    public OperationRepresentation(OperationResource or, BankOperationAssembler assembler, OperationValidator validator) {
        this.or = or;
        this.assembler = assembler;
        this.validator = validator;
    }

    // GET one OPERATION of one ACCOUNT
    public ResponseEntity<?> getOneOperationOfOneAccount(String idAccount, String idOperation) {
        return Optional.ofNullable(or.findByAccount_IdAndId(idAccount, idOperation)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    //GET all OPERATIONS of one ACCOUNT and filtered by PARAMS
    public ResponseEntity<?> getAllOperationsOfOneAccount(String idAccount, String category, String shop, String country) {

        if (category.isEmpty() && shop.isEmpty() && country.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_Id(idAccount)));
        }
        else if (shop.isEmpty() && country.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCategory(idAccount, category)));
        }
        else if (category.isEmpty() && country.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndShop(idAccount, shop)));
        }
        else if (category.isEmpty() && shop.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCountry(idAccount, country)));
        }
        else if (country.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCategoryAndShop(idAccount, category, shop)));
        }
        else if (shop.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCategoryAndCountry(idAccount, category, country)));
        }
        else if (category.isEmpty()){
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndShopAndCountry(idAccount, shop, country)));
        }
        else {
            return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCategoryAndShopAndCountry(idAccount, category, shop, country)));
        }
    }

    // GET one OPERATION of one CARD
    public ResponseEntity<?> getOneOperationOfOneCard(String idAccount, String numCard, String idOperation) {
        return Optional.ofNullable(or.findByAccount_IdAndCard_NumberAndId(idAccount, numCard, idOperation)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    //GET all OPERATIONS of one CARD
    public ResponseEntity<?> getAllOperationsOfOneCard(String idAccount, String numCard) {
        return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_IdAndCard_Number(idAccount, numCard)));
    }

    //POST one OPERATION
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(String idAccount, @RequestBody @Valid OperationInput operation)  {
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                operation.getAccount(),
                operation.getCard(),
                operation.getWording(),
                operation.getCategory(),
                operation.getAmount(),
                operation.getRate(),
                new Date(),
                operation.getShop(),
                operation.getCountry()
        );
        Operation saved = or.save(operation2Save);
        URI location = linkTo(OperationRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // PATCH
    @PatchMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> updateOperationPartiel(@PathVariable("operationId") String operationId, @RequestBody Map<Object, Object> fields) {
        Optional<Operation> body = or.findById(operationId);
        if (body.isPresent()) {
            Operation operation = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Operation.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, operation, v);
            });
            validator.validate(new OperationInput(operation.getAccount(), operation.getCard(), operation.getWording(), operation.getCategory(),
                    operation.getAmount(), operation.getRate(), operation.getDate(), operation.getShop(), operation.getCountry()));
            operation.setId(operationId);
            or.save(operation);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
