package co.com.backend.reactive.model.bootcamp.gateways;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
}
