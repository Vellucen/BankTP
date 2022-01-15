package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.OperationInput;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankRepresentation {

    private final AccountResource account;
    private final CardResource cards;
    private final OperationRepresentation operations;

    public BankRepresentation(AccountResource account, CardResource cards, OperationRepresentation operations) {
        this.account = account;
        this.cards = cards;
        this.operations = operations;
    }

    //SIGN IN
    @SneakyThrows
    @GetMapping(value = "sign_in")
    public ResponseEntity<?> signIn (@RequestParam String firsname, @RequestParam String lastname) {
        String grant_type = "passworld";
        String client_id = "bankWEB";
        if (account.existsByFirstnameAndLastname(firsname, lastname)) {
            String idAccount = account.findByFirstnameAndLastname(firsname, lastname).getId();
            String accountURL = "/accounts/"+idAccount;
            return new ResponseEntity<>(accountURL, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //POST one OPERATION (payment by card in shop use code)
    @PostMapping(value = "payment/{cardNum}/code/{cardCode}")
    @Transactional
    public ResponseEntity<?> paymentByCardUseCode (@PathVariable("cardNum") String numCard, @PathVariable("cardCode") String codeCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentByCode(idAccount, numCard, codeCard, operation);
    }

    //POST one OPERATION (payment by card use contactless)
    @PostMapping(value = "payment/{cardNum}/contactless")
    @Transactional
    public ResponseEntity<?> paymentByCardUseContactless (@PathVariable("cardNum") String numCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentUseContactless(idAccount, numCard, operation);
    }

    //POST one OPERATION (payment by card online)
    @PostMapping(value = "payment/{cardNum}/online/{cardCrypto}")
    @Transactional
    public ResponseEntity<?> paymentByCardOnline(@PathVariable("cardNum") String numCard, @PathVariable("cardCrypto") String cryptoCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentOnline(idAccount, numCard, cryptoCard, operation);
    }
}
