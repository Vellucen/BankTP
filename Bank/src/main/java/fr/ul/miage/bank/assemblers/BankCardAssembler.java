package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.boundaries.CardRepresentation;
import fr.ul.miage.bank.entities.Card;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BankCardAssembler implements RepresentationModelAssembler<Card, EntityModel<Card>> {
    @Override
    public EntityModel<Card> toModel(Card card) {
        return EntityModel.of(card,
                linkTo(methodOn(CardRepresentation.class)
                        .getOneCard(card.getId())).withSelfRel(),
                linkTo(methodOn(CardRepresentation.class)
                        .getAllCards()).withRel("collection"));
    }

    @Override
    public CollectionModel<EntityModel<Card>> toCollectionModel(Iterable<? extends Card> entities) {
        List<EntityModel<Card>> cardModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(cardModel,
                linkTo(methodOn(CardRepresentation.class)
                        .getAllCards()).withSelfRel());
    }
}
