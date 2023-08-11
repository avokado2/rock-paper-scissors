package org.avokado2.rps.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = PlayerEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    private PlayerEntity player1;

    @ManyToOne(targetEntity = PlayerEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private PlayerEntity player2;

    private int score1;

    private int score2;

    private int currentRound;

    private int roundsCount;

    @Column(name = "start_at", insertable = false, updatable = false)
    private Date startAt;

    @Column(name = "update_at", insertable = false, updatable = false)
    private Date updateAt;

    private boolean completed;

    private boolean pause;
}
