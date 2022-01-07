package fr.ul.miage.bank.assemblers;

import fr.ul.miage.bank.boundaries.AccountRepresentation;
import fr.ul.miage.bank.entities.Account;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

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
                        .getAmountOneAccount(account.getId())).withRel("Amount account"),
                linkTo(methodOn(AccountRepresentation.class)
                        .getAllCardsOneAccount(account.getId())).withRel("Cards list"),
                linkTo(methodOn(AccountRepresentation.class)
                        .getAllOperationsOneAccount(account.getId(), "", "", "")).withRel("Operations list"));
    }
}
