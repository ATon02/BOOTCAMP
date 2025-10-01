package co.com.backend.reactive.r2dbc.entiry;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("bootcamps")
public class BootcampEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private Long durationInDays;
}
