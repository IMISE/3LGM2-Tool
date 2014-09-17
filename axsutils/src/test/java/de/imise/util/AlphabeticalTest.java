package de.imise.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public class AlphabeticalTest {

    @Test
    public void SimpleTest() {
        String firstString = "Hallo";
        String secondString = "Leute";
        Pair<String, String> pair = new Pair<String, String>(firstString, secondString);
        pair.setFirstItem(firstString);
        pair.setSecondItem(secondString);
        assertEquals(pair.getFirstItem(), firstString);
        assertEquals(pair.getSecondItem(), secondString);
        assertNotNull(pair);
    }

}
