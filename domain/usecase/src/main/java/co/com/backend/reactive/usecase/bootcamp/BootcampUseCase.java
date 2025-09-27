package co.com.backend.reactive.usecase.bootcamp;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
public class BootcampUseCase implements IBootcampUseCase {

    //private final BootcampRepository bootcampRepository;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return null;
    }

}
