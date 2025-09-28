package co.com.backend.reactive.usecase.bootcamp;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.model.capacitydata.gateways.CapacityDataRepository;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;
import co.com.backend.reactive.usecase.bootcamp.utils.BootcampValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
@RequiredArgsConstructor
public class BootcampUseCase implements IBootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final CapacityDataRepository capacityDataRepository;

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return BootcampValidator.validateForSave(bootcamp)
                .flatMap(validatedBootcamp -> validateAndCheckCapacities(validatedBootcamp))
                .flatMap(this::saveBootcampWithCapacities);
    }

    private Mono<Bootcamp> validateAndCheckCapacities(Bootcamp bootcamp) {
        if (bootcamp.getCapacities() == null || bootcamp.getCapacities().isEmpty()) {
            return Mono.error(new IllegalArgumentException(BootcampError.CAPACITIES_REQUIRED.getMessage()));
        }
        
        return Flux.fromIterable(bootcamp.getCapacities())
                .flatMap(this::validateCapacityExists)
                .collectList()
                .map(validatedCapacityIds -> bootcamp);
    }

    private Mono<Long> validateCapacityExists(Long capacityId) {
        return capacityDataRepository.existsById(capacityId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException(
                                BootcampError.CAPACITY_NOT_FOUND.getMessage() + " " + capacityId));
                    }
                    return Mono.just(capacityId);
                });
    }

    private Mono<Bootcamp> saveBootcampWithCapacities(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcamp)
                .flatMap(savedBootcamp -> {
                    Bootcamp bootcampWithCapacities = Bootcamp.builder()
                            .id(savedBootcamp.getId())
                            .name(savedBootcamp.getName())
                            .description(savedBootcamp.getDescription())
                            .startDate(savedBootcamp.getStartDate())
                            .durationInDays(savedBootcamp.getDurationInDays())
                            .capacities(bootcamp.getCapacities())
                            .build();

                    return saveBootcampCapacities(bootcampWithCapacities)
                            .thenReturn(bootcampWithCapacities);
                });
    }

    private Mono<Void> saveBootcampCapacities(Bootcamp bootcamp) {
        return Flux.fromIterable(bootcamp.getCapacities())
                .map(capacityId -> {
                    return BootcampCapacity.builder()
                            .bootcampId(bootcamp.getId())
                            .capacityId(capacityId)
                            .build();
                })
                .flatMap(bootcampCapacityRepository::save)
                .then();
    }
}
