package com.monocept.chatbot.reposiotry;
import com.monocept.chatbot.Entity.Message;
import com.monocept.chatbot.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
@Repository
public interface ChatHistoryRepository extends JpaRepository<Message, Long> {

  //  @Query(value = "SELECT * FROM chat_history c WHERE c.date_time >= :dateTime AND c.email = :email", nativeQuery = true)
   // List<History> findMessagesFromLast90DayswrtEmail(LocalDateTime dateTime, String email);

 //  @Query("SELECT new com.monocept.chatbot.model.dto.HistoryDTO(c.msgId, c.msg, c.messageTo, c.dateTime, c.replyId, c.type, c.mediaUrl, c.activity) FROM History c WHERE c.dateTime >= :dateTime AND c.email = :email  group by dateTime,msgId" )
   // Page<HistoryDTO> findMessagesFromDayswrtEmail(@Param("dateTime") LocalDateTime dateTime, @Param("email") String email, Pageable pageable );
    @Query("SELECT new com.monocept.chatbot.model.dto.MessageDto(" +
            "c.id, " +
            "c.userId, " +
            "c.email, " +
            "c.sendType, " +
            "c.messageType, " +
            "c.messageId, " +
            "c.messageTo, " +
            "c.text, " +
            "c.replyToMessageId, " +
            "c.status, " +
            "c.emoji, " +
            "c.action, " +
            "c.media, " +
            "c.options, " +
            "c.botOptions, " +
            "c.platform, " +
            "c.createdAt) " +
            "FROM Message c WHERE c.createdAt > :createdAt AND c.email = :email " +
            "ORDER BY c.createdAt DESC")
    Page<MessageDto> findMessagesFromDayswrtEmail(@Param("createdAt") ZonedDateTime createdAt, @Param("email") String email, Pageable pageable);

    //  void deleteByDateTimeBefore(LocalDateTime dateTime);
  @Modifying
  @Query("DELETE FROM Message h WHERE h.createdAt < :createdAt")
  void deleteByDateTimeBefore(@Param("createdAt") ZonedDateTime createdAt);

}
