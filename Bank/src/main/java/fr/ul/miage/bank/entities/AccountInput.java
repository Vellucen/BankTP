package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {

    @Size(min = 2)
    @Pattern(regexp = "[A-Z][0-9]+")
    private String firstname;
    @Size(min = 2)
    @Pattern(regexp = "[A-Z][0-9]+")
    private String lastname;
    @Past
    private Date birthdate;
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[A-Z]{2}")
    private String country;
    @Size(min = 9, max = 9)
    @Pattern(regexp = "[0-9]{2}[A-Z]{2}[0-9]{5}")
    private String passportnumber;
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    private String phonenumber;
    private String secret;
    @Size(min = 27, max = 27)
    @Pattern(regexp = "[A-Z]{2}[0-9]+")
    private String iban;
}
