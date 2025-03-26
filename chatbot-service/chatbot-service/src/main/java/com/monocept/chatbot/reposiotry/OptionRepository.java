package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository  extends JpaRepository<Option, Long> {

    @Query(value = "SELECT name FROM option WHERE active = true", nativeQuery = true)
    List<String> findActiveOptionNames();

    @Modifying
    @Transactional
    @Query(value = "UPDATE option SET active = false WHERE name = :name AND active = true", nativeQuery = true)
    int deactivateOptionByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE option SET option = name WHERE name = :name AND active = true")
    int updateOptionByName(@Param("name") String name);

}
