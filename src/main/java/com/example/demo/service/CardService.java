package com.example.demo.service;

import com.example.demo.model.Card;
import com.example.demo.model.Person;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Service
public class CardService {
    private final BiFunction<CardRepository, Card, Mono<Card>> validateBeforeInsert
            = (repo, card) -> repo.findByName(card.getNumber());
    @Autowired
    private CardRepository repository;

    public Flux<Card> listAll() {
        return repository.findAll();
    }

    public Mono<Void> insert(Mono<Card> cardMono) {
        return cardMono
                .flatMap(card -> validateBeforeInsert.apply(repository, card))
                .switchIfEmpty(Mono.defer(() -> cardMono.doOnNext(repository::save)))
                .then();
    }
}
