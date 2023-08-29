package org.avokado2.rps.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "timestamp", insertable = false, updatable = false)
    private Date timestamp;

    private long gameId;

    @ManyToOne(targetEntity = PlayerEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    @ManyToOne(targetEntity = PlayerEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private PlayerEntity recipient;
}
