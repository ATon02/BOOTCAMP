package co.com.backend.reactive.usecase.bootcamp;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface IBootcampUseCase {
    Mono<Bootcamp> save(Bootcamp bootcamp);
}
