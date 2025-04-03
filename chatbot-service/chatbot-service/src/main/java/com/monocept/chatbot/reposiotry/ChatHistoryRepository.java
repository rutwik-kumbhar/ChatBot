package com.monocept.chatbot.reposiotry;
import com.monocept.chatbot.model.dto.HistoryDTO;
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
public interface ChatHistoryRepository extends JpaRepository<com.monocept.chatbot.entity.Message, Long> {

  //  @Query(value = "SELECT * FROM chat_history c WHERE c.date_time >= :dateTime AND c.email = :email", nativeQuery = true)
   // List<History> findMessagesFromLast90DayswrtEmail(LocalDateTime dateTime, String email);

  //  @Query("SELECT new com.monocept.chatbot.model.dto.HistoryDTO(c.id, c.messageId, c.messageTo, c.text, c.action) FROM message c WHERE c.createdAt >= :createdAt AND c.email = :email order by createdAt DESC" )
    //Page<HistoryDTO> findMessagesFromLast90DayswrtEmail(@Param("createdAt") ZonedDateTime createdAt, @Param("email") String email, Pageable pageable );

  //  void deleteByDateTimeBefore(LocalDateTime dateTime);
 // @Modifying
  //@Query("DELETE FROM message h WHERE h.dateTime < :dateTime")
  //void deleteByDateTimeBefore(@Param("dateTime") LocalDateTime dateTime);

}
