package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(String messageId);

    //  @Query(value = "SELECT * FROM chat_history c WHERE c.date_time >= :dateTime AND c.email = :email", nativeQuery = true)
   // List<History> findMessagesFromLast90DayswrtEmail(LocalDateTime dateTime, String email);

//    @Query("SELECT new com.monocept.chatbot.model.dto.HistoryDTO(c.msgId, c.msg, c.messageTo, c.dateTime, c.replyId, c.type, c.mediaUrl, c.activity) FROM History c WHERE c.dateTime >= :dateTime AND c.email = :email order by dateTime DESC" )
//    Page<HistoryDTO> findMessagesFromLast90DayswrtEmail(@Param("dateTime") LocalDateTime dateTime, @Param("email") String email, Pageable pageable );


    @Query(value = "SELECT COUNT(*) FROM message WHERE message_to = 'BOT' AND status = 'DELIVERED' AND user_id = :userId", nativeQuery = true)
    long countDeliveredBotMessagesByAgentId(String userId);
}
