package com.railwaymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_number", nullable = false, unique = true, length = 10)
    private String trainNumber;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Coach> coaches = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Route> routes = new ArrayList<>();
}
