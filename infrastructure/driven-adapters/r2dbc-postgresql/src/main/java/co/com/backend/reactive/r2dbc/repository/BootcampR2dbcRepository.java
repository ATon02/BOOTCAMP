package co.com.backend.reactive.r2dbc.repository;

import java.util.List;

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

    @Query("SELECT b.id, b.name, " +
       "COUNT(bc.capacity_id) as capacity_count " +
       "FROM bootcamps b " +
       "LEFT JOIN bootcamp_capacity bc ON b.id = bc.bootcamp_id " +
       "GROUP BY b.id, b.name " +
       "ORDER BY " +
       "CASE WHEN :sortBy = 'name' AND :sortDirection = 'asc' THEN b.name ELSE NULL END ASC, " +
       "CASE WHEN :sortBy = 'name' AND :sortDirection = 'desc' THEN b.name ELSE NULL END DESC, " +
       "CASE WHEN :sortBy = 'capacity_count' AND :sortDirection = 'asc' THEN COUNT(bc.capacity_id) ELSE NULL END ASC, " +
       "CASE WHEN :sortBy = 'capacity_count' AND :sortDirection = 'desc' THEN COUNT(bc.capacity_id) ELSE NULL END DESC " +
       "LIMIT :size OFFSET :offset")
    Flux<BootcampEntity> findAllPaginated(@Param("offset") int offset,
                                        @Param("size") int size,
                                        @Param("sortBy") String sortBy,
                                        @Param("sortDirection") String sortDirection);

    @Query("SELECT * FROM bootcamps WHERE id IN (:ids)")
    Flux<BootcampEntity> findByIds(@Param("ids") List<Long> ids);
}

