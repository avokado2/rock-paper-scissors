package org.avokado2.rps.dao;

import org.avokado2.rps.model.ChatMessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepositry extends CrudRepository<ChatMessageEntity, Long> {

    Page<ChatMessageEntity> findByGameId(long gameId, Pageable pageable);
}
