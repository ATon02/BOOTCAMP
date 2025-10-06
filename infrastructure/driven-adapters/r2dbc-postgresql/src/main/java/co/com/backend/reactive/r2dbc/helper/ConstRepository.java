package co.com.backend.reactive.r2dbc.helper;


import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ConstRepository {
    private ConstRepository() {}

    public static final String FIND_CAPACITIES_IDS_BY_BOOTCAMP_ID =
            "SELECT capacity_id FROM bootcamp_capacity WHERE bootcamp_id = :bootcampId";

    public static final String FIND_BOOTCAMP_IDS_BY_CAPACITY_ID =
            "SELECT bootcamp_id FROM bootcamp_capacity WHERE capacity_id = :capacityId";

    public static final String DELETE_BY_BOOTCAMP_ID =
            "DELETE FROM bootcamp_capacity WHERE bootcamp_id = :bootcampId";

    public static final String COUNT_BOOTCAMPS_BY_CAPACITY_ID =
            "SELECT COUNT(*) FROM bootcamp_capacity WHERE capacity_id = :capacityId";

    public static final String FIND_ALL_PAGINATED =
            "SELECT b.id, b.name, " +
                    "COUNT(bc.capacity_id) as capacity_count " +
                    "FROM bootcamps b " +
                    "LEFT JOIN bootcamp_capacity bc ON b.id = bc.bootcamp_id " +
                    "GROUP BY b.id, b.name " +
                    "ORDER BY " +
                    "CASE WHEN :sortBy = 'name' AND :sortDirection = 'asc' THEN b.name ELSE NULL END ASC, " +
                    "CASE WHEN :sortBy = 'name' AND :sortDirection = 'desc' THEN b.name ELSE NULL END DESC, " +
                    "CASE WHEN :sortBy = 'capacity_count' AND :sortDirection = 'asc' THEN COUNT(bc.capacity_id) ELSE NULL END ASC, " +
                    "CASE WHEN :sortBy = 'capacity_count' AND :sortDirection = 'desc' THEN COUNT(bc.capacity_id) ELSE NULL END DESC " +
                    "LIMIT :size OFFSET :offset";

    public static final String FIND_BY_IDS =
            "SELECT * FROM bootcamps WHERE id IN (:ids)";
}
