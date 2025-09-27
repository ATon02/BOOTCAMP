package co.com.backend.reactive.r2dbc.entiry;
import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("bootcamp_capacity")
public class BootcampCapacityEntity {
    @Id
    private Long id;
    private Long bootcampId;
    private Long capacityId;
}
