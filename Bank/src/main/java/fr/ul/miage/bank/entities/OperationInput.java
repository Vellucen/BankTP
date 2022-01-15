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

    @Size(min = 5, max = 50)
    private String wording;
    @Size(max = 20)
    private String category;
    @Positive
    private Double amount;
    private Date date;
    @NotNull
    @Size(min = 27, max = 27)
    @Pattern(regexp = "FR55[0-9]{23}")
    private String ibancreditor;
    @Size(min = 2, max = 2)
    @Pattern(regexp = "[A-Z]{2}")
    private String country;
}
