package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OptionRepository  extends JpaRepository<Option, Long> {

    @Query(value = "SELECT name FROM option", nativeQuery = true)
    List<String> findOptionNames();

    @Modifying
    @Transactional
    @Query(value = "UPDATE option SET name = :newValue WHERE name = :oldValue", nativeQuery = true)
    int updateOptionByName(@Param("newValue") String newValue, @Param("oldValue") String oldValue);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM option WHERE name IN :names", nativeQuery = true)
    int deleteByOptionNames(@Param("names") List<String> names);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM option", nativeQuery = true)
    void deleteAllOptions();

}
