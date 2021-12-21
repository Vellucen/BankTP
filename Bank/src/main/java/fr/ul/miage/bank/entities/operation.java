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
public class operation implements Serializable {

    @Id
    private String id;
    private String wording;
    private String category;
    private Double amount;
    private Double rate;
    private Date date;
    private String creditoraccount;
    private String country;
}
