package co.com.backend.reactive.api;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.backend.reactive.usecase.bootcamp.IBootcampUseCase;
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

    public Mono<ServerResponse> createBootcamp(ServerRequest serverRequest) {
        String path = serverRequest.path();
        
        return serverRequest.bodyToMono(BootcampRequestDTO.class)
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
}
