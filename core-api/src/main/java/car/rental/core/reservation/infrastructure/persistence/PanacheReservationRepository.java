package car.rental.core.reservation.infrastructure.persistence;

import car.rental.core.reservation.domain.model.Reservation;
import car.rental.core.reservation.domain.repository.ReservationRepository;
import car.rental.core.reservation.dto.QueryReservationRequest;
import car.rental.core.reservation.infrastructure.mapper.ReservationMapper;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PanacheReservationRepository implements ReservationRepository {

    @Inject
    ReservationEntityRepository reservationEntityRepository;

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationEntityRepository.findByIdOptional(id)
                .filter(entity -> entity.getActive())
                .map(ReservationMapper::toDomain);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationEntityRepository.find("active", true)
                .stream()
                .map(ReservationMapper::toDomain)
                .toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationMapper.toEntity(reservation);
        reservationEntityRepository.persist(entity);
        return ReservationMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        reservationEntityRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findByQuery(QueryReservationRequest query) {
        StringBuilder queryStr = new StringBuilder("active = :active");
        Parameters params = Parameters.with("active", true);

        if (query.getUserId() != null) {
            queryStr.append(" and user.id = :userId");
            params.and("userId", query.getUserId());
        }

        if (query.getVehicleId() != null) {
            queryStr.append(" and vehicle.id = :vehicleId");
            params.and("vehicleId", query.getVehicleId());
        }

        if (query.getStartDate() != null) {
            queryStr.append(" and startDate >= :startDate");
            params.and("startDate", query.getStartDate());
        }

        if (query.getEndDate() != null) {
            queryStr.append(" and endDate <= :endDate");
            params.and("endDate", query.getEndDate());
        }

        if (query.getStatus() != null) {
            queryStr.append(" and status = :status");
            params.and("status", query.getStatus());
        }

        Sort sort = query.getSort() != null && !query.getSort().isEmpty()
                ? Sort.ascending(query.getSort())
                : Sort.descending("dateCreated");

        return reservationEntityRepository.find(queryStr.toString(), sort, params)
                .page(query.getPage(), query.getSize())
                .list()
                .stream()
                .map(ReservationMapper::toDomain)
                .toList();
    }

    @Override
    public long countByQuery(QueryReservationRequest query) {
        StringBuilder queryStr = new StringBuilder("active = :active");
        Parameters params = Parameters.with("active", true);

        if (query.getUserId() != null) {
            queryStr.append(" and user.id = :userId");
            params.and("userId", query.getUserId());
        }

        if (query.getVehicleId() != null) {
            queryStr.append(" and vehicle.id = :vehicleId");
            params.and("vehicleId", query.getVehicleId());
        }

        if (query.getStartDate() != null) {
            queryStr.append(" and startDate >= :startDate");
            params.and("startDate", query.getStartDate());
        }

        if (query.getEndDate() != null) {
            queryStr.append(" and endDate <= :endDate");
            params.and("endDate", query.getEndDate());
        }

        if (query.getStatus() != null) {
            queryStr.append(" and status = :status");
            params.and("status", query.getStatus());
        }

        return reservationEntityRepository.count(queryStr.toString(), params);
    }

    @Override
    public Reservation update(Reservation reservation) {
        ReservationEntity entity = ReservationMapper.toEntity(reservation);
        reservationEntityRepository.getEntityManager().merge(entity);
        return ReservationMapper.toDomain(entity);
    }

    @Override
    public void softDeleteById(Long id) {
        reservationEntityRepository.findByIdOptional(id).ifPresent(entity -> {
            entity.setActive(false);
            reservationEntityRepository.persist(entity);
        });
    }

    @Override
    public boolean isVehicleAvailable(Long vehicleId, Instant startDate, Instant endDate, Long excludeReservationId) {
        StringBuilder queryStr = new StringBuilder("active = :active and vehicle.id = :vehicleId and status != :cancelledStatus");
        Parameters params = Parameters.with("active", true)
                .and("vehicleId", vehicleId)
                .and("cancelledStatus", car.rental.core.reservation.domain.model.ReservationStatus.CANCELLED);

        // Overlap conditions
        queryStr.append(" and ((startDate <= :startDate and endDate >= :startDate) or (startDate <= :endDate and endDate >= :endDate) or (startDate >= :startDate and endDate <= :endDate))");
        params.and("startDate", startDate).and("endDate", endDate);

        if (excludeReservationId != null) {
            queryStr.append(" and id != :excludeId");
            params.and("excludeId", excludeReservationId);
        }

        long count = reservationEntityRepository.count(queryStr.toString(), params);
        return count == 0;
    }
}
