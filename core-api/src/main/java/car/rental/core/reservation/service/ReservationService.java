package car.rental.core.reservation.service;

import car.rental.core.common.dto.PageResponse;
import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.domain.repository.PricingRepository;
import car.rental.core.reservation.domain.model.Reservation;
import car.rental.core.reservation.domain.model.ReservationStatus;
import car.rental.core.reservation.domain.repository.ReservationRepository;
import car.rental.core.reservation.dto.CalculatePriceRequest;
import car.rental.core.reservation.dto.CreateReservationRequest;
import car.rental.core.reservation.dto.QueryReservationRequest;
import car.rental.core.reservation.dto.UpdateReservationRequest;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.domain.repository.UserRepository;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.domain.repository.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class ReservationService {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    VehicleRepository vehicleRepository;

    @Inject
    PricingRepository pricingRepository;

    @Transactional
    public Reservation createReservation(CreateReservationRequest request) {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Check availability
        if (!reservationRepository.isVehicleAvailable(request.getVehicleId(), request.getStartDate(), request.getEndDate(), null)) {
            throw new RuntimeException("Vehicle is not available for the selected dates");
        }

        // Calculate price
        BigDecimal price = calculatePrice(request.getVehicleId(), request.getStartDate(), request.getEndDate());

        Reservation reservation = Reservation.builder()
                .user(user)
                .vehicle(vehicle)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .price(price)
                .status(ReservationStatus.PENDING)
                .dateCreated(Instant.now())
                .dateModified(Instant.now())
                .build();

        return reservationRepository.save(reservation);
    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public PageResponse<Reservation> findReservations(QueryReservationRequest query) {
        List<Reservation> reservations = reservationRepository.findByQuery(query);
        long totalRecords = reservationRepository.countByQuery(query);

        return PageResponse.<Reservation>builder()
                .data(reservations)
                .metadata(PageResponse.PageMetadata.builder()
                        .page(query.getPage())
                        .size(query.getSize())
                        .totalRecords(totalRecords)
                        .currentPageUrl(buildPageUrl(query, query.getPage()))
                        .prevPageUrl(query.getPage() > 0 ? buildPageUrl(query, query.getPage() - 1) : null)
                        .nextPageUrl((query.getPage() + 1) * query.getSize() < totalRecords ? buildPageUrl(query, query.getPage() + 1) : null)
                        .build())
                .build();
    }

    @Transactional
    public Reservation updateReservation(Long id, UpdateReservationRequest request) {
        Reservation existing = findReservationById(id);
        if (existing == null) {
            throw new RuntimeException("Reservation not found");
        }

        // Validate user exists if provided
        User user = existing.getUser();
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // Validate vehicle exists if provided
        Vehicle vehicle = existing.getVehicle();
        if (request.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        }

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : existing.getStartDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : existing.getEndDate();

        // Check availability if dates or vehicle changed
        if ((request.getVehicleId() != null && !request.getVehicleId().equals(existing.getVehicle().getId())) ||
                (request.getStartDate() != null && !request.getStartDate().equals(existing.getStartDate())) ||
                (request.getEndDate() != null && !request.getEndDate().equals(existing.getEndDate()))) {
            if (!reservationRepository.isVehicleAvailable(vehicle.getId(), startDate, endDate, id)) {
                throw new RuntimeException("Vehicle is not available for the selected dates");
            }
        }

        // Recalculate price if dates changed
        BigDecimal price = existing.getPrice();
        if (request.getStartDate() != null || request.getEndDate() != null) {
            price = calculatePrice(vehicle.getId(), startDate, endDate);
        }

        Reservation updated = Reservation.builder()
                .id(id)
                .user(user)
                .vehicle(vehicle)
                .startDate(startDate)
                .endDate(endDate)
                .price(price)
                .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
                .dateCreated(existing.getDateCreated())
                .dateModified(Instant.now())
                .build();

        return reservationRepository.update(updated);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.softDeleteById(id);
    }

    public BigDecimal calculateReservationPrice(CalculatePriceRequest request) {
        return calculatePrice(request.getVehicleId(), request.getStartDate(), request.getEndDate());
    }

    private BigDecimal calculatePrice(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        // Find active pricing for the vehicle
        Pricing pricing = pricingRepository.findActiveByVehicleId(vehicleId).orElse(null);
        if (pricing == null || pricing.getPricingCategory() == null) {
            throw new RuntimeException("No pricing found for the vehicle");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Inclusive days

        // Find the appropriate pricing tier
        return pricing.getPricingCategory().getPricingTiers().stream()
                .filter(tier -> days >= tier.getMinDays() && (tier.getMaxDays() == null || days <= tier.getMaxDays()))
                .findFirst()
                .map(tier -> tier.getPrice().multiply(BigDecimal.valueOf(days)))
                .orElseThrow(() -> new RuntimeException("No pricing tier found for " + days + " days"));
    }

    private String buildPageUrl(QueryReservationRequest query, int page) {
        StringBuilder url = new StringBuilder("/v1/reservations?page=").append(page).append("&size=").append(query.getSize());

        if (query.getUserId() != null) {
            url.append("&userId=").append(query.getUserId());
        }

        if (query.getVehicleId() != null) {
            url.append("&vehicleId=").append(query.getVehicleId());
        }

        if (query.getStartDate() != null) {
            url.append("&startDate=").append(query.getStartDate());
        }

        if (query.getEndDate() != null) {
            url.append("&endDate=").append(query.getEndDate());
        }

        if (query.getStatus() != null) {
            url.append("&status=").append(query.getStatus());
        }

        if (query.getSort() != null && !query.getSort().trim().isEmpty()) {
            url.append("&sort=").append(URLEncoder.encode(query.getSort().trim(), StandardCharsets.UTF_8));
        }

        return url.toString();
    }
}
