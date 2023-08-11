package org.avokado2.rps.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "game_round")
public class GameRoundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = GameEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameEntity game;

    private int roundNumber;

    @Enumerated(EnumType.STRING)
    private GameChoice choice1;

    @Enumerated(EnumType.STRING)
    private GameChoice choice2;

    @Column(name = "create_ts", insertable = false, updatable = false)
    private Date createTs;

    @Column(name = "update_ts", insertable = false, updatable = false)
    private Date updateTs;

    private Integer winner;
}


