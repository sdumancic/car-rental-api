package car.rental.core.reservation.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.reservation.domain.model.Reservation;
import car.rental.core.reservation.dto.QueryReservationRequest;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends BaseRepository<Reservation> {
    List<Reservation> findByQuery(QueryReservationRequest query);

    long countByQuery(QueryReservationRequest query);

    Reservation update(Reservation reservation);

    void softDeleteById(Long id);

    boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate, Long excludeReservationId);
}
