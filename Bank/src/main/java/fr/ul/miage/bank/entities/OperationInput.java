package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationInput {

    @NotNull
    @Size(min = 5, max = 50)
    private String wording;
    @Size(min = 5, max = 20)
    private String category;
    @NotNull
    @Positive
    private Double amount;
    private Double rate;
    private Date date;
    @NotNull
    private String creditoraccount;
    @Size(min = 4)
    private String country;
}
