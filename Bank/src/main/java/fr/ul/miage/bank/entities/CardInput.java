package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInput {

    @Size(min = 4, max = 4)
    @Pattern(regexp = "[0-9]+")
    private String code;
    @Positive
    private Double cap;
}
