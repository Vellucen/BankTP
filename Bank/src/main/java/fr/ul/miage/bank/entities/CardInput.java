package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInput {

    private Account account;
    @Size(min = 16, max = 16)
    @Pattern(regexp = "[0-9]+")
    private String number;
    private Date expiration;
    @Size(min = 4, max = 4)
    @Pattern(regexp = "[0-9]+")
    private String code;
    @Size(min = 3, max = 3)
    @Pattern(regexp = "[0-9]+")
    private String cryptogram;
    private Double cap;
    private boolean blocked;
    private boolean location;
    private boolean contactless;
    private boolean virtual;
}
