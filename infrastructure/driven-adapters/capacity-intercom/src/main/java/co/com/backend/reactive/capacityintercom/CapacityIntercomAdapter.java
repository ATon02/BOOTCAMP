package co.com.backend.reactive.capacityintercom;

import java.util.List;
import java.util.stream.Collectors;

import co.com.backend.reactive.model.capacitydata.gateways.CapacityDataRepository;
import co.com.backend.reactive.model.capacitydata.CapacityData;
import co.com.backend.reactive.model.tecnologydata.TecnologyData;
import co.com.backend.reactive.capacityintercom.dtos.CapacityIntercomResponse;
import co.com.backend.reactive.capacityintercom.dtos.CapacityBatchIntercomResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Component;


@Component
public class CapacityIntercomAdapter implements CapacityDataRepository {

    private final WebClient webClient;

    public CapacityIntercomAdapter(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public Mono<CapacityData> findById(Long id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/capacity/{id}")
                                             .build(id))
                .retrieve()
                .bodyToMono(CapacityIntercomResponse.class)
                .filter(response -> response.getStatus() == 200 && response.getData() != null)
                .map(CapacityIntercomResponse::getData)
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return findById(id)
                .map(capacity -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Flux<CapacityData> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        
        String idsParam = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/capacity/batch")
                                             .queryParam("ids", idsParam)
                                             .build())
                .retrieve()
                .bodyToMono(CapacityBatchIntercomResponse.class)
                .filter(response -> response.getStatus() == 200 && response.getData() != null)
                .flatMapMany(response -> Flux.fromIterable(response.getData()))
                .onErrorResume(e -> Flux.fromIterable(ids).flatMap(this::findById));
    }

    @Override
    public Mono<Void> deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.empty();
        }
        
        String idsParam = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/capacity")
                                             .queryParam("ids", idsParam)
                                             .build())
                .retrieve()
                .bodyToMono(CapacityIntercomResponse.class)
                .filter(response -> response.getStatus() == 200)
                .then()
                .onErrorResume(e -> {
                    return Mono.error(new RuntimeException("Failed to delete capacities: " + e.getMessage(), e));
                });
    }

}
