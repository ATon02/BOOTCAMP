package co.com.backend.reactive.r2dbc.repository;

import java.util.List;

import co.com.backend.reactive.r2dbc.helper.ConstRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;


import co.com.backend.reactive.r2dbc.entiry.BootcampEntity;

// TODO: This file is just an example, you should delete or modify it
@Repository
public interface BootcampR2dbcRepository extends ReactiveCrudRepository<BootcampEntity, Long>, ReactiveQueryByExampleExecutor<BootcampEntity> {

    @Query(ConstRepository.FIND_ALL_PAGINATED)
    Flux<BootcampEntity> findAllPaginated(@Param("offset") int offset,
                                        @Param("size") int size,
                                        @Param("sortBy") String sortBy,
                                        @Param("sortDirection") String sortDirection);

    @Query(ConstRepository.FIND_BY_IDS)
    Flux<BootcampEntity> findByIds(@Param("ids") List<Long> ids);
}

