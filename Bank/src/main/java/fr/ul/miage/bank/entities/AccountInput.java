package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    private List<Card> cards;
    private List<Operation> operations;
    private Double amount;
    private String firstname;
    private String lastname;
    private Date birthdate;
    @Size(min = 4)
    private String country;
    @NotNull
    @NotBlank
    private String passportnumber;
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    private String phonenumber;
    @Size(min = 6, max = 6)
    @Pattern(regexp = "[0-9]+")
    private String secret;
    private String iban;
}
