package co.com.backend.reactive.r2dbc.adapter;


import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.r2dbc.entiry.BootcampCapacityEntity;
import co.com.backend.reactive.r2dbc.helper.ReactiveAdapterOperations;
import co.com.backend.reactive.r2dbc.repository.BootcampCapacityR2dbcRepository;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BootcampCapacityRepositoryAdapter extends ReactiveAdapterOperations<
    BootcampCapacity/* change for domain model */,
    BootcampCapacityEntity/* change for adapter model */,
    Long,
    BootcampCapacityR2dbcRepository
> implements BootcampCapacityRepository{
    public BootcampCapacityRepositoryAdapter(BootcampCapacityR2dbcRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, BootcampCapacity.class/* change for domain model */));
    }

}
