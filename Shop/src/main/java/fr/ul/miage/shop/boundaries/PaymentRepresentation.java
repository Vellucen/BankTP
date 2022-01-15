package fr.ul.miage.shop.boundaries;

import fr.ul.miage.shop.entities.Payment;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value="/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentRepresentation {

    //POST one OPERATION (payment by card in shop use code)
    @PostMapping(value = "/{amount}/number/{cardNum}/code/{cardCode}")
    @Transactional
    public ResponseEntity<?> paymentByCardUseCode (@PathVariable("amount") Double amount, @PathVariable("cardNum") String numCard, @PathVariable("cardCode") String cardCode) {
        Payment payment = new Payment(amount);
        String postUrl = "http://localhost:8082/payment/" + numCard + "/code/" + cardCode;
        return sendPaymentToBank(postUrl, payment);
    }

    //POST one OPERATION (payment by card in shop use contactless)
    @PostMapping(value = "/{amount}/number/{cardNum}/contactless")
    @Transactional
    public ResponseEntity<?> paymentByCardUseContactless (@PathVariable("amount") Double amount, @PathVariable("cardNum") String numCard) {
        Payment payment = new Payment(amount);
        String postUrl = "http://localhost:8082/payment/" + numCard + "/contactless";
        return sendPaymentToBank(postUrl, payment);
    }

    //POST one OPERATION (payment by card in online shop use cryptogram)

    @PostMapping(value = "/{amount}/number/{cardNum}/online/{cardCrypto}")
    @Transactional
    public ResponseEntity<?> paymentByCardUseContactless (@PathVariable("amount") Double amount, @PathVariable("cardNum") String numCard, @PathVariable("cardCrypto") String cardCrypto) {
        Payment payment = new Payment(amount);
        String postUrl = "http://localhost:8082/payment/" + numCard + "/online/" + cardCrypto;
        return sendPaymentToBank(postUrl, payment);
    }

    @SneakyThrows
    private ResponseEntity<?> sendPaymentToBank(String postUrl, Payment payment) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        String json = payment.toJson();
        post.setEntity(new StringEntity(json));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(post);
        String reasonPhrase = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        return new ResponseEntity<>(reasonPhrase, HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
    }
}
