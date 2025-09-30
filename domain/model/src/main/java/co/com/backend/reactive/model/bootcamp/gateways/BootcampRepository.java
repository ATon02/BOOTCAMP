package co.com.backend.reactive.model.bootcamp.gateways;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


public interface BootcampRepository {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Bootcamp> findById(Long id);
    Flux<Bootcamp> findAllPaginated(int page, int size, String sortBy, String sortDirection);
    Mono<Void> deleteById(Long id);

}
