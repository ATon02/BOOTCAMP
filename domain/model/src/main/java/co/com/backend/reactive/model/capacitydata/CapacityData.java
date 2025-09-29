package co.com.backend.reactive.model.capacitydata;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import co.com.backend.reactive.model.tecnologydata.TecnologyData;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityData {
    private Long id;
    private String name;
    private String description;
    private List<TecnologyData> technologies;
}
