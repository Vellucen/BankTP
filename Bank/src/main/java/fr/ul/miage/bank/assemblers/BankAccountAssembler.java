package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.boundaries.AccountRepresentation;
import fr.ul.miage.bank.entities.Account;
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
public class BankAccountAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {
    @Override
    public EntityModel<Account> toModel(Account account) {
        return EntityModel.of(account,
                linkTo(methodOn(AccountRepresentation.class)
                        .getOneAccount(account.getId())).withSelfRel(),
                linkTo(methodOn(AccountRepresentation.class)
                        .getAllAccounts()).withRel("collection"));
    }

    @Override
    public CollectionModel<EntityModel<Account>> toCollectionModel(Iterable<? extends Account> entities) {
        List<EntityModel<Account>> accountModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(i -> toModel(i))
                .collect(Collectors.toList());
        return CollectionModel.of(accountModel,
                linkTo(methodOn(AccountRepresentation.class)
                        .getAllAccounts()).withSelfRel());
    }
}
