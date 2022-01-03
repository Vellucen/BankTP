package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    @Id
    private String id;
    @OneToMany
    private List<Card> cards;
    @OneToMany
    private List<Operation> operations;
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
