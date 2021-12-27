package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.entities.Operation;
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
public class BankAccountAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {
    @Override
    public EntityModel<Operation> toModel(Operation operation) {
        return EntityModel.of(operation,
                linkTo(methodOn(OperationRepresentation.class)
                        .getOneIntervenant(operation.getId())).withSelfRel(),
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllIntervenants()).withRel("collection"));
    }

    @Override
    public CollectionModel<EntityModel<Operation>> toCollectionModel(Iterable<? extends Operation> entities) {
        List<EntityModel<Operation>> intervenantModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(intervenantModel,
                linkTo(methodOn(OperationRepresentation.class)
                        .getAllIntervenants()).withSelfRel());
    }
}
