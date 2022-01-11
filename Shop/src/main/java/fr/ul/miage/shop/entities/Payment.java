package fr.ul.miage.shop.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Payment implements Serializable {

    @Id
    private String id;
    private String wording;
    private String category;
    private Double amount;
    private Double rate;
    private Date date;
    private String ibancreditor;
    private String country;

    public Payment(Double amount){
        this.id = UUID.randomUUID().toString();
        this.wording = "Achat chez ShopyShop";
        this.category = "Loisir";
        this.amount = amount;
        this.rate = 1.00;
        this.date = new Date();
        this.ibancreditor = "FR5571264259886310520048352";
        this.country = "FR";

    }

    public String toJson(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "{" +
                "\"wording\": \"" + this.wording + "\"," +
                "\"category\": \"" + this.category + "\"," +
                "\"amount\": " + this.amount + "," +
                "\"rate\": " + this.rate + "," +
                "\"date\": \"" + sdf.format(this.date) + "\"," +
                "\"ibancreditor\": \"" + this.ibancreditor + "\"," +
                "\"country\": \"" + this.country + "\"" +
                "}";
    }
}
