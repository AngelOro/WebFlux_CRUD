package com.example.demo.controller;

import com.example.demo.model.Card;
import com.example.demo.model.Person;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.service.CardService;
import com.example.demo.service.PersonService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = CardController.class)
class CardControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @SpyBean
    private CardService personService;

    @Captor
    private ArgumentCaptor<Mono<Card>> argumentCaptor;

    @MockBean
    private CardRepository repository;

    @ParameterizedTest
    @CsvSource({"FULANO, 05/26, 82194289390, MasterCard, 06, 0",
            "FULANO, 05/26, 82194289390, MasterCard, 06, 1"})
    void post( String tittle, String date,String number, String type, String code, Integer times) {

        if(times == 0) {
            when(repository.findByName(number)).thenReturn(Mono.just(new Card()));
        }

        if(times == 1) {
            when(repository.findByName(number)).thenReturn(Mono.empty());
        }

        var request = Mono.just(new Card(tittle, date, number, type, code));
        webTestClient.post()
                .uri("/card")
                .body(request, Card.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(personService).insert(argumentCaptor.capture());
        verify(repository, times(times)).save(any());

        var card = argumentCaptor.getValue().block();

        Assertions.assertEquals(number, card.getNumber());
        Assertions.assertEquals(tittle, card.getTitle());
        Assertions.assertEquals(date, card.getDate());
        Assertions.assertEquals(type, card.getType());
        Assertions.assertEquals(code, card.getCode());
    }

    @Test
    void get() {
        webTestClient.get()
                .uri("/card/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Card.class)
                .consumeWith(cardEntityExchangeResult -> {
                    var card = cardEntityExchangeResult.getResponseBody();
                    assert card != null;
                });
    }

    @Test
    void update() {
        var request = Mono.just(new Card());
        webTestClient.put()
                .uri("/card")
                .body(request, Card.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

}