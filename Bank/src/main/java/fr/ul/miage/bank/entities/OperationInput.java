package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    private Account account;
    private Card card;
    @Size(min = 5, max = 50)
    private String wording;
    @Size(min = 5, max = 20)
    private String category;
    @Positive
    private Double amount;
    @Positive
    private Double rate;
    @Past
    private Date date;
    private String shop;
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[A-Z]{2}")
    private String country;
}
