package com.monocept.chatbot.reposiotry;
import com.monocept.chatbot.model.dto.HistoryDTO;
import com.monocept.chatbot.Entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ChatHistoryRepository extends JpaRepository<History, Long> {

  //  @Query(value = "SELECT * FROM chat_history c WHERE c.date_time >= :dateTime AND c.email = :email", nativeQuery = true)
   // List<History> findMessagesFromLast90DayswrtEmail(LocalDateTime dateTime, String email);

    @Query("SELECT new com.monocept.chatbot.model.dto.HistoryDTO(c.msgId, c.msg, c.messageTo, c.dateTime, c.replyId, c.type, c.mediaUrl, c.activity) FROM History c WHERE c.dateTime >= :dateTime AND c.email = :email order by dateTime DESC" )
    Page<HistoryDTO> findMessagesFromLast90DayswrtEmail(@Param("dateTime") LocalDateTime dateTime, @Param("email") String email, Pageable pageable );

}
