package com.monocept.chatbot.reposiotry;


import com.monocept.chatbot.entity.PlaceHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceholderRepository extends JpaRepository<PlaceHolder, Long> {

    @Query(value = "SELECT name FROM placeholder WHERE active = true", nativeQuery = true)
    List<String> findActivePlaceholderNames();
}
