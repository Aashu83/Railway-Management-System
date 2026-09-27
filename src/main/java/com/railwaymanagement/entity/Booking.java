package com.railwaymanagement.entity;

import com.railwaymanagement.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints = {
        // Prevents two confirmed bookings from claiming the same seat on the same
        // schedule and journey date — the concurrency guard lives at the DB level,
        // not just in application code.
        @UniqueConstraint(columnNames = {"schedule_id", "seat_id", "journey_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, unique = true, length = 20)
    private String pnr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    // When the booking was made — used for cancellation-window / refund policy
    // and lead-time analytics. Distinct from journeyDate below.
    @Column(name = "booking_date", updatable = false)
    private LocalDateTime bookingDate;

    // The actual date of travel
    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @PrePersist
    protected void onCreate() {
        this.bookingDate = LocalDateTime.now();
    }
}
