package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.entity.PlaceHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceholderRepository extends JpaRepository<PlaceHolder, Long> {

    List<PlaceHolder> findByActiveTrue();
}
