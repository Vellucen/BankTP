package fr.ul.miage.bank.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operation implements Serializable {

    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "id_card")
    private Card card;
    private String wording;
    private String category;
    private Double amount;
    private Double rate;
    private Date date;
    private String ibancreditor;
    private String country;

    public Operation(String id, Account account, Card card, String wording, String category, Double amount, Date date, String ibancreditor, String country) {
        this.id = id;
        this.account = account;
        this.card = card;
        this.wording = wording;
        this.category = category;
        this.amount = amount;
        this.rate = calculRate(account.getCountry(), country);
        this.date = date;
        this.ibancreditor = ibancreditor;
        this.country = country;
    }

    //€ -> $ = 1.14 | $ -> € = 0.88 | € -> £ = 0.83 | £ -> € = 1.2 | £ -> $ = 1.37 | $ -> £ = 0.73
    private Double calculRate(String source, String target){
        if (source.equals("FR") && target.equals("US")){
            return 1.14;
        }
        else if (source.equals("FR") && target.equals("UK")){
            return 0.83;
        }
        else if (source.equals("US") && target.equals("UK")){
            return 0.73;
        }
        else if (source.equals("US") && target.equals("FR")){
            return 0.88;
        }
        else if (source.equals("UK") && target.equals("US")){
            return 1.37;
        }
        else if (source.equals("UK") && target.equals("FR")){
            return 1.20;
        }
        else {
            return 1.00;
        }
    }
}
