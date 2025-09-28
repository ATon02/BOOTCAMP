package co.com.backend.reactive.model.bootcampcapacity.gateways;

import reactor.core.publisher.Mono;
import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;

public interface BootcampCapacityRepository {
    Mono<BootcampCapacity> save(BootcampCapacity bootcampCapacity);
}
