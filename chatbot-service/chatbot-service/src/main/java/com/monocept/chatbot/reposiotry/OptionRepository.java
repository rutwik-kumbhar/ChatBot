package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository  extends JpaRepository<Option, Long> {

    List<Option> findByActiveTrue();

}
