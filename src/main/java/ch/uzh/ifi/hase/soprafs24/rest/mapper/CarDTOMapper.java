package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs24.entity.Car;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CarDTO;

/**
 * CarDTOMapper
 * This class is responsible for mapping between Car entity and CarDTO
 */
@Mapper
public interface CarDTOMapper {

    CarDTOMapper INSTANCE = Mappers.getMapper(CarDTOMapper.class);




    //what the user sends
    @Mapping(source = "carModel", target = "carModel")
    @Mapping(source = "space", target = "space")
    @Mapping(source = "supportedWeight", target = "supportedWeight")
    @Mapping(source = "electric", target = "electric")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "carPicturePath", target = "carPicturePath")
    @Mapping(target = "driver", ignore = true) // Ignore driver for mapping, handle it in service layer

    Car convertCarDTOToEntity(CarDTO carDTO);

    // what the user gets
    @Mapping(source = "carId", target = "carId")
    @Mapping(source = "carModel", target = "carModel")
    @Mapping(source = "space", target = "space")
    @Mapping(source = "supportedWeight", target = "supportedWeight")
    @Mapping(source = "electric", target = "electric")
    @Mapping(source = "licensePlate", target = "licensePlate")
    @Mapping(source = "carPicturePath", target = "carPicturePath")
    @Mapping(source = "driver.userId", target = "driverId")

    CarDTO convertEntityToCarDTO(Car car);


}