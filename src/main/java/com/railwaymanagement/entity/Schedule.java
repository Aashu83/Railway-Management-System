package com.railwaymanagement.entity;

import com.railwaymanagement.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // Timetabled times — what's printed on the schedule
    @Column(name = "sched_departure", nullable = false)
    private LocalDateTime scheduledDeparture;

    @Column(name = "sched_arrival", nullable = false)
    private LocalDateTime scheduledArrival;

    // Live/estimated times — defaults to scheduled, updates on delay
    @Column(name = "est_departure")
    private LocalDateTime estimatedDeparture;

    @Column(name = "est_arrival")
    private LocalDateTime estimatedArrival;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.ON_TIME;
}
