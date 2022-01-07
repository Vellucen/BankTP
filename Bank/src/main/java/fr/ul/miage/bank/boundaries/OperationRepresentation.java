package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankOperationAssembler;
import fr.ul.miage.bank.entities.*;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;

@RestController
@RequestMapping(value="/operations", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Operation.class)
public class OperationRepresentation {

    private final OperationResource or;
    private final AccountResource ar;
    private final CardResource cr;
    private final BankOperationAssembler assembler;
    private final OperationValidator validator;

    public OperationRepresentation(OperationResource or, AccountResource ar, CardResource cr, BankOperationAssembler assembler, OperationValidator validator) {
        this.or = or;
        this.ar = ar;
        this.cr = cr;
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

    //POST one OPERATION of one ACCOUNT (transfer)
    public ResponseEntity<?> transfer(String idAccount, @RequestBody @Valid OperationInput operation)  {
        ResponseEntity<?> response = checkingTransfer(idAccount, operation);
        if (response.getStatusCode() == HttpStatus.OK) {
            Operation operation2Save = new Operation(
                    UUID.randomUUID().toString(),
                    ar.getById(idAccount),
                    null,
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
        else {
            return response;
        }
    }

    //POST one OPERATION of one CARD (payment in shop by code)
    public ResponseEntity<?> paymentByCode(String idAccount, String numCard, @RequestBody @Valid OperationInput operation)  {
        ResponseEntity<?> response = checkingPaymentByCode(idAccount, numCard, operation);
        if (response.getStatusCode() == HttpStatus.OK) {
            Operation operation2Save = new Operation(
                    UUID.randomUUID().toString(),
                    ar.getById(idAccount),
                    cr.findByNumber(numCard).get(),
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
        else {
            return response;
        }
    }

    //POST one OPERATION of one CARD (payment in shop use contactless)
    public ResponseEntity<?> paymentUseContactless(String idAccount, String numCard, @RequestBody @Valid OperationInput operation)  {
        ResponseEntity<?> response = checkingPaymentUseContactless(idAccount, numCard, operation);
        if (response.getStatusCode() == HttpStatus.OK) {
            Operation operation2Save = new Operation(
                    UUID.randomUUID().toString(),
                    ar.getById(idAccount),
                    cr.findByNumber(numCard).get(),
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
        else {
            return response;
        }
    }

    //POST one OPERATION of one CARD (payment online)
    public ResponseEntity<?> paymentOnline(String idAccount, String numCard, @RequestBody @Valid OperationInput operation)  {
        ResponseEntity<?> response = checkingPaymentOnline(idAccount, numCard, operation);
        if (response.getStatusCode() == HttpStatus.OK) {
            Operation operation2Save = new Operation(
                    UUID.randomUUID().toString(),
                    ar.getById(idAccount),
                    cr.findByNumber(numCard).get(),
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
        else {
            return response;
        }
    }

    private ResponseEntity<?> checkingTransfer(String idAccount, @Valid OperationInput operation){
        Account account = ar.findById(idAccount).get();
        //amount verification
        if (account.getAmount() >= operation.getAmount()){
            return new ResponseEntity<>("Validated operation", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Insufficient account amount", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> checkingPaymentByCode(String idAccount, String numCard, @Valid OperationInput operation){
        Account account = ar.getById(idAccount);
        Card card = cr.findByNumber(numCard).get();
        //code verification
        if (card.getCode().equals(operation.getCard().getCode())){
            return PaymentInShopVerification(account, card, operation);
        }
        else {
            return new ResponseEntity<>("Bad code", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> checkingPaymentUseContactless(String idAccount, String numCard, @Valid OperationInput operation){
        Account account = ar.getById(idAccount);
        Card card = cr.findByNumber(numCard).get();
        //contactless verification
        if (operation.getCard().isContactless()){
            return PaymentInShopVerification(account, card, operation);
        }
        else {
            return new ResponseEntity<>("Contactless not activated", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> checkingPaymentOnline(String idAccount, String numCard, @Valid OperationInput operation){
        Account account = ar.getById(idAccount);
        Card card = cr.findByNumber(numCard).get();
        //cryptogram verification
        if (card.getCryptogram().equals(operation.getCard().getCryptogram())){
            //expiration date verification
            if (card.getExpiration().after(operation.getDate())){
                //blocked verification
                if (!card.isBlocked()) {
                    //location verification
                    if (locationVerif(account, card, operation)) {
                        //virtual verification
                        if (virtualVerif(account, card)) {
                            //amount verification
                            if (account.getAmount() >= operation.getAmount()) {
                                //cap verification
                                if ((card.getCap() - sumAmountOperation30Days(account, card)) >= operation.getAmount()) {
                                    return new ResponseEntity<>("Validated operation", HttpStatus.OK);
                                }
                                else {
                                    return new ResponseEntity<>("Cap reached", HttpStatus.BAD_REQUEST);
                                }
                            }
                            else {
                                return new ResponseEntity<>("Insufficient account amount", HttpStatus.BAD_REQUEST);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Virtual not activated", HttpStatus.BAD_REQUEST);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Bad location", HttpStatus.BAD_REQUEST);
                    }
                }
                else {
                    return new ResponseEntity<>("Blocked card", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<>("Expired card", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>("Validated operation", HttpStatus.OK);
        }
    }

    private ResponseEntity<?> PaymentInShopVerification(Account account, Card card, @Valid OperationInput operation) {
        //expiration date verification
        if (card.getExpiration().after(operation.getDate())){
            //blocked verification
            if (!card.isBlocked()) {
                //location verification
                if (locationVerif(account, card, operation)) {
                    //virtual verification
                    if (!card.isVirtual()) {
                        //amount verification
                        if (account.getAmount() >= operation.getAmount()) {
                            //cap verification
                            if ((card.getCap() - sumAmountOperation30Days(account, card)) >= operation.getAmount()) {
                                return new ResponseEntity<>("Validated operation", HttpStatus.OK);
                            }
                            else {
                                return new ResponseEntity<>("Cap reached", HttpStatus.BAD_REQUEST);
                            }
                        }
                        else {
                            return new ResponseEntity<>("Insufficient account amount", HttpStatus.BAD_REQUEST);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Virtual card not allowed", HttpStatus.BAD_REQUEST);
                    }
                }
                else {
                    return new ResponseEntity<>("Bad location", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<>("Blocked card", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>("Expired card", HttpStatus.BAD_REQUEST);
        }
    }

    private Boolean locationVerif(Account account, Card card, @Valid OperationInput operation) {
        if (card.isLocation()){
            if (account.getCountry().equals(operation.getCountry())){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    private Boolean virtualVerif(Account account, Card card) {
        if (card.isVirtual()){
            //verify if any operation is link to this virtual card
            if (or.findByAccount_IdAndCard_Number(account.getId(), card.getNumber()).size() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    private Double sumAmountOperation30Days(Account account, Card card) {
        ArrayList<Operation> operationsCard = new ArrayList<>(or.findByAccount_IdAndCard_Number(account.getId(), card.getNumber()));
        Double sumAmountOperations = 0.00;
        Date date = new Date();
        for (Operation op:operationsCard) {
            if ((date.getTime() - op.getDate().getTime())/(1000*60*60*24) <= 30) {
                sumAmountOperations += op.getAmount();
            }
        }
        return sumAmountOperations;
    }
}
