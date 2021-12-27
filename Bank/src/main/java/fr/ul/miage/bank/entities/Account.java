package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    private String id;
    private Double amount;
    private String firstname;
    private String lastname;
    private Date birthdate;
    private String country;
    private String passportnumber;
    private String phonenumber;
    private String secret;
    private String iban;
}
