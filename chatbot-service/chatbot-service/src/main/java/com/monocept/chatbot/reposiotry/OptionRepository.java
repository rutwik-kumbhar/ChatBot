package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import com.monocept.chatbot.model.dto.NameIconDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository  extends JpaRepository<Option, Long> {

    @Query("SELECT new com.monocept.chatbot.model.dto.NameIconDto(o.name, o.icon) FROM PlaceHolder o")
    List<NameIconDto> findOptionNames();


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM option WHERE name IN :names", nativeQuery = true)
    int deleteByOptionNames(@Param("names") List<String> names);


    @Async
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM option", nativeQuery = true)
    void deleteAllOptions();

}
