package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.boundaries.AccountRepresentation;
import fr.ul.miage.bank.entities.Card;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BankCardAssembler implements RepresentationModelAssembler<Card, EntityModel<Card>> {
    @Override
    public EntityModel<Card> toModel(Card card) {
        return EntityModel.of(card,
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneCardOneAccount(card.getAccount().getId(), card.getNumber())).withSelfRel(),
                linkTo(methodOn(AccountRepresentation.class)
                        .getAllOperationsOneCard(card.getAccount().getId(), card.getNumber())).withRel("Operations list"));
    }
}
