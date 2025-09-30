package co.com.backend.reactive.r2dbc.adapter;


import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.r2dbc.entiry.BootcampCapacityEntity;
import co.com.backend.reactive.r2dbc.helper.ReactiveAdapterOperations;
import co.com.backend.reactive.r2dbc.repository.BootcampCapacityR2dbcRepository;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;


@Repository
public class BootcampCapacityRepositoryAdapter extends ReactiveAdapterOperations<
    BootcampCapacity,
    BootcampCapacityEntity,
    Long,
    BootcampCapacityR2dbcRepository
> implements BootcampCapacityRepository{
    public BootcampCapacityRepositoryAdapter(BootcampCapacityR2dbcRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, BootcampCapacity.class));
    }

    @Override
    public Mono<BootcampCapacity> save(BootcampCapacity bootcampCapacity) {
        return super.save(bootcampCapacity);
    }

    @Override
    public Flux<Long> findCapacitiesIdsByBootcampId(Long bootcampId) {
        return repository.findCapacitiesIdsByBootcampId(bootcampId);
    }

    @Override
    public Flux<Long> findBootcampIdsByCapacityId(Long capacityId) {
        return repository.findBootcampIdsByCapacityId(capacityId);
    }

    @Override
    public Mono<Void> deleteByBootcampId(Long bootcampId) {
        return repository.deleteByBootcampId(bootcampId);
    }

    @Override
    public Mono<Long> countBootcampsByCapacityId(Long capacityId) {
        return repository.countBootcampsByCapacityId(capacityId);
    }

}
