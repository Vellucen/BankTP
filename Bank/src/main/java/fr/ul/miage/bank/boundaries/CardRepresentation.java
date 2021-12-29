package fr.ul.miage.bank.boundaries;

import fr.ul.miage.bank.assemblers.BankCardAssembler;
import fr.ul.miage.bank.entities.Card;
import fr.ul.miage.bank.entities.CardInput;
import fr.ul.miage.bank.entities.CardValidator;
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
@RequestMapping(value="/cards", produces = MediaType.APPLICATION_JSON_VALUE)
@ExposesResourceFor(Card.class)
public class CardRepresentation {

    private final CardResource cr;
    private final BankCardAssembler assembler;
    private final CardValidator validator;

    public CardRepresentation(CardResource cr, BankCardAssembler assembler, CardValidator validator) {
        this.cr = cr;
        this.assembler = assembler;
        this.validator = validator;
    }

    // GET all
    @GetMapping
    public ResponseEntity<?> getAllCards() {
        return ResponseEntity.ok(assembler.toCollectionModel(cr.findAll()));
    }

    // GET one
    @GetMapping(value="/{cardId}")
    public ResponseEntity<?> getOneCard(@PathVariable("cardId") String id) {
        return Optional.ofNullable(cr.findById(id)).filter(Optional::isPresent)
                .map(i -> ResponseEntity.ok(assembler.toModel(i.get())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> saveCard(@RequestBody @Valid CardInput card)  {
        Card card2Save = new Card(
                UUID.randomUUID().toString(),
                card.getAccount(),
                card.getNumber(),
                card.getCode(),
                card.getCryptogram(),
                card.getCap(),
                card.isBlocked(),
                card.isLocation(),
                card.isContactless(),
                card.isVirtual()
        );
        Card saved = cr.save(card2Save);
        URI location = linkTo(CardRepresentation.class).slash(saved.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    // DELETE
    @DeleteMapping(value = "/{cardId}")
    @Transactional
    public ResponseEntity<?> deleteCard(@PathVariable("cardId") String cardId) {
        Optional<Card> card = cr.findById(cardId);
        if (card.isPresent()) {
            cr.delete(card.get());
        }
        return ResponseEntity.noContent().build();
    }

    // PUT
    @PutMapping(value = "/{cardId}")
    @Transactional
    public ResponseEntity<?> updateCard(@RequestBody Card card,
                                           @PathVariable("cardId") String cardId) {
        Optional<Card> body = Optional.ofNullable(card);
        if (!body.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        if (!cr.existsById(cardId)) {
            return ResponseEntity.notFound().build();
        }
        card.setId(cardId);
        Card result = cr.save(card);
        return ResponseEntity.ok().build();
    }

    // PATCH
    @PatchMapping(value = "/{cardId}")
    @Transactional
    public ResponseEntity<?> updateCardPartiel(@PathVariable("cardId") String cardId,
                                                  @RequestBody Map<Object, Object> fields) {
        Optional<Card> body = cr.findById(cardId);
        if (body.isPresent()) {
            Card card = body.get();
            fields.forEach((f, v) -> {
                Field field = ReflectionUtils.findField(Card.class, f.toString());
                field.setAccessible(true);
                ReflectionUtils.setField(field, card, v);
            });
            validator.validate(new CardInput(card.getAccount(), card.getNumber(), card.getCode(), card.getCryptogram(),
                    card.getCap(), card.isBlocked(), card.isLocation(), card.isContactless(), card.isVirtual()));
            card.setId(cardId);
            cr.save(card);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
