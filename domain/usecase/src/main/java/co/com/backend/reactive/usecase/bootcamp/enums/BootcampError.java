package co.com.backend.reactive.usecase.bootcamp.enums;

public enum BootcampError {

    BOOTCAMP_ID_REQUIRED("Bootcamp ID is required"),
    INVALID_ID("Invalid ID provided"),
    BOOTCAMP_NOT_FOUND("Bootcamp not found"),
    BOOTCAMP_NAME_REQUIRED("Bootcamp name is required"),
    BOOTCAMP_DESCRIPTION_REQUIRED("Bootcamp description is required"),
    BOOTCAMP_START_DATE_REQUIRED("Bootcamp start date is required"),
    BOOTCAMP_START_DATE_INVALID("Bootcamp start date cannot be in the past"),
    BOOTCAMP_DURATION_REQUIRED("Bootcamp duration is required"),
    BOOTCAMP_DURATION_INVALID("Bootcamp duration must be greater than 0"),
    MINIMUM_CAPACITIES_REQUIRED("Bootcamp must have at least 1 capacity"),
    MAXIMUM_CAPACITIES_EXCEEDED("Bootcamp cannot have more than 4 capacities"),
    INVALID_CAPACITY_COUNT("Bootcamp must have between 1 and 4 capacities"),
    DUPLICATE_CAPACITIES("Bootcamp cannot have duplicate capacities"),
    CAPACITY_ALREADY_EXISTS("Capacity already exists in this bootcamp"),
    CAPACITY_NOT_FOUND("Capacity not found in this bootcamp"),
    CAPACITIES_REQUIRED("Bootcamp must have at least one capacity");

    private final String message;

    BootcampError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}