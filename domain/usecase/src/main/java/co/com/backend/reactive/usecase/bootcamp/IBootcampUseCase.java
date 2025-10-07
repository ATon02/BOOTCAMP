package co.com.backend.reactive.usecase.bootcamp;

import java.util.List;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.usecase.bootcamp.dto.BootcampCompletedResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface IBootcampUseCase {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Flux<BootcampCompletedResponse> getAllBootcampWithCapacities(int page, int size, String sortBy, String sortDirection);
    Mono<Void> deleteBootcamp(Long id);
    Flux<BootcampCompletedResponse> getBootcampWithCapacitiesByIds(List<Long> ids);
    Mono<BootcampCompletedResponse> getBootcampById(Long id);

    
}
