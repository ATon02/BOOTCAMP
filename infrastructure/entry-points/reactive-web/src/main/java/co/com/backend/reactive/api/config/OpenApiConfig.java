package co.com.backend.reactive.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import co.com.backend.reactive.api.dtos.request.BootcampRequestDTO;
import co.com.backend.reactive.api.dtos.response.BootcampResponseDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import co.com.backend.reactive.api.dtos.response.ErrorResponse;
import co.com.backend.reactive.usecase.bootcamp.dto.BootcampCompletedResponse;
import co.com.backend.reactive.usecase.bootcamp.dto.CapacityDTO;
import co.com.backend.reactive.model.tecnologydata.TecnologyData;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("Bootcamp Management API").version("v1"));
    }

    @Bean
    public GroupedOpenApi publicApi(OpenApiCustomizer customizer) {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(customizer)
                .build();
    }

    @Bean
    @Primary
    public OpenApiCustomizer customizer() {
        return openApi -> {
            openApi.getComponents()
                    .addSchemas("BootcampRequest", new Schema<BootcampRequestDTO>()
                            .addProperty("name", new StringSchema().maxLength(50))
                            .addProperty("description", new StringSchema().maxLength(255))
                            .addProperty("startDate", new StringSchema().format("date"))
                            .addProperty("durationInDays", new Schema<>().type("integer").format("int64"))
                            .addProperty("capacities", new Schema<>().type("array")
                                    .items(new Schema<>().type("integer").format("int64"))))
                    .addSchemas("BootcampResponse", new Schema<BootcampResponseDTO>()
                            .addProperty("id", new Schema<>().type("integer").format("int64"))
                            .addProperty("name", new StringSchema())
                            .addProperty("description", new StringSchema())
                            .addProperty("startDate", new StringSchema().format("date"))
                            .addProperty("durationInDays", new Schema<>().type("integer").format("int64"))
                            .addProperty("capacities", new Schema<>().type("array")
                                    .items(new Schema<>().type("integer").format("int64"))))
                    .addSchemas("BootcampSuccessResponse", new Schema<BaseResponse<BootcampResponseDTO>>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time"))
                            .addProperty("data", new Schema<>().$ref("#/components/schemas/BootcampResponse")))
                    .addSchemas("ErrorResponse", new Schema<ErrorResponse>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time")))
                    .addSchemas("TecnologyData", new Schema<TecnologyData>()
                            .addProperty("id", new Schema<>().type("integer").format("int64"))
                            .addProperty("name", new StringSchema()))
                    .addSchemas("CapacityDTO", new Schema<CapacityDTO>()
                            .addProperty("id", new Schema<>().type("integer").format("int64"))
                            .addProperty("name", new StringSchema())
                            .addProperty("description", new StringSchema())
                            .addProperty("tecnologies", new Schema<>().type("array")
                                    .items(new Schema<>().$ref("#/components/schemas/TecnologyData"))))
                    .addSchemas("BootcampCompletedResponse", new Schema<BootcampCompletedResponse>()
                            .addProperty("id", new Schema<>().type("integer").format("int64"))
                            .addProperty("name", new StringSchema())
                            .addProperty("capacities", new Schema<>().type("array")
                                    .items(new Schema<>().$ref("#/components/schemas/CapacityDTO"))))
                    .addSchemas("BootcampListResponse", new Schema<BaseResponse<List<BootcampCompletedResponse>>>()
                            .addProperty("status", new IntegerSchema().format("int32"))
                            .addProperty("message", new StringSchema())
                            .addProperty("path", new StringSchema())
                            .addProperty("timestamp", new StringSchema().format("date-time"))
                            .addProperty("data", new Schema<>().type("array")
                                    .items(new Schema<>().$ref("#/components/schemas/BootcampCompletedResponse"))));

            PathItem saveBootcampPath = new PathItem()
                    .post(new Operation()
                            .operationId("saveBootcamp")
                            .tags(List.of("Bootcamp"))
                            .summary("Create a new bootcamp")
                            .description("Creates a new bootcamp with the provided information and validates that all capacities exist")
                            .requestBody(new RequestBody()
                                    .description("Bootcamp payload")
                                    .required(true)
                                    .content(new Content().addMediaType("application/json",
                                            new io.swagger.v3.oas.models.media.MediaType()
                                                    .schema(new Schema<>().$ref("#/components/schemas/BootcampRequest")))))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("Bootcamp created successfully")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/BootcampSuccessResponse")))))
                                    .addApiResponse("400", new ApiResponse()
                                            .description("Validation error - Invalid bootcamp data or capacity not found")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                            )
                    )
                    .get(new Operation()
                            .operationId("getAllBootcampWithCapacities")
                            .tags(List.of("Bootcamp"))
                            .summary("Get all bootcamps with their capacities")
                            .description("Retrieves a paginated list of all bootcamps along with their associated capacities and technologies")
                            .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                    .name("page")
                                    .in("query")
                                    .description("Page number (0-based)")
                                    .required(false)
                                    .schema(new IntegerSchema().format("int32")._default(0)))
                            .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                    .name("size")
                                    .in("query")
                                    .description("Number of items per page")
                                    .required(false)
                                    .schema(new IntegerSchema().format("int32")._default(10)))
                            .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                    .name("sort")
                                    .in("query")
                                    .description("Sort criteria in the format: property,direction (e.g., name,asc)")
                                    .required(false)
                                    .schema(new StringSchema()._default("name,asc")))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("List of bootcamps retrieved successfully")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/BootcampListResponse")))))
                                    .addApiResponse("500", new ApiResponse()
                                            .description("Internal server error")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                            )
                    );

            PathItem deleteBootcampPath = new PathItem()
                    .delete(new Operation()
                            .operationId("deleteBootcamp")
                            .tags(List.of("Bootcamp"))
                            .summary("Delete a bootcamp by ID")
                            .description("Deletes an existing bootcamp and all its associated capacity relationships")
                            .addParametersItem(new io.swagger.v3.oas.models.parameters.Parameter()
                                    .name("id")
                                    .in("path")
                                    .description("The ID of the bootcamp to delete")
                                    .required(true)
                                    .schema(new Schema<>().type("integer").format("int64")))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("Bootcamp deleted successfully")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/BootcampSuccessResponse")))))
                                    .addApiResponse("404", new ApiResponse()
                                            .description("Bootcamp not found")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                                    .addApiResponse("500", new ApiResponse()
                                            .description("Internal server error")
                                            .content(new Content().addMediaType("application/json",
                                                    new io.swagger.v3.oas.models.media.MediaType()
                                                            .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))))
                            )
                    );

            openApi.path("/api/v1/bootcamp", saveBootcampPath);
            openApi.path("/api/v1/bootcamp/{id}", deleteBootcampPath);
        };
    }
}
