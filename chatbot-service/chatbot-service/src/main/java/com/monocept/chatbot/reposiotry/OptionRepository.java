package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OptionRepository  extends JpaRepository<Option, Long> {

    @Query(value = "SELECT name FROM option WHERE active = true", nativeQuery = true)
    List<String> findActiveOptionNames();

}
