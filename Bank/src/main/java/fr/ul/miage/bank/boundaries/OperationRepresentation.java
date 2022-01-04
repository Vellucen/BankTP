package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankOperationAssembler;
import fr.ul.miage.bank.entities.*;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.lang.reflect.Field;
import java.net.URI;
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
    public ResponseEntity<?> getOneOperationOfOneAccount(String id) {
        return Optional.ofNullable(or.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    //GET all OPERATIONS of one ACCOUNT
    public ResponseEntity<?> getAllOperationsOfOneAccount(String id) {
        return ResponseEntity.ok(assembler.toCollectionModel(or.findByAccount_Id(id)));
    }

    // GET one OPERATION of one CARD
    public ResponseEntity<?> getOneOperationOfOneCard(String id) {
        return Optional.ofNullable(or.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    //GET all OPERATIONS of one CARD
    public ResponseEntity<?> getAllOperationsOfOneCard(String numCard) {
        return ResponseEntity.ok(assembler.toCollectionModel(or.findByCard_Number(numCard)));
    }

    //POST one OPERATION
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveOperation(@RequestBody @Valid OperationInput operation)  {
        Operation operation2Save = new Operation(
                UUID.randomUUID().toString(),
                operation.getAccount(),
                operation.getCard(),
                operation.getWording(),
                operation.getCategory(),
                operation.getAmount(),
                operation.getRate(),
                operation.getDate(),
                operation.getCreditoraccount(),
                operation.getCountry()
        );
        Operation saved = or.save(operation2Save);
        URI location = linkTo(OperationRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // DELETE
    @DeleteMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> deleteOperation(@PathVariable("operationId") String operationId) {
        Optional<Operation> operation = or.findById(operationId);
        if (operation.isPresent()) {
            or.delete(operation.get());
        }
        return ResponseEntity.noContent().build();
    }

    // PUT
    @PutMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> updateOperation(@RequestBody Operation operation,
                                        @PathVariable("operationId") String operationId) {
        Optional<Operation> body = Optional.ofNullable(operation);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!or.existsById(operationId)) {
            return ResponseEntity.notFound().build();
        }
        operation.setId(operationId);
        Operation result = or.save(operation);
        return ResponseEntity.ok().build();
    }

    // PATCH
    @PatchMapping(value = "/{operationId}")
    @Transactional
    public ResponseEntity<?> updateOperationPartiel(@PathVariable("operationId") String operationId,
                                                  @RequestBody Map<Object, Object> fields) {
        Optional<Operation> body = or.findById(operationId);
        if (body.isPresent()) {
            Operation operation = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Operation.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, operation, v);
            });
            validator.validate(new OperationInput(operation.getAccount(), operation.getCard(), operation.getWording(), operation.getCategory(),
                    operation.getAmount(), operation.getRate(), operation.getDate(), operation.getCreditoraccount(), operation.getCountry()));
            operation.setId(operationId);
            or.save(operation);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
