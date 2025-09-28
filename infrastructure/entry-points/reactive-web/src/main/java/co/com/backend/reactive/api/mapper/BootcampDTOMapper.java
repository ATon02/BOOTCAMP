package co.com.backend.reactive.api.mapper;

import co.com.backend.reactive.model.bootcamp.Bootcamp;
import co.com.backend.reactive.api.dtos.request.BootcampRequestDTO;
import co.com.backend.reactive.api.dtos.response.BootcampResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampDTOMapper {

    Bootcamp toModel(BootcampRequestDTO requestDTO);

    BootcampResponseDTO toResponseDTO(Bootcamp bootcamp);
}