package co.com.backend.reactive.r2dbc.adapter;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import co.com.backend.reactive.r2dbc.entiry.BootcampEntity;
import co.com.backend.reactive.r2dbc.helper.ReactiveAdapterOperations;
import co.com.backend.reactive.r2dbc.repository.BootcampR2dbcRepository;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public class BootcampRepositoryAdapter extends ReactiveAdapterOperations<
    Bootcamp/* change for domain model */,
    BootcampEntity/* change for adapter model */,
    Long,
    BootcampR2dbcRepository
> implements BootcampRepository{
    public BootcampRepositoryAdapter(BootcampR2dbcRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class/* change for domain model */));
    }

    @Override
    public Flux<Bootcamp> findAllPaginated(int page, int size, String sortBy, String sortDirection) {
        int offset = page * size;
        return repository.findAllPaginated(offset, size, sortBy, sortDirection)
                .map(this::toEntity);
    }

}
