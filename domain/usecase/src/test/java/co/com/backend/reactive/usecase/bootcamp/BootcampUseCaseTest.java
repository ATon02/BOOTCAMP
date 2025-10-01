package co.com.backend.reactive.usecase.bootcamp;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.model.capacitydata.gateways.CapacityDataRepository;
import co.com.backend.reactive.model.capacitydata.CapacityData;
import co.com.backend.reactive.model.tecnologydata.TecnologyData;
import co.com.backend.reactive.usecase.bootcamp.dto.BootcampCompletedResponse;
import co.com.backend.reactive.usecase.bootcamp.dto.CapacityDTO;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private BootcampCapacityRepository bootcampCapacityRepository;

    @Mock
    private CapacityDataRepository capacityDataRepository;

    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    private Bootcamp testBootcamp;
    private Bootcamp savedBootcamp;
    private Set<Long> capacityIds;

    @BeforeEach
    void setUp() {
        capacityIds = new HashSet<>();
        capacityIds.add(1L);
        capacityIds.add(2L);
        
        LocalDate futureDate = LocalDate.now().plusDays(1);
        
        testBootcamp = Bootcamp.builder()
                .name("Java Bootcamp")
                .description("Intensive Java training program")
                .startDate(futureDate)
                .durationInDays(60L)
                .capacities(capacityIds)
                .build();

        savedBootcamp = Bootcamp.builder()
                .id(1L)
                .name("Java Bootcamp")
                .description("Intensive Java training program")
                .startDate(futureDate)
                .durationInDays(60L)
                .capacities(capacityIds)
                .build();
    }

    @Test
    void save_ShouldSaveBootcampSuccessfully_WhenValidDataProvided() {
        when(capacityDataRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(capacityDataRepository.existsById(2L)).thenReturn(Mono.just(true));
        when(bootcampRepository.save(any(Bootcamp.class))).thenReturn(Mono.just(savedBootcamp));
        when(bootcampCapacityRepository.save(any(BootcampCapacity.class))).thenReturn(Mono.just(new BootcampCapacity()));

        StepVerifier.create(bootcampUseCase.save(testBootcamp))
                .expectNextMatches(result -> 
                    result.getId().equals(1L) && 
                    result.getName().equals("Java Bootcamp") && 
                    result.getCapacities().size() == 2
                )
                .verifyComplete();

        verify(capacityDataRepository, times(2)).existsById(anyLong());
        verify(bootcampRepository, times(1)).save(any(Bootcamp.class));
        verify(bootcampCapacityRepository, times(2)).save(any(BootcampCapacity.class));
    }

    @Test
    void save_ShouldThrowException_WhenCapacitiesAreNull() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Bootcamp bootcampWithoutCapacities = Bootcamp.builder()
                .name("Java Bootcamp")
                .description("Intensive Java training program")
                .startDate(futureDate)
                .durationInDays(60L)
                .capacities(null)
                .build();

        StepVerifier.create(bootcampUseCase.save(bootcampWithoutCapacities))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(capacityDataRepository, never()).existsById(anyLong());
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
    }

    @Test
    void save_ShouldThrowException_WhenCapacitiesAreEmpty() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Bootcamp bootcampWithEmptyCapacities = Bootcamp.builder()
                .name("Java Bootcamp")
                .description("Intensive Java training program")
                .startDate(futureDate)
                .durationInDays(60L)
                .capacities(new HashSet<>())
                .build();

        StepVerifier.create(bootcampUseCase.save(bootcampWithEmptyCapacities))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(capacityDataRepository, never()).existsById(anyLong());
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
    }

    @Test
    void save_ShouldThrowException_WhenCapacityDoesNotExist() {
        when(capacityDataRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(capacityDataRepository.existsById(2L)).thenReturn(Mono.just(false));

        StepVerifier.create(bootcampUseCase.save(testBootcamp))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(capacityDataRepository, times(2)).existsById(anyLong());
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
        verify(bootcampCapacityRepository, never()).save(any(BootcampCapacity.class));
    }

    @Test
    void save_ShouldValidateBootcampData_WhenInvalidDataProvided() {
        Bootcamp invalidBootcamp = Bootcamp.builder()
                .name("")
                .description("Intensive Java training program")
                .startDate(LocalDate.now().plusDays(1))
                .durationInDays(60L)
                .capacities(capacityIds)
                .build();

        StepVerifier.create(bootcampUseCase.save(invalidBootcamp))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(capacityDataRepository, never()).existsById(anyLong());
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
    }

    @Test
    void getAllBootcampWithCapacities_ShouldReturnBootcampsWithCapacities_WhenDataExists() {
        List<Bootcamp> bootcamps = Arrays.asList(
            Bootcamp.builder().id(1L).name("Java Bootcamp").build(),
            Bootcamp.builder().id(2L).name("React Bootcamp").build()
        );
        
        List<Long> capacityIds = Arrays.asList(1L, 2L);
        
        List<TecnologyData> technologies = Arrays.asList(
            TecnologyData.builder().id(1L).name("Java").build(),
            TecnologyData.builder().id(2L).name("Spring").build()
        );
        
        List<CapacityData> capacityDataList = Arrays.asList(
            CapacityData.builder()
                .id(1L)
                .name("Backend Development")
                .description("Backend development skills")
                .technologies(technologies)
                .build(),
            CapacityData.builder()
                .id(2L)
                .name("Database Management")
                .description("Database skills")
                .technologies(technologies)
                .build()
        );

        when(bootcampRepository.findAllPaginated(0, 10, "name", "asc"))
            .thenReturn(Flux.fromIterable(bootcamps));
        when(bootcampCapacityRepository.findCapacitiesIdsByBootcampId(1L))
            .thenReturn(Flux.fromIterable(capacityIds));
        when(bootcampCapacityRepository.findCapacitiesIdsByBootcampId(2L))
            .thenReturn(Flux.fromIterable(capacityIds));
        when(capacityDataRepository.findByIds(capacityIds))
            .thenReturn(Flux.fromIterable(capacityDataList));

        StepVerifier.create(bootcampUseCase.getAllBootcampWithCapacities(0, 10, "name", "asc"))
            .expectNextMatches(response -> 
                response.getId().equals(1L) && 
                response.getName().equals("Java Bootcamp") &&
                response.getCapacities().size() == 2
            )
            .expectNextMatches(response -> 
                response.getId().equals(2L) && 
                response.getName().equals("React Bootcamp") &&
                response.getCapacities().size() == 2
            )
            .verifyComplete();

        verify(bootcampRepository).findAllPaginated(0, 10, "name", "asc");
        verify(bootcampCapacityRepository, times(2)).findCapacitiesIdsByBootcampId(anyLong());
        verify(capacityDataRepository, times(2)).findByIds(anyList());
    }

    @Test
    void getAllBootcampWithCapacities_ShouldReturnBootcampWithEmptyCapacities_WhenNoCapacitiesFound() {
        List<Bootcamp> bootcamps = Arrays.asList(
            Bootcamp.builder().id(1L).name("Empty Bootcamp").build()
        );

        when(bootcampRepository.findAllPaginated(0, 10, "name", "asc"))
            .thenReturn(Flux.fromIterable(bootcamps));
        when(bootcampCapacityRepository.findCapacitiesIdsByBootcampId(1L))
            .thenReturn(Flux.empty());

        StepVerifier.create(bootcampUseCase.getAllBootcampWithCapacities(0, 10, "name", "asc"))
            .expectNextMatches(response -> 
                response.getId().equals(1L) && 
                response.getName().equals("Empty Bootcamp") &&
                response.getCapacities().isEmpty()
            )
            .verifyComplete();

        verify(bootcampRepository).findAllPaginated(0, 10, "name", "asc");
        verify(bootcampCapacityRepository).findCapacitiesIdsByBootcampId(1L);
        verify(capacityDataRepository, never()).findByIds(anyList());
    }

    @Test
    void getAllBootcampWithCapacities_ShouldHandleCapacityDataError_WhenExternalServiceFails() {
        List<Bootcamp> bootcamps = Arrays.asList(
            Bootcamp.builder().id(1L).name("Java Bootcamp").build()
        );
        
        List<Long> capacityIds = Arrays.asList(1L, 2L);

        when(bootcampRepository.findAllPaginated(0, 10, "name", "asc"))
            .thenReturn(Flux.fromIterable(bootcamps));
        when(bootcampCapacityRepository.findCapacitiesIdsByBootcampId(1L))
            .thenReturn(Flux.fromIterable(capacityIds));
        when(capacityDataRepository.findByIds(capacityIds))
            .thenReturn(Flux.error(new RuntimeException("External service error")));

        StepVerifier.create(bootcampUseCase.getAllBootcampWithCapacities(0, 10, "name", "asc"))
            .expectNextMatches(response -> 
                response.getId().equals(1L) && 
                response.getName().equals("Java Bootcamp") &&
                response.getCapacities().isEmpty()
            )
            .verifyComplete();

        verify(bootcampRepository).findAllPaginated(0, 10, "name", "asc");
        verify(bootcampCapacityRepository).findCapacitiesIdsByBootcampId(1L);
        verify(capacityDataRepository).findByIds(capacityIds);
    }

    @Test
    void getAllBootcampWithCapacities_ShouldReturnEmpty_WhenNoBootcampsFound() {
        when(bootcampRepository.findAllPaginated(0, 10, "name", "asc"))
            .thenReturn(Flux.empty());

        StepVerifier.create(bootcampUseCase.getAllBootcampWithCapacities(0, 10, "name", "asc"))
            .verifyComplete();

        verify(bootcampRepository).findAllPaginated(0, 10, "name", "asc");
        verify(bootcampCapacityRepository, never()).findCapacitiesIdsByBootcampId(anyLong());
        verify(capacityDataRepository, never()).findByIds(anyList());
    }

    @Test
    void getAllBootcampWithCapacities_ShouldMapCapacityDataCorrectly_WhenTechnologiesExist() {
        List<Bootcamp> bootcamps = Arrays.asList(
            Bootcamp.builder().id(1L).name("Java Bootcamp").build()
        );
        
        List<Long> capacityIds = Arrays.asList(1L);
        
        List<TecnologyData> technologies = Arrays.asList(
            TecnologyData.builder().id(1L).name("Java").build(),
            TecnologyData.builder().id(2L).name("Spring Boot").build()
        );
        
        CapacityData capacityData = CapacityData.builder()
            .id(1L)
            .name("Backend Development")
            .description("Full backend development")
            .technologies(technologies)
            .build();

        when(bootcampRepository.findAllPaginated(0, 10, "name", "asc"))
            .thenReturn(Flux.fromIterable(bootcamps));
        when(bootcampCapacityRepository.findCapacitiesIdsByBootcampId(1L))
            .thenReturn(Flux.fromIterable(capacityIds));
        when(capacityDataRepository.findByIds(capacityIds))
            .thenReturn(Flux.just(capacityData));

        StepVerifier.create(bootcampUseCase.getAllBootcampWithCapacities(0, 10, "name", "asc"))
            .expectNextMatches(response -> {
                CapacityDTO capacity = response.getCapacities().get(0);
                return response.getId().equals(1L) && 
                       capacity.getId().equals(1L) &&
                       capacity.getName().equals("Backend Development") &&
                       capacity.getDescription().equals("Full backend development") &&
                       capacity.getTecnologies().size() == 2 &&
                       capacity.getTecnologies().get(0).getName().equals("Java");
            })
            .verifyComplete();
    }
}