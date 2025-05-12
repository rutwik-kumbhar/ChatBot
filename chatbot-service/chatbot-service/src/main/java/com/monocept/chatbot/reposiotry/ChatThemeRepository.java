package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.entity.Theme;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatThemeRepository extends JpaRepository<Theme, Long> {
     Optional<Theme> findByIsActiveTrueAndPlatformIgnoreCase(String platform);
     Optional<Theme> findByThemeNameIgnoreCaseAndPlatformIgnoreCase(String themeName, String platform);
     List<Theme> findAllByPlatformIgnoreCase(String platform);
    @Modifying
    @Transactional
    @Query("UPDATE Theme t SET t.isActive = :status WHERE t.platform = :platform")
    int updateThemeStatus(boolean status,  String platform);



}