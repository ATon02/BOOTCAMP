package co.com.backend.reactive.r2dbc.repository;

import co.com.backend.reactive.r2dbc.helper.ConstRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import co.com.backend.reactive.r2dbc.entiry.BootcampCapacityEntity;


// TODO: This file is just an example, you should delete or modify it
@Repository
public interface BootcampCapacityR2dbcRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapacityEntity> {

    @Query(ConstRepository.FIND_CAPACITIES_IDS_BY_BOOTCAMP_ID)
    Flux<Long> findCapacitiesIdsByBootcampId(Long bootcampId);

    @Query(ConstRepository.FIND_BOOTCAMP_IDS_BY_CAPACITY_ID)
    Flux<Long> findBootcampIdsByCapacityId(Long capacityId);

    @Modifying
    @Query(ConstRepository.DELETE_BY_BOOTCAMP_ID)
    Mono<Void> deleteByBootcampId(Long bootcampId);

    @Query(ConstRepository.COUNT_BOOTCAMPS_BY_CAPACITY_ID)
    Mono<Long> countBootcampsByCapacityId(Long capacityId);

}
