package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.boundaries.AccountRepresentation;
import fr.ul.miage.bank.entities.Operation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BankOperationAssembler implements RepresentationModelAssembler<Operation, EntityModel<Operation>> {
    @Override
    public EntityModel<Operation> toModel(Operation operation) {
        return EntityModel.of(operation,
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneOperationOneAccount(operation.getAccount().getId(), operation.getId())).withSelfRel());
    }

}
