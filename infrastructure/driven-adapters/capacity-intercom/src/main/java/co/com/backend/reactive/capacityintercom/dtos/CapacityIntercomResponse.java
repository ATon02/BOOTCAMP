package co.com.backend.reactive.capacityintercom.dtos;

import co.com.backend.reactive.model.capacitydata.CapacityData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityIntercomResponse {
    private int status;
    private String message;
    private String path;
    private String timestamp;
    private CapacityData data;
}