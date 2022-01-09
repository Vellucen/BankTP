package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankAccountAssembler;
import fr.ul.miage.bank.entities.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
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
    private final CardRepresentation cards;
    private final OperationRepresentation operations;

    public AccountRepresentation(AccountResource ar, BankAccountAssembler assembler, CardRepresentation cards, OperationRepresentation operations) {
        this.ar = ar;
        this.assembler = assembler;
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
                10.00,
                account.getFirstname(),
                account.getLastname(),
                account.getBirthdate(),
                account.getCountry(),
                account.getPassportnumber(),
                account.getPhonenumber(),
                hashSecret(account.getPassportnumber(), account.getSecret()),
                ibanGenerator()
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

    //POST one OPERATION (transfer)
    @PostMapping(value = "/{accountId}/operations")
    @Transactional
    public ResponseEntity<?> transferOperation(@PathVariable("accountId") String idAccount, @RequestBody @Valid OperationInput operation) {
        return operations.transfer(idAccount, operation);
    }

    //PUT cap of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/cap/{newCap}")
    @Transactional
    public ResponseEntity<?> updateCapOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @PathVariable("newCap") Double newCap) {
        return cards.updateCapCard(idAccount, numCard, newCap);
    }

    //PUT blocked of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/blocked")
    @Transactional
    public ResponseEntity<?> updateBlockedOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.updateBlockedCard(idAccount, numCard);
    }

    //PUT location of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/location")
    @Transactional
    public ResponseEntity<?> updateLocationOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.updateLocationCard(idAccount, numCard);
    }

    //PUT contactless of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/contactless")
    @Transactional
    public ResponseEntity<?> updateContactlessOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.updateContactlessCard(idAccount, numCard);
    }

    //PUT virtual of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/virtual")
    @Transactional
    public ResponseEntity<?> updateVirtualOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        return cards.updateVirtualCard(idAccount, numCard);
    }

    // PUT amount of one ACCOUNT
    @PutMapping(value = "/{accountId}/amount/{moneyAdded}")
    @Transactional
    public ResponseEntity<?> updateAmountOfAccount(@PathVariable("accountId") String idAccount,@PathVariable("moneyAdded") Double moneyAdded) {
        if (moneyAdded>0.00) {
            Account account = ar.getById(idAccount);
            account.setAmount(account.getAmount() + moneyAdded);
            Optional<Account> body = Optional.ofNullable(account);
            if (!body.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            if (!ar.existsById(idAccount)) {
                return ResponseEntity.notFound().build();
            }
            account.setId(idAccount);
            Account result = ar.save(account);
            return ResponseEntity.ok().build();
        }
        else {
            return new ResponseEntity<>("Negatives or null values not allowed", HttpStatus.BAD_REQUEST);
        }

    }

    private String hashSecret(String numPassport, String secret){
        return org.apache.commons.codec.digest.DigestUtils.sha256(numPassport + secret).toString();
    }

    private String ibanGenerator () {
        return "FR55" + RandomStringUtils.randomNumeric(23);
    }
}
