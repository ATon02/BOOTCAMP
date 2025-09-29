package co.com.backend.reactive.r2dbc.repository;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;


import co.com.backend.reactive.r2dbc.entiry.BootcampCapacityEntity;


// TODO: This file is just an example, you should delete or modify it
@Repository
public interface BootcampCapacityR2dbcRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapacityEntity> {

    @Query("SELECT capacity_id FROM bootcamp_capacity WHERE bootcamp_id = :bootcampId")
    Flux<Long> findCapacitiesIdsByBootcampId(Long bootcampId);

}
