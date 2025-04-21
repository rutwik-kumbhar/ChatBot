package com.monocept.chatbot.reposiotry;
import com.monocept.chatbot.entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
@Repository
public interface ChatHistoryRepository extends JpaRepository<Message, Long> {

  @Modifying
  @Transactional
  @Query("DELETE FROM Message h WHERE h.createdAt < :createdAt")
  void deleteByDateTimeBefore(@Param("createdAt") ZonedDateTime createdAt);

 Page<Message> findByEmail(String email, Pageable pageable);

}
