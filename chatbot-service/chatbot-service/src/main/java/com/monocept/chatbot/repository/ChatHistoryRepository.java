package com.monocept.chatbot.repository;

import com.monocept.chatbot.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, String> {

    @Query("SELECT c FROM ChatHistory c WHERE c.email = :email ORDER BY c.dateTime ASC")
    List<ChatHistory> findByEmailOrderByDateTimeAsc(String email);
}

