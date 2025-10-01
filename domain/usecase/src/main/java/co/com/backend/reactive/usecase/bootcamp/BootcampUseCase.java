package co.com.backend.reactive.usecase.bootcamp;

import java.util.List;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.model.capacitydata.gateways.CapacityDataRepository;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;
import co.com.backend.reactive.usecase.bootcamp.utils.BootcampValidator;
import co.com.backend.reactive.usecase.bootcamp.dto.BootcampCompletedResponse;
import co.com.backend.reactive.usecase.bootcamp.dto.CapacityDTO;
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

    @Override
    public Flux<BootcampCompletedResponse> getAllBootcampWithCapacities(int page, int size, String sortBy,
            String sortDirection) {
        return bootcampRepository.findAllPaginated(page, size, sortBy, sortDirection)
                .concatMap(bootcamp -> bootcampCapacityRepository.findCapacitiesIdsByBootcampId(bootcamp.getId())
                        .collectList()
                        .flatMap(capacitiesIds -> {
                            if (capacitiesIds.isEmpty()) {
                                return Mono.just(BootcampCompletedResponse.builder()
                                        .id(bootcamp.getId())
                                        .name(bootcamp.getName())
                                        .description(bootcamp.getDescription())
                                        .startDate(bootcamp.getStartDate())
                                        .durationInDays(bootcamp.getDurationInDays())
                                        .capacities(List.of())
                                        .build());
                            }

                            return capacityDataRepository.findByIds(capacitiesIds)
                                    .onErrorResume(error -> Flux.empty())
                                    .map(capacityData -> CapacityDTO.builder()
                                            .id(capacityData.getId())
                                            .name(capacityData.getName())
                                            .description(capacityData.getDescription())
                                            .tecnologies(capacityData.getTechnologies())
                                            .build())
                                    .collectList()
                                    .map(capacities -> BootcampCompletedResponse.builder()
                                            .id(bootcamp.getId())
                                            .name(bootcamp.getName())
                                            .description(bootcamp.getDescription())
                                            .startDate(bootcamp.getStartDate())
                                            .durationInDays(bootcamp.getDurationInDays())
                                            .capacities(capacities)
                                            .build());
                        }));
    }
    
    @Override
    public Mono<Void> deleteBootcamp(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException(BootcampError.INVALID_ID.getMessage()));
        }
        
        return bootcampCapacityRepository.findCapacitiesIdsByBootcampId(id)
                .distinct()
                .collectList()
                .flatMap(this::findSingleCapacities)
                .flatMap(singleCapacityIds -> {
                    return bootcampCapacityRepository.deleteByBootcampId(id)
                            .then(Mono.defer(() -> {
                                if (!singleCapacityIds.isEmpty()) {
                                    return capacityDataRepository.deleteByIds(singleCapacityIds)
                                            .then(bootcampRepository.deleteById(id));
                                } else {
                                    return bootcampRepository.deleteById(id);
                                }
                            }));
                });
    }

    private Mono<List<Long>> findSingleCapacities(List<Long> capacityIds) {
        return Flux.fromIterable(capacityIds)
                .filterWhen(capacityId -> 
                    bootcampCapacityRepository.countBootcampsByCapacityId(capacityId)
                            .map(count -> count == 1)
                )
                .collectList();
    }

    @Override
    public Flux<BootcampCompletedResponse> getBootcampWithCapacitiesByIds(List<Long> ids){
        return bootcampRepository.findByIds(ids)
                .concatMap(bootcamp -> bootcampCapacityRepository.findCapacitiesIdsByBootcampId(bootcamp.getId())
                        .collectList()
                        .flatMap(capacitiesIds -> {
                            if (capacitiesIds.isEmpty()) {
                                return Mono.just(BootcampCompletedResponse.builder()
                                        .id(bootcamp.getId())
                                        .name(bootcamp.getName())
                                        .description(bootcamp.getDescription())
                                        .startDate(bootcamp.getStartDate())
                                        .durationInDays(bootcamp.getDurationInDays())
                                        .capacities(List.of())
                                        .build());
                            }

                            return capacityDataRepository.findByIds(capacitiesIds)
                                    .onErrorResume(error -> Flux.empty())
                                    .map(capacityData -> CapacityDTO.builder()
                                            .id(capacityData.getId())
                                            .name(capacityData.getName())
                                            .description(capacityData.getDescription())
                                            .tecnologies(capacityData.getTechnologies())
                                            .build())
                                    .collectList()
                                    .map(capacities -> BootcampCompletedResponse.builder()
                                            .id(bootcamp.getId())
                                            .name(bootcamp.getName())
                                            .description(bootcamp.getDescription())
                                            .startDate(bootcamp.getStartDate())
                                            .durationInDays(bootcamp.getDurationInDays())
                                            .capacities(capacities)
                                            .build());
                        }));
    }


    @Override
    public Mono<Bootcamp> getBootcampById(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException(BootcampError.INVALID_ID.getMessage()));
        }
        return bootcampRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BootcampError.BOOTCAMP_NOT_FOUND.getMessage() + " " + id)));
    }
}

