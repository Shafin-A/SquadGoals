package com.github.shafina.squadgoals.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void testIdGetterSetter() {
        Tag tag = new Tag();
        tag.setId(1L);
        assertEquals(1L, tag.getId());
    }

    @Test
    void testNameGetterSetter() {
        Tag tag = new Tag();
        tag.setName("wellness");
        assertEquals("wellness", tag.getName());
    }
}