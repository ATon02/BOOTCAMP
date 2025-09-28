package co.com.backend.reactive.model.capacitydata.gateways;

import java.util.List;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import co.com.backend.reactive.model.capacitydata.CapacityData;

public interface CapacityDataRepository {
    Mono<CapacityData> findById(Long id);
    Mono<Boolean> existsById(Long id);
    Flux<CapacityData> findByIds(List<Long> ids);
}
