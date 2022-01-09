package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankCardAssembler;
import fr.ul.miage.bank.entities.Card;
import fr.ul.miage.bank.entities.CardInput;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.Boolean.FALSE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value="/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Card.class)
public class CardRepresentation {

    private final CardResource cr;
    private final AccountResource ar;
    private final BankCardAssembler assembler;
    private final OperationRepresentation operations;

    public CardRepresentation(CardResource cr, AccountResource ar, BankCardAssembler assembler, OperationRepresentation operations) {
        this.cr = cr;
        this.ar = ar;
        this.assembler = assembler;
        this.operations = operations;
    }

    // GET one CARD of one ACCOUNT
    public ResponseEntity<?> getOneCard(String idAccount, String numCard) {
        return Optional.ofNullable(cr.findByAccount_IdAndNumber(idAccount, numCard)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    //GET all CARDS of one ACCOUNT
    public ResponseEntity<?> getAllCardsOfOneAccount(String idAccount) {
        return ResponseEntity.ok(assembler.toCollectionModel(cr.findByAccount_Id(idAccount)));
    }

    // GET one OPERATION of one CARD
    public ResponseEntity<?> getOneOperation(String idAccount, String numCard, String idOperation) {
        return operations.getOneOperationOfOneCard(idAccount, numCard, idOperation);
    }

    //GET all OPERATIONS of one CARD
    public ResponseEntity<?> getAllOperations(String idAccount, String numCard) {
        return operations.getAllOperationsOfOneCard(idAccount, numCard);
    }

    //POST one CARD
    public ResponseEntity<?> saveCard(String idAccount, CardInput card)  {
        Date expirationDate = new Date();
        expirationDate.setYear(expirationDate.getYear()+4);
        Card card2Save = new Card(
                UUID.randomUUID().toString(),
                ar.getById(idAccount),
                stringNumericGenerator(16),
                expirationDate,
                card.getCode(),
                stringNumericGenerator(3),
                card.getCap(),
                FALSE,
                FALSE,
                FALSE,
                FALSE
        );
        Card saved = cr.save(card2Save);
        URI location = linkTo(CardRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    //PUT new cap Card
    public ResponseEntity<?> updateCapCard(String idAccount, String numCard, Double newCap) {
        Card card = cr.findByNumber(numCard).get();
        String idCard = card.getId();
        card.setCap(newCap);
        return updateCard(idCard, card);
    }

    //PUT change blocked Card
    public ResponseEntity<?> updateBlockedCard(String idAccount, String numCard) {
        Card card = cr.findByNumber(numCard).get();
        String idCard = card.getId();
        card.setBlocked(!card.isBlocked());
        return updateCard(idCard, card);
    }

    //PUT change location Card
    public ResponseEntity<?> updateLocationCard(String idAccount, String numCard) {
        Card card = cr.findByNumber(numCard).get();
        String idCard = card.getId();
        card.setLocation(!card.isLocation());
        return updateCard(idCard, card);
    }

    //PUT change contactless Card
    public ResponseEntity<?> updateContactlessCard(String idAccount, String numCard) {
        Card card = cr.findByNumber(numCard).get();
        String idCard = card.getId();
        card.setContactless(!card.isContactless());
        return updateCard(idCard, card);
    }

    //PUT change virtual Card
    public ResponseEntity<?> updateVirtualCard(String idAccount, String numCard) {
        Card card = cr.findByNumber(numCard).get();
        String idCard = card.getId();
        card.setVirtual(!card.isVirtual());
        return updateCard(idCard, card);
    }

    private ResponseEntity<?> updateCard(String idCard, Card card) {
        Optional<Card> body = Optional.ofNullable(card);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cr.existsById(idCard)) {
            return ResponseEntity.notFound().build();
        }
        card.setId(idCard);
        Card result = cr.save(card);
        return ResponseEntity.ok().build();
    }

    private String stringNumericGenerator (int i) {
        return RandomStringUtils.randomNumeric(i);
    }
}
