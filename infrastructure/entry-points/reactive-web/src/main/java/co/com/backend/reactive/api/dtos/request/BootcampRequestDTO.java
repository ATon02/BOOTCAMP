package co.com.backend.reactive.api.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampRequestDTO {
    private String name;
    private String description;
    private Date startDate;
    private Long durationInDays;
    private Set<Long> capacities;
}