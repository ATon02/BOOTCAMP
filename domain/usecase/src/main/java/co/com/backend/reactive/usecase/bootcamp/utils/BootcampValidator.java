package co.com.backend.reactive.usecase.bootcamp.utils;

import reactor.core.publisher.Mono;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;

public class BootcampValidator {

    public static Mono<Bootcamp> validate(Bootcamp bootcamp) {
        return Mono.fromCallable(() -> {
            validateBasicFields(bootcamp);
            validateCapacities(bootcamp);
            return bootcamp;
        });
    }

    public static Mono<Bootcamp> validateForSave(Bootcamp bootcamp) {
        return validate(bootcamp);
    }

    public static Mono<Bootcamp> validateCapacityAddition(Bootcamp bootcamp, Long capacityId) {
        return Mono.fromCallable(() -> {
            if (bootcamp.getCapacities() == null) {
                bootcamp.setCapacities(new HashSet<>());
            }
            
            if (bootcamp.getCapacities().size() >= 4) {
                throw new IllegalArgumentException(BootcampError.MAXIMUM_CAPACITIES_EXCEEDED.getMessage());
            }
            
            if (bootcamp.getCapacities().contains(capacityId)) {
                throw new IllegalArgumentException(BootcampError.CAPACITY_ALREADY_EXISTS.getMessage());
            }
            
            return bootcamp;
        });
    }

    public static Mono<Bootcamp> validateCapacityRemoval(Bootcamp bootcamp, Long capacityId) {
        return Mono.fromCallable(() -> {
            if (bootcamp.getCapacities() == null || bootcamp.getCapacities().size() <= 1) {
                throw new IllegalArgumentException(BootcampError.MINIMUM_CAPACITIES_REQUIRED.getMessage());
            }
            
            if (!bootcamp.getCapacities().contains(capacityId)) {
                throw new IllegalArgumentException(BootcampError.CAPACITY_NOT_FOUND.getMessage());
            }
            
            return bootcamp;
        });
    }

    private static void validateBasicFields(Bootcamp bootcamp) {
        if (bootcamp.getId() == null) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_ID_REQUIRED.getMessage());
        }

        if (bootcamp.getName() == null || bootcamp.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_NAME_REQUIRED.getMessage());
        }
        
        if (bootcamp.getDescription() == null || bootcamp.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_DESCRIPTION_REQUIRED.getMessage());
        }

        if (bootcamp.getStartDate() == null) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_START_DATE_REQUIRED.getMessage());
        }

        Date currentDate = new Date();
        if (bootcamp.getStartDate().before(currentDate)) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_START_DATE_INVALID.getMessage());
        }

        if (bootcamp.getDurationInDays() == null) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_DURATION_REQUIRED.getMessage());
        }

        if (bootcamp.getDurationInDays() <= 0) {
            throw new IllegalArgumentException(BootcampError.BOOTCAMP_DURATION_INVALID.getMessage());
        }
    }

    private static void validateCapacities(Bootcamp bootcamp) {
        if (bootcamp.getCapacities() == null) {
            bootcamp.setCapacities(new HashSet<>());
        }

        Set<Long> uniqueCapacities = new HashSet<>(bootcamp.getCapacities());
        if (uniqueCapacities.size() != bootcamp.getCapacities().size()) {
            throw new IllegalArgumentException(BootcampError.DUPLICATE_CAPACITIES.getMessage());
        }

        if (bootcamp.getCapacities().size() < 1 || bootcamp.getCapacities().size() > 4) {
            throw new IllegalArgumentException(BootcampError.INVALID_CAPACITY_COUNT.getMessage());
        }
    }
}