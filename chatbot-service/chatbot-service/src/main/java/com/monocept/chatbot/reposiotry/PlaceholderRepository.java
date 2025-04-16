package com.monocept.chatbot.reposiotry;


import com.monocept.chatbot.entity.PlaceHolder;
import com.monocept.chatbot.model.dto.NameIconDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlaceholderRepository extends JpaRepository<PlaceHolder, Long> {

    @Query("SELECT new com.monocept.chatbot.model.dto.NameIconDto(p.name, p.icon) FROM PlaceHolder p")
    List<NameIconDto> findPlaceholderNames();

    @Modifying
    @Transactional
    @Query(value = "UPDATE placeholder SET option = name", nativeQuery = true)
    int updatePlaceholderByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM placeholder WHERE name = :name", nativeQuery = true)
    int  deleteByPlaceholderName(@Param("name") String name);

    @Async
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM placeholder", nativeQuery = true)
    void deleteAllOptions();

}
