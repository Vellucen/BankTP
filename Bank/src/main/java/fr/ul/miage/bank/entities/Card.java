package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card implements Serializable {

    @Id
    private String id;
    private String number;
    private String code;
    private String cryptogram;
    private Double cap;
    private boolean blocked;
    private boolean location;
    private boolean contactless;
    private boolean virtual;
}
