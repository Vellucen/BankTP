package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card implements Serializable {

    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;
    @OneToMany
    private List<Operation> operations;
    private String number;
    private String code;
    private String cryptogram;
    private Double cap;
    private boolean blocked;
    private boolean location;
    private boolean contactless;
    private boolean virtual;
}
