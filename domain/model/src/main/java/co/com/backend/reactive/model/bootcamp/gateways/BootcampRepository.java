package co.com.backend.reactive.model.bootcamp.gateways;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Flux<Bootcamp> findAllPaginated(int page, int size, String sortBy, String sortDirection);

}
