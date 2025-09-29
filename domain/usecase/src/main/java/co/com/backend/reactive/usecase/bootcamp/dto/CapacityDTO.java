package co.com.backend.reactive.usecase.bootcamp.dto;

import java.util.List;

import co.com.backend.reactive.model.tecnologydata.TecnologyData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityDTO {
    private Long id;
    private String name;
    private String description;
    private List<TecnologyData> tecnologies;

}
