package co.com.backend.reactive.r2dbc.repository;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


import co.com.backend.reactive.r2dbc.entiry.BootcampEntity;

// TODO: This file is just an example, you should delete or modify it
@Repository
public interface BootcampR2dbcRepository extends ReactiveCrudRepository<BootcampEntity, Long>, ReactiveQueryByExampleExecutor<BootcampEntity> {

}
