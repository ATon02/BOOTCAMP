package co.com.backend.reactive.api;

import co.com.backend.reactive.api.helper.ConstHandler;
import co.com.backend.reactive.api.helper.ValidatorRequest;
import co.com.backend.reactive.usecase.bootcamp.enums.BootcampError;
import co.com.backend.reactive.usecase.bootcamp.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.backend.reactive.usecase.bootcamp.IBootcampUseCase;
import co.com.backend.reactive.usecase.bootcamp.dto.BootcampCompletedResponse;
import co.com.backend.reactive.api.mapper.BootcampDTOMapper;
import co.com.backend.reactive.api.dtos.request.BootcampRequestDTO;
import co.com.backend.reactive.api.dtos.response.BootcampResponseDTO;
import co.com.backend.reactive.api.dtos.response.BaseResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final IBootcampUseCase bootcampUseCase;
    private final BootcampDTOMapper bootcampDTOMapper;
    private final ValidatorRequest validatorRequest;

    public Mono<ServerResponse> createBootcamp(ServerRequest serverRequest) {
        String path = serverRequest.path();
        
        return serverRequest.bodyToMono(BootcampRequestDTO.class)
                .doOnNext(validatorRequest::validateDTO)
                .map(bootcampDTOMapper::toModel)
                .flatMap(bootcampUseCase::save)
                .map(bootcampDTOMapper::toResponseDTO)
                .flatMap(bootcamp -> {
                    BaseResponse<BootcampResponseDTO> body = BaseResponse.<BootcampResponseDTO>builder()
                            .status(200)
                            .message("Bootcamp created successfully")
                            .path(path)
                            .timestamp(LocalDateTime.now())
                            .data(bootcamp)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });
    }

    public Mono<ServerResponse> getAllBootcampWithCapacities(ServerRequest serverRequest) {
        String path = serverRequest.path();
        int page = Integer.parseInt(serverRequest.queryParam(ConstHandler.PAGE_PARAM.getParameterName()).orElse(ConstHandler.PAGE_PARAM.getDefaultValue()));
        int size = Integer.parseInt(serverRequest.queryParam(ConstHandler.SIZE_PARAM.getParameterName()).orElse(ConstHandler.SIZE_PARAM.getDefaultValue()));
        String sort = serverRequest.queryParam(ConstHandler.SORT_PARAM.getParameterName()).orElse(ConstHandler.SORT_PARAM.getDefaultValue());

        String[] sortParts = sort.split(",");
        String sortBy = sortParts.length > 0 ? sortParts[0] : ConstHandler.SORT_BY_PARAM.getDefaultValue();
        String sortDirection = sortParts.length > 1 ? sortParts[1] : ConstHandler.SORT_DIRECTION_PARAM.getDefaultValue();

        return bootcampUseCase.getAllBootcampWithCapacities(page, size, sortBy, sortDirection)
                .collectList()
                .flatMap(response -> {
                    BaseResponse<List<BootcampCompletedResponse>> body =
                            BaseResponse.<List<BootcampCompletedResponse>>builder()
                            .status(200)
                            .message("Bootcamps retrieved successfully")
                            .path(path)
                            .timestamp(LocalDateTime.now())
                            .data(response)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });
    }

    public Mono<ServerResponse> deleteBootcamp(ServerRequest serverRequest) {
        String path = serverRequest.path();
        String idBootcamp = serverRequest.pathVariable(ConstHandler.Id_PARAM.getParameterName());
        return bootcampUseCase.deleteBootcamp(Long.valueOf(idBootcamp))
                .then(Mono.defer(() -> {
                    BaseResponse<Void> body = BaseResponse.<Void>builder()
                            .status(200)
                            .message("Bootcamp deleted successfully")
                            .path(path)
                            .timestamp(LocalDateTime.now())
                            .data(null)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                }));
    }

    public Mono<ServerResponse> getBootcampWithCapacitiesByIds(ServerRequest serverRequest) {
        String path = serverRequest.path();
        String idsParam = serverRequest.queryParam(ConstHandler.IDs_PARAM.getParameterName()).orElse(ConstHandler.IDs_PARAM.getDefaultValue());
        return Mono.fromCallable(() -> {
                    if (idsParam.isEmpty()) {
                        throw new BusinessException(BootcampError.BOOTCAMP_ID_REQUIRED.getMessage());
                    }
                    return Arrays.stream(idsParam.split(","))
                            .map(String::trim)
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                })
                .onErrorMap(NumberFormatException.class,
                    ex -> new BusinessException(BootcampError.INVALID_ID.getMessage()))
                .flatMapMany(bootcampUseCase::getBootcampWithCapacitiesByIds)
                .collectList()
                .flatMap(response -> {
                    BaseResponse<List<BootcampCompletedResponse>> body =
                            BaseResponse.<List<BootcampCompletedResponse>>builder()
                            .status(200)
                            .message("Bootcamps retrieved successfully")
                            .path(path)
                            .timestamp(LocalDateTime.now())
                            .data(response)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });
    }

    public Mono<ServerResponse> getBootcampById(ServerRequest serverRequest) {
        String path = serverRequest.path();
        String idBootcamp = serverRequest.pathVariable(ConstHandler.Id_PARAM.getParameterName());

        return Mono.fromCallable(() -> Long.parseLong(idBootcamp))
                .onErrorMap(NumberFormatException.class,
                    ex -> new BusinessException(BootcampError.INVALID_ID.getMessage()))
                .flatMap(bootcampUseCase::getBootcampById)
                .map(bootcampDTOMapper::toResponseDTO)
                .flatMap(bootcamp -> {
                    BaseResponse<BootcampResponseDTO> body = BaseResponse.<BootcampResponseDTO>builder()
                            .status(200)
                            .message("Bootcamp found successfully")
                            .path(serverRequest.path())
                            .timestamp(LocalDateTime.now())
                            .data(bootcamp)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(body);
                });
    }
}
