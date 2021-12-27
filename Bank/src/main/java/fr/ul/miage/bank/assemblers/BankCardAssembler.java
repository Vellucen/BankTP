package fr.ul.miage.bank.assemblers;

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
                        .getOneIntervenant(card.getId())).withSelfRel(),
                linkTo(methodOn(CardtRepresentation.class)
                        .getAllIntervenants()).withRel("collection"));
    }

    @Override
    public CollectionModel<EntityModel<Card>> toCollectionModel(Iterable<? extends Card> entities) {
        List<EntityModel<Card>> intervenantModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(intervenantModel,
                linkTo(methodOn(CardRepresentation.class)
                        .getAllIntervenants()).withSelfRel());
    }
}
