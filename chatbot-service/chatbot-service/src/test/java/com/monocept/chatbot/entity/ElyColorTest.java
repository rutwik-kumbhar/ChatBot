package com.monocept.chatbot.entity;

import com.monocept.chatbot.Entity.ElyColor;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ElyColorTest {

    @Test
    void testConstructor() {
        ZonedDateTime now = ZonedDateTime.now();

        ElyColor color = new ElyColor();
        color.setThemeName("dark");
        color.setPlatform("web");
        color.setBackgroundColor("#000");
        color.setUserMessageColor("#fff");
        color.setBotMessageColor("#eee");
        color.setBorderColor("#ddd");
        color.setButtonColor("#ccc");
        color.setCoachOptionColor("#bbb");
        color.setBotOptionColor("#aaa");
        color.setCreatedAt(now);
        color.setUpdatedAt(now);
        color.setActive(true);

        // Test that values are correctly set
        assertEquals("dark", color.getThemeName());
        assertEquals("web", color.getPlatform());
        assertEquals("#000", color.getBackgroundColor());
        assertEquals("#fff", color.getUserMessageColor());
        assertEquals("#eee", color.getBotMessageColor());
        assertEquals("#ddd", color.getBorderColor());
        assertEquals("#ccc", color.getButtonColor());
        assertEquals("#bbb", color.getCoachOptionColor());
        assertEquals("#aaa", color.getBotOptionColor());
        assertEquals(now, color.getCreatedAt());
        assertEquals(now, color.getUpdatedAt());
        assertTrue(color.isActive());
    }

    @Test
    void testEquality() {
        ElyColor color1 = new ElyColor();
        color1.setThemeName("dark");
        color1.setPlatform("mspace");

        ElyColor color2 = new ElyColor();
        color2.setThemeName("dark");
        color2.setPlatform("mspace");

        // Test that two entities with the same values are equal
        assertEquals(color1, color2);
    }

    @Test
    void testNotEqual() {
        ElyColor color1 = new ElyColor();
        color1.setThemeName("dark");
        color1.setPlatform("mspace");

        ElyColor color2 = new ElyColor();
        color2.setThemeName("light");
        color2.setPlatform("mspace");

        // Test that two entities with different values are not equal
        assertNotEquals(color1, color2);
    }

    @Test
    void testHashCode() {
        ElyColor color1 = new ElyColor();
        color1.setThemeName("dark");
        color1.setPlatform("mspace");

        ElyColor color2 = new ElyColor();
        color2.setThemeName("dark");
        color2.setPlatform("mspace");

        // Test that hashCodes are the same for equal objects
        assertEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    void testToString() {
        ZonedDateTime now = ZonedDateTime.now();

        ElyColor color = new ElyColor();
        color.setThemeName("dark");
        color.setPlatform("mspace");
        color.setCreatedAt(now);
        color.setUpdatedAt(now);
        color.setActive(true);

        // Ensure that toString method doesn't throw exceptions
        assertNotNull(color.toString());
        assertTrue(color.toString().contains("dark"));
        assertTrue(color.toString().contains("mspace"));
    }

}
