package co.com.backend.reactive.usecase.bootcamp;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.model.bootcamp.gateways.BootcampRepository;
import co.com.backend.reactive.model.bootcampcapacity.BootcampCapacity;
import co.com.backend.reactive.model.bootcampcapacity.gateways.BootcampCapacityRepository;
import co.com.backend.reactive.model.capacitydata.gateways.CapacityDataRepository;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        
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
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
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
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
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
                .startDate(new Date(System.currentTimeMillis() + 86400000))
                .durationInDays(60L)
                .capacities(capacityIds)
                .build();

        StepVerifier.create(bootcampUseCase.save(invalidBootcamp))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(capacityDataRepository, never()).existsById(anyLong());
        verify(bootcampRepository, never()).save(any(Bootcamp.class));
    }
}