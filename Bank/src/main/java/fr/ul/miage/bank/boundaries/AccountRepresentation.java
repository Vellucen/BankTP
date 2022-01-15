package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankAccountAssembler;
import fr.ul.miage.bank.entities.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return Optional.ofNullable(ar.findById(idAccount)).filter(Optional::isPresent)
                        .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                        .orElse(ResponseEntity.notFound().build());
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET amount of one ACCOUNT
    @GetMapping(value="/{accountId}/amount")
    public ResponseEntity<?> getAmountOneAccount(@PathVariable("accountId") String idAccount) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return Optional.ofNullable(ar.findById(idAccount)).filter(Optional::isPresent)
                        .map(i -> ResponseEntity.ok(assembler.toModel(i.get()).getContent().getAmount()))
                        .orElse(ResponseEntity.notFound().build());
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET one CARD of one ACCOUNT
    @GetMapping(value="/{accountId}/cards/{cardNum}")
    public ResponseEntity<?> getOneCardOneAccount(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.getOneCard(idAccount, numCard);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //GET all CARDS of one ACCOUNT
    @GetMapping(value = "/{accountId}/cards")
    public ResponseEntity<?> getAllCardsOneAccount(@PathVariable("accountId") String idAccount) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.getAllCardsOfOneAccount(idAccount);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET one OPERATION of one ACCOUNT
    @GetMapping(value="/{accountId}/operations/{operationId}")
    public ResponseEntity<?> getOneOperationOneAccount(@PathVariable("accountId") String idAccount, @PathVariable("operationId") String idOperation) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return operations.getOneOperationOfOneAccount(idAccount, idOperation);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //GET all OPERATIONS of one ACCOUNT and filtered by PARAMS
    @GetMapping(value = "/{accountId}/operations")
    public ResponseEntity<?> getAllOperationsOneAccount(@PathVariable("accountId") String idAccount, @RequestParam(required = false) String category, @RequestParam(required = false) String shop, @RequestParam(required = false) String country) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return operations.getAllOperationsOfOneAccount(idAccount, category, shop, country);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET one OPERATION of one CARD
    @GetMapping(value="/{accountId}/cards/{cardNum}/operations/{operationId}")
    public ResponseEntity<?> getOneOperationOneCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @PathVariable("operationId") String idOperation) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.getOneOperation(idAccount, numCard, idOperation);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //GET all OPERATIONS of one CARD
    @GetMapping(value = "/{accountId}/cards/{cardNum}/operations")
    public ResponseEntity<?> getAllOperationsOneCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.getAllOperations(idAccount, numCard);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.saveCard(idAccount, card);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //POST one OPERATION (transfer)
    @PostMapping(value = "/{accountId}/operations")
    @Transactional
    public ResponseEntity<?> transferOperation(@PathVariable("accountId") String idAccount, @RequestBody @Valid OperationInput operation) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return operations.transfer(idAccount, operation);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //PUT cap of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/cap/{newCap}")
    @Transactional
    public ResponseEntity<?> updateCapOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard, @PathVariable("newCap") Double newCap) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.updateCapCard(idAccount, numCard, newCap);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //PUT blocked of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/blocked")
    @Transactional
    public ResponseEntity<?> updateBlockedOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.updateBlockedCard(idAccount, numCard);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //PUT location of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/location")
    @Transactional
    public ResponseEntity<?> updateLocationOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.updateLocationCard(idAccount, numCard);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //PUT contactless of one CARD
    @PutMapping(value = "/{accountId}/cards/{cardNum}/contactless")
    @Transactional
    public ResponseEntity<?> updateContactlessOfCard(@PathVariable("accountId") String idAccount, @PathVariable("cardNum") String numCard) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
                return cards.updateContactlessCard(idAccount, numCard);
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT amount of one ACCOUNT
    @PutMapping(value = "/{accountId}/amount/{moneyAdded}")
    @Transactional
    public ResponseEntity<?> updateAmountOfAccount(@PathVariable("accountId") String idAccount,@PathVariable("moneyAdded") Double moneyAdded) {
        if (ar.existsById(idAccount)){
            if (usernameInToken().equals(usernameInAccount(idAccount))){
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
                    ar.save(account);
                    return ResponseEntity.ok().build();
                }
                else {
                    return new ResponseEntity<>("Negatives or null values not allowed", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private String ibanGenerator () {
        return "FR55" + RandomStringUtils.randomNumeric(23);
    }

    private String usernameInToken() {
        String username;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
                // retrieving username here
                username = kp.getKeycloakSecurityContext().getToken().getPreferredUsername();
            } else {
                username = "";
            }
        } else {
            username = "";
        }
        System.out.println("du token"+username);
        return username;
    }

    private String usernameInAccount (String idAccount) {
        Account account = ar.findById(idAccount).get();
        String username = (account.getFirstname()+account.getLastname()).toLowerCase();
        System.out.println("du compte"+username);
        return username;
    }
}