package com.example.demo.repository;

import com.example.demo.model.Card;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CardRepository extends ReactiveMongoRepository<Card, String> {
    Mono<Card> findByName(String number);
}