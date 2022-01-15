package fr.ul.miage.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ul.miage.bank.boundaries.AccountResource;
import fr.ul.miage.bank.boundaries.CardResource;
import fr.ul.miage.bank.boundaries.OperationResource;
import fr.ul.miage.bank.entities.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankServiceApplicationTests {
    @LocalServerPort
    int port;

    @Autowired
    AccountResource ar;
    @Autowired
    CardResource cr;
    @Autowired
    OperationResource or;

    @BeforeEach
    public void setupContext(){
        RestAssured.port = port;
    }

    @SneakyThrows
    @Test
    public void getOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Response response = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.asString();
        assertThat(jsonAsString, containsString("John"));
    }

    @SneakyThrows
    @Test
    public void getAmountOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Response response = when().get("/accounts/"+a1.getId()+"/amount").then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, equalTo("164.19"));
    }

    @SneakyThrows
    @Test
    public void getOneCardOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111222233334444",date2, "0000","000",500.00,false,false,false, false);
        cr.save(c1);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("1111222233334444"));
    }

    @SneakyThrows
    @Test
    public void getAllCardsOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        Card c2 = new Card(UUID.randomUUID().toString(),a1,"2222222222222222",date2, "2222","222",600.00,false,false,false, false);
        cr.save(c2);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/").then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("1111111111111111"));
        assertThat(jsonAsString, containsString("2222222222222222"));
    }

    @SneakyThrows
    @Test
    public void getOneOperationOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-11");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,null, "TestOperation", "Test",55.55,1.00,date2,"FR5511111111111111111111111","FR");
        or.save(o1);
        Response response = when().get("/accounts/"+a1.getId()+"/operations/"+o1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("TestOperation"));
    }

    @SneakyThrows
    @Test
    public void getAllOperationsOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-11");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,null, "TestOperation1", "Test",55.55,1.00,date2,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,null, "TestOperation2", "Test",43.18,1.00,date2,"FR5511111111111111111111111","FR");
        or.save(o2);
        Response response = when().get("/accounts/"+a1.getId()+"/operations").then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("TestOperation1"));
        assertThat(jsonAsString, containsString("TestOperation2"));
    }

    @SneakyThrows
    @Test//avec params
    public void getAllOperationsOneAccountWithParamsTest(){
        String param1Category = "Test1";
        String param2Shop = "";
        String param3Country = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-11");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,null, "TestOperation1", "Test1",55.55,1.00,date2,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,null, "TestOperation2", "Test2",43.18,1.00,date2,"FR5511111111111111111111111","FR");
        or.save(o2);
        Response response = when().get("/accounts/"+a1.getId()+"/operations?category="+param1Category+"&shop="+param2Shop+"&country="+param3Country).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("TestOperation1"));
        assertThat(jsonAsString, not(containsString("TestOperation2")));
    }

    @SneakyThrows
    @Test
    public void getOneOperationOneCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-11");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation", "Test",55.55,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/operations/"+o1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("TestOperation"));
    }

    @SneakyThrows
    @Test
    public void getAllOperationsOneCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-11");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation1", "Test",55.55,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation2", "Test",43.18,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o2);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/operations").then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("TestOperation1"));
        assertThat(jsonAsString, containsString("TestOperation2"));
    }




    @SneakyThrows
    @Test
    public void updateAmountOfAccountTest(){
        Double addedAmount = 10.00;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),55.55,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        when().put("/accounts/"+a1.getId()+"/amount/"+addedAmount).then().statusCode(HttpStatus.SC_OK);
        Response response = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("65.55"));
    }

    @SneakyThrows
    @Test
    public void updateCapOfCardTest(){//only works if launched alone else HttpStatus = 500 and not 200
        Double newCap = 700.00;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),55.55,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111222233334444",date2, "0000","000",500.00,false,false,false, false);
        cr.save(c1);
        when().put("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/cap/"+newCap).then().statusCode(HttpStatus.SC_OK);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("700.0"));
    }

    @SneakyThrows
    @Test
    public void updateBlockedOfCardTest(){//only works if launched alone else HttpStatus = 500 and not 200
        char quotationMarks = '"';
        String expectedString = quotationMarks + "blocked" +quotationMarks +" : true";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),55.55,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111222233334444",date2, "0000","000",500.00,false,false,false, false);
        cr.save(c1);
        when().put("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/blocked/").then().statusCode(HttpStatus.SC_OK);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString(expectedString));
    }

    @SneakyThrows
    @Test
    public void updateLocationOfCardTest(){//only works if launched alone else HttpStatus = 500 and not 200
        char quotationMarks = '"';
        String expectedString = quotationMarks + "location" +quotationMarks +" : true";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),55.55,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111222233334444",date2, "0000","000",500.00,false,false,false, false);
        cr.save(c1);
        when().put("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/location/").then().statusCode(HttpStatus.SC_OK);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString(expectedString));
    }

    @SneakyThrows
    @Test
    public void updateContactlessOfCardTest(){//only works if launched alone else HttpStatus = 500 and not 200
        char quotationMarks = '"';
        String expectedString = quotationMarks + "contactless" +quotationMarks +" : true";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),55.55,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111222233334444",date2, "0000","000",500.00,false,false,false, false);
        cr.save(c1);
        when().put("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()+"/contactless/").then().statusCode(HttpStatus.SC_OK);
        Response response = when().get("/accounts/"+a1.getId()+"/cards/"+c1.getNumber()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString(expectedString));
    }




    @SneakyThrows
    @Test
    public void saveAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        AccountInput a1 = new AccountInput("John", "Doe", date1, "FR", "12AB34567", "0600000000");
        Response response = given().body(this.toJsonString(a1))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void saveCardOneAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        CardInput c1 = new CardInput("5555",500.00, false);
        Response response = given().body(this.toJsonString(c1))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+a1.getId()+"/cards")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void transferOperationTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date2,"FR5599999999999999999999999","UK");
        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+a1.getId()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void transferOperationBalanceAmountsAccountsTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date2,"FR5599999999999999999999999","UK");

        given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+a1.getId()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        Response response1 = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString1 = response1.body().asString();
        assertThat(jsonAsString1, containsString("152.19"));
        Response response2 = when().get("/accounts/"+a2.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString2 = response2.body().asString();
        assertThat(jsonAsString2, containsString("450.82"));
    }

    @SneakyThrows
    @Test
    public void transferOperationInsufficientAmountAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),110.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date2,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/"+a1.getId()+"/operations")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Insufficient account amount"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeBalanceAmountsAccountsTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString1 = response1.body().asString();
        assertThat(jsonAsString1, containsString("152.19"));
        Response response2 = when().get("/accounts/"+a2.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString2 = response2.body().asString();
        assertThat(jsonAsString2, containsString("450.82"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeBadCodeTest(){
        String badCode = "5555";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ badCode)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Bad code"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeExpiredCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2020-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Expired card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeBlockedCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,true,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Blocked card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeBadLocationTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,true,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Bad location"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeVirtualCarsNotAllowedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, true);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Virtual card not allowed"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeInsufficientAmountAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),50.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Insufficient account amount"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseCodeCapReachedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),500.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),120.00,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",120.00,false,false,false, false);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation1", "Test",60.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation2", "Test",40.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o2);
        OperationInput o3 = new OperationInput("TestOperation3","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o3))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/code/"+ c1.getCode())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Cap reached"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessBalanceAmountsAccountsTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString1 = response1.body().asString();
        assertThat(jsonAsString1, containsString("152.19"));
        Response response2 = when().get("/accounts/"+a2.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString2 = response2.body().asString();
        assertThat(jsonAsString2, containsString("450.82"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessContactlessNotActivatedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Contactless not activated"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessExpiredCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2020-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Expired card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessBlockedCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,true,false,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Blocked card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessBadLocationTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,true,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Bad location"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessVirtualCarsNotAllowedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,true, true);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Virtual card not allowed"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessInsufficientAmountAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),50.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,true, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Insufficient account amount"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardUseContactlessCapReachedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),500.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),120.00,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",120.00,false,false,true, false);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation1", "Test",60.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation2", "Test",40.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o2);
        OperationInput o3 = new OperationInput("TestOperation3","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o3))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/contactless")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Cap reached"));
    }







    @SneakyThrows
    @Test
    public void paymentByCardOnlineTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        String location = response.getHeader("Location");
        when().get(location).then().statusCode(HttpStatus.SC_OK);
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineBalanceAmountsAccountsTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),164.19,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",10.00,date3,"FR5599999999999999999999999","UK");
        given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .response();

        Response response1 = when().get("/accounts/"+a1.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString1 = response1.body().asString();
        assertThat(jsonAsString1, containsString("152.19"));
        Response response2 = when().get("/accounts/"+a2.getId()).then().statusCode(HttpStatus.SC_OK).extract().response();
        String jsonAsString2 = response2.body().asString();
        assertThat(jsonAsString2, containsString("450.82"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineBadCryptogramTest(){
        String badCryptogram = "555";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ badCryptogram)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Bad cryptogram"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineExpiredCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2020-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Expired card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineBlockedCardTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,true,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Blocked card"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineBadLocationTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,true,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Bad location"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineVirtualCarsTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2022-03-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),200.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, true);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation1", "Test",30.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        OperationInput o2 = new OperationInput("TestOperation2","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o2))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Virtual card expired"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineInsufficientAmountAccountTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),50.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),438.82,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",500.00,false,false,false, false);
        cr.save(c1);
        OperationInput o1 = new OperationInput("TestOperation","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o1))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Insufficient account amount"));
    }

    @SneakyThrows
    @Test
    public void paymentByCardOnlineCapReachedTest(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = formatter.parse("1978-05-19");
        Date date2 = formatter.parse("2023-05-19");
        Date date3 = formatter.parse("2022-01-12");
        Account a1 = new Account(UUID.randomUUID().toString(),500.00,"John","Doe", date1,"FR","12AB34567","0600000000","FR5500000000000000000000000");
        ar.save(a1);
        Account a2 = new Account(UUID.randomUUID().toString(),120.00,"Jane","Doe", date1,"UK","34BZ43185","0600000000","FR5599999999999999999999999");
        ar.save(a2);
        Card c1 = new Card(UUID.randomUUID().toString(),a1,"1111111111111111",date2, "1111","111",120.00,false,false,false, false);
        cr.save(c1);
        Operation o1 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation1", "Test",60.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o1);
        Operation o2 = new Operation(UUID.randomUUID().toString(),a1,c1, "TestOperation2", "Test",40.00,1.00,date3,"FR5511111111111111111111111","FR");
        or.save(o2);
        OperationInput o3 = new OperationInput("TestOperation3","Test",100.00,date3,"FR5599999999999999999999999","UK");

        Response response = given().body(this.toJsonString(o3))
                .contentType(ContentType.JSON)
                .when()
                .post("/payment/"+ c1.getNumber() +"/online/"+ c1.getCryptogram())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response();

        String jsonAsString = response.body().asString();
        assertThat(jsonAsString, containsString("Cap reached"));
    }

    private String toJsonString(Object o) throws Exception{
        ObjectMapper map = new ObjectMapper();
        return  map.writeValueAsString(o);

    }

}
