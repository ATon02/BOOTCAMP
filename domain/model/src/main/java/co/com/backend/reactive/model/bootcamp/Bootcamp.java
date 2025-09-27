package co.com.backend.reactive.model.bootcamp;
import lombok.Builder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Bootcamp {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Long durationInDays;
    @Builder.Default
    private Set<Long> capacities = new HashSet<>();
}
