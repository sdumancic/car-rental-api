package car.rental.core.reservation.infrastructure.mapper;

import car.rental.core.reservation.domain.model.Reservation;
import car.rental.core.reservation.infrastructure.persistence.ReservationEntity;
import car.rental.core.users.infrastructure.mapper.UserMapper;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;

import java.time.Instant;

public class ReservationMapper {

    public static Reservation toDomain(ReservationEntity entity) {
        if (entity == null) {
            return null;
        }

        return Reservation.builder()
                .id(entity.getId())
                .user(UserMapper.toDomain(entity.getUser()))
                .vehicle(VehicleMapper.toDomain(entity.getVehicle()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .dateCreated(entity.getDateCreated())
                .dateModified(entity.getDateModified())
                .build();
    }

    public static ReservationEntity toEntity(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationEntity entity = new ReservationEntity();
        entity.setId(reservation.getId());
        entity.setUser(UserMapper.toEntity(reservation.getUser()));
        entity.setVehicle(VehicleMapper.toEntity(reservation.getVehicle()));
        entity.setStartDate(reservation.getStartDate());
        entity.setEndDate(reservation.getEndDate());
        entity.setPrice(reservation.getPrice());
        entity.setStatus(reservation.getStatus());
        entity.setDateCreated(reservation.getDateCreated() != null ? reservation.getDateCreated() : Instant.now());
        entity.setDateModified(Instant.now());
        entity.setActive(true);

        return entity;
    }
}
