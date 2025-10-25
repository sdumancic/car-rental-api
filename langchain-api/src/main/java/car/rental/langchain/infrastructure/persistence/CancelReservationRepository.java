package car.rental.langchain.infrastructure.persistence;

import car.rental.langchain.domain.model.ReservationStatus;
import car.rental.langchain.exception.Exceptions;
import dev.langchain4j.agent.tool.Tool;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CancelReservationRepository implements PanacheRepository<ReservationEntity> {

    @Tool("Cancel a booking")
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        var booking = getBookingDetails(bookingId, userId);
        // too late to cancel
        if (booking.getStartDate().minusSeconds(11 * 24 * 3600).isBefore(java.time.Instant.now())) {
            throw new Exceptions.BookingCannotBeCancelledException(bookingId, "booking from date is 11 days before today");
        }
        // too short to cancel
        if (booking.getEndDate().minusSeconds(4 * 24 * 3600).isBefore(booking.getStartDate())) {
            throw new Exceptions.BookingCannotBeCancelledException(bookingId, "booking period is less than four days");
        }
        booking.setDateModified(java.time.Instant.now());
        booking.setStatus(ReservationStatus.CANCELLED);
        booking.setActive(false);
        persist(booking);
    }

    @Tool("List booking for a user")
    @Transactional
    public List<ReservationEntity> listBookingsForUser(Long userId) {
        return list("userId = ?1 and active = true", userId);
    }

    @Tool("Get booking details")
    @Transactional
    public ReservationEntity getBookingDetails(Long bookingId, Long userId) {
        var found = findByIdOptional(bookingId)
                .orElseThrow(() -> new Exceptions.BookingNotFoundException(bookingId));

        if (!(found.getUserId().equals(userId))) {
            throw new Exceptions.BookingNotFoundException(bookingId);
        }
        return found;
    }
}
