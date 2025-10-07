package co.com.backend.reactive.api.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampRequestDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date cannot be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate  startDate;
    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be greater than 0")
    private Long durationInDays;
    @NotEmpty(message = "Capacities cannot be empty")
    @Size(min = 1, max = 4, message = "Capacities size must be between 1 and 4 elements")
    private Set<Long> capacities;
}