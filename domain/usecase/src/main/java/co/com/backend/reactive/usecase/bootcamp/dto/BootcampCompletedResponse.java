package co.com.backend.reactive.usecase.bootcamp.dto;

import java.util.Date;
import java.util.List;

import co.com.backend.reactive.usecase.bootcamp.dto.CapacityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BootcampCompletedResponse {
    private Long id;
    private String name;
    private List<CapacityDTO> capacities;

}
