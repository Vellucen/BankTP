package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankAccountAssembler;
import fr.ul.miage.bank.entities.Account;
import fr.ul.miage.bank.entities.AccountInput;
import fr.ul.miage.bank.entities.AccountValidator;
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
@RequestMapping(value="/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Account.class)
public class AccountRepresentation {

    private final AccountResource ar;
    private final BankAccountAssembler assembler;
    private final AccountValidator validator;

    public AccountRepresentation(AccountResource ar, BankAccountAssembler assembler, AccountValidator validator) {
        this.ar = ar;
        this.assembler = assembler;
        this.validator = validator;
    }

    // GET all
    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        return ResponseEntity.ok(assembler.toCollectionModel(ar.findAll()));
    }

    // GET one
    @GetMapping(value="/{accountId}")
    public ResponseEntity<?> getOneAccount(@PathVariable("accountId") String id) {
        return Optional.ofNullable(ar.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountInput account)  {
        Account account2Save = new Account(
                UUID.randomUUID().toString(),
                account.getAmount(),
                account.getFirstname(),
                account.getLastname(),
                account.getBirthdate(),
                account.getCountry(),
                account.getPassportnumber(),
                account.getPhonenumber(),
                account.getSecret(),
                account.getIban()
        );
        Account saved = ar.save(account2Save);
        URI location = linkTo(AccountRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // DELETE
    @DeleteMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> deleteAccount(@PathVariable("accountId") String accountId) {
        Optional<Account> account = ar.findById(accountId);
        if (account.isPresent()) {
            ar.delete(account.get());
        }
        return ResponseEntity.noContent().build();
    }

    // PUT
    @PutMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccount(@RequestBody Account account,
                                               @PathVariable("accountId") String accountId) {
        Optional<Account> body = Optional.ofNullable(account);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!ar.existsById(accountId)) {
            return ResponseEntity.notFound().build();
        }
        account.setId(accountId);
        Account result = ar.save(account);
        return ResponseEntity.ok().build();
    }

    // PATCH
    @PatchMapping(value = "/{accountId}")
    @Transactional
    public ResponseEntity<?> updateAccountPartiel(@PathVariable("accountId") String accountId,
                                                      @RequestBody Map<Object, Object> fields) {
        Optional<Account> body = ar.findById(accountId);
        if (body.isPresent()) {
            Account account = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Account.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, account, v);
            });
            validator.validate(new AccountInput(account.getAmount(), account.getFirstname(),
                    account.getLastname(), account.getBirthdate(), account.getCountry(), account.getPassportnumber(), account.getPhonenumber(), account.getSecret(), account.getIban()));
            account.setId(accountId);
            ar.save(account);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
