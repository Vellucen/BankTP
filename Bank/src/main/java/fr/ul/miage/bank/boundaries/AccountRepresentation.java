package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankAccountAssembler;
import fr.ul.miage.bank.entities.*;
import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Account.class)
public class AccountRepresentation {

    private final AccountResource ar;
    private final BankAccountAssembler assembler;
    private final AccountValidator validator;
    private final CardRepresentation cards;
    private final OperationRepresentation operations;

    public AccountRepresentation(AccountResource ar, BankAccountAssembler assembler, AccountValidator validator, CardRepresentation cards, OperationRepresentation operations) {
        this.ar = ar;
        this.assembler = assembler;
        this.validator = validator;
        this.cards = cards;
        this.operations = operations;
    }

    // GET one ACCOUNT
    @GetMapping(value="/{accountId}")
    public ResponseEntity<?> getOneAccount(@PathVariable("accountId") String idAccount) {
        return Optional.ofNullable(ar.findById(idAccount)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET amount of one ACCOUNT
    @GetMapping(value="/{accountId}/amount")
    public ResponseEntity<?> getAmountOneAccount(@PathVariable("accountId") String idAccount) {
        return Optional.ofNullable(ar.findById(idAccount)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get()).getContent().getAmount()))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET one CARD of one ACCOUNT
    @GetMapping(value="/{accountId}/cards/{cardNum}")
    public ResponseEntity<?> getOneCardOneAccount(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.getOneCard(idAccount, numCard);
    }

    //GET all CARDS of one ACCOUNT
    @GetMapping(value = "/{accountId}/cards")
    public ResponseEntity<?> getAllCardsOneAccount(@PathVariable("accountId") String idAccount) {
        return cards.getAllCardsOfOneAccount(idAccount);
    }

    // GET one OPERATION of one ACCOUNT
    @GetMapping(value="/{accountId}/operations/{operationId}")
    public ResponseEntity<?> getOneOperationOneAccount(@PathVariable("accountId") String idAccount, @PathVariable("operationId") String idOperation) {
        return operations.getOneOperationOfOneAccount(idAccount, idOperation);
    }

    //GET all OPERATIONS of one ACCOUNT and filtered by PARAMS
    @GetMapping(value = "/{accountId}/operations")
    public ResponseEntity<?> getAllOperationsOneAccount(@PathVariable("accountId") String idAccount, @RequestParam(required = false) String category, @RequestParam(required = false) String shop, @RequestParam(required = false) String country) {
        return operations.getAllOperationsOfOneAccount(idAccount, category, shop, country);
    }

    // GET one OPERATION of one CARD
    @GetMapping(value="/{accountId}/cards/{cardNum}/operations/{operationId}")
    public ResponseEntity<?> getOneOperationOneCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @PathVariable("operationId") String idOperation) {
        return cards.getOneOperation(idAccount, numCard, idOperation);
    }

    //GET all OPERATIONS of one CARD
    @GetMapping(value = "/{accountId}/cards/{cardNum}/operations")
    public ResponseEntity<?> getAllOperationsOneCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.getAllOperations(idAccount, numCard);
    }

    //POST one ACCOUNT
    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountInput account)  {
        Account account2Save = new Account(
                UUID.randomUUID().toString(),
                0.00,
                account.getFirstname(),
                account.getLastname(),
                account.getBirthdate(),
                account.getCountry(),
                account.getPassportnumber(),
                account.getPhonenumber(),
                hashSecret(account.getPassportnumber(), account.getSecret()),
                account.getIban()
        );
        Account saved = ar.save(account2Save);
        URI location = linkTo(AccountRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    //POST one CARD
    @PostMapping(value = "/{accountId}/cards")
    @Transactional
    public ResponseEntity<?> saveCardOneAccount(@PathVariable("accountId") String idAccount, @RequestBody @Valid CardInput card) {
        return cards.saveCard(idAccount, card);
    }

    //POST one OPERATION (payment by card in shop use code)
    @PostMapping(value = "/{accountId}/cards/{cardNum}operations/code")
    @Transactional
    public ResponseEntity<?> paymentByCardUseCode(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @RequestBody @Valid OperationInput operation) {
        return operations.paymentByCode(idAccount, numCard, operation);
    }

    //POST one OPERATION (payment by card use contactless)
    @PostMapping(value = "/{accountId}/cards/{cardNum}operations/contactless")
    @Transactional
    public ResponseEntity<?> paymentByCardUseContactless(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @RequestBody @Valid OperationInput operation) {
        return operations.paymentUseContactless(idAccount, numCard, operation);
    }

    //POST one OPERATION (payment by card online)
    @PostMapping(value = "/{accountId}/cards/{cardNum}operations/online")
    @Transactional
    public ResponseEntity<?> paymentByCardOnline(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @RequestBody @Valid OperationInput operation) {
        return operations.paymentOnline(idAccount, numCard, operation);
    }

    //POST one OPERATION (transfer)
    @PostMapping(value = "/{accountId}/operations")
    @Transactional
    public ResponseEntity<?> transferOperation(@PathVariable("accountId") String idAccount, @RequestBody @Valid OperationInput operation) {
        return operations.transfer(idAccount, operation);
    }

    // PATCH one ACCOUNT
    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("accountId") String accountId, @RequestBody Map<Object, Object> fields) {
        Optional<Account> body = ar.findById(accountId);
        if (body.isPresent()) {
            Account account = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Account.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, account, v);
            });
            validator.validate(new AccountInput(account.getFirstname(),
                    account.getLastname(), account.getBirthdate(), account.getCountry(), account.getPassportnumber(), account.getPhonenumber(), account.getSecret(), account.getIban()));
            account.setId(accountId);
            ar.save(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    //PATCH one CARD
    @PatchMapping(value = "/{accountId}/cards/{cardNum}")
    @Transactional
    public ResponseEntity<?> updateCardPartiel(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @RequestBody Map<Object, Object> fields) {
        return cards.updateCardPartiel(idAccount, numCard, fields);
    }

    private String hashSecret(String numPassport, String secret){
        return org.apache.commons.codec.digest.DigestUtils.sha256(numPassport + secret).toString();
    }
}
