package com.monocept.chatbot.reposiotry;

import com.monocept.chatbot.Entity.ElyColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatThemeRepository extends JpaRepository<ElyColor, Long> {
     Optional<ElyColor> findByThemeNameIgnoreCase(String themeName);
     Optional<ElyColor> findByIsActiveTrue();
     Optional<ElyColor> findByIsActiveTrueAndPlatformIgnoreCase(String platform);
     Optional<ElyColor> findByThemeNameIgnoreCaseAndPlatformIgnoreCase(String themeName, String platform);
     List<ElyColor> findAllByPlatformIgnoreCase(String platform);

}