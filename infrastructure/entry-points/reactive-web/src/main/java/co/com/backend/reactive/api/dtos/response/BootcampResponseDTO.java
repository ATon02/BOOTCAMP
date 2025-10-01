package co.com.backend.reactive.api.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate  startDate;
    private Long durationInDays;
    private Set<Long> capacities;
}