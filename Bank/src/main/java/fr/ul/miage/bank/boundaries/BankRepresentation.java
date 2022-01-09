package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.entities.OperationInput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/payment", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankRepresentation {

    private final CardResource cards;
    private final OperationRepresentation operations;

    public BankRepresentation(CardResource cards, OperationRepresentation operations) {
        this.cards = cards;
        this.operations = operations;
    }

    //POST one OPERATION (payment by card in shop use code)
    @PostMapping(value = "/{cardNum}/code/{cardCode}")
    @Transactional
    public ResponseEntity<?> paymentByCardUseCode (@PathVariable("cardNum") String numCard, @PathVariable("cardCode") String codeCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentByCode(idAccount, numCard, codeCard, operation);
    }

    //POST one OPERATION (payment by card use contactless)
    @PostMapping(value = "/{cardNum}/contactless")
    @Transactional
    public ResponseEntity<?> paymentByCardUseContactless (@PathVariable("cardNum") String numCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentUseContactless(idAccount, numCard, operation);
    }

    //POST one OPERATION (payment by card online)
    @PostMapping(value = "/{cardNum}/online/{cardCrypto}")
    @Transactional
    public ResponseEntity<?> paymentByCardOnline(@PathVariable("cardNum") String numCard, @PathVariable("cardCrypto") String cryptoCard, @RequestBody @Valid OperationInput operation) {
        String idAccount = cards.findByNumber(numCard).get().getAccount().getId();
        return operations.paymentOnline(idAccount, numCard, cryptoCard, operation);
    }
}
