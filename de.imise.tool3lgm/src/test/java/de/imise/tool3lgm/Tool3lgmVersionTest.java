package de.imise.tool3lgm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class Tool3lgmVersionTest {

    @Test
    public void parseStringTest() {
        Tool3lgmVersion version = Tool3lgmVersion.parseString("1.2.3");
        assertEquals(version.major, 1);
        assertEquals(version.minor, 2);
        assertEquals(version.patch, 3);
        assertTrue(version.suffix.equals(""));

        version = Tool3lgmVersion.parseString("Tool3lgmVersion 1.2.3");
        assertEquals(version.major, 1);
        assertEquals(version.minor, 2);
        assertEquals(version.patch, 3);
        assertTrue(version.suffix.equals(""));

        version = Tool3lgmVersion.parseString("Tool3lgmVersion 1.2.3 (dev)");
        assertEquals(version.major, 1);
        assertEquals(version.minor, 2);
        assertEquals(version.patch, 3);
        assertEquals(version.suffix, " (dev)");

        version = Tool3lgmVersion.parseString("12.345.6789");
        assertEquals(version.major, 12);
        assertEquals(version.minor, 345);
        assertEquals(version.patch, 6789);
        assertEquals(version.patch, 6789);
        assertTrue(version.suffix.equals(""));

        //the dot after the 2 is invalid
        version = Tool3lgmVersion.parseString("Tool3lgmVersion 1.2. (dev)");
        assertNull(version);

        //
        version = Tool3lgmVersion.parseString("");
        assertNull(version);

        version = Tool3lgmVersion.parseString("Tool");
        assertNull(version);
    }

    @Test
    public void compareToTest() {
        Tool3lgmVersion version1 = Tool3lgmVersion.parseString("1.2.3");
        Tool3lgmVersion version2 = Tool3lgmVersion.parseString("1.2.4");
        assertTrue(version1.compareTo(version1) == 0);
        assertTrue(version2.compareTo(version2) == 0);
        assertTrue(version1.compareTo(version2) < 0);
        assertTrue(version2.compareTo(version1) > 0);

        version1 = Tool3lgmVersion.parseString("1.2.3_dev");
        version2 = Tool3lgmVersion.parseString("1.2.3");
        assertTrue(version1.compareTo(version1) == 0);
        assertTrue(version2.compareTo(version2) == 0);
        assertTrue(version1.compareTo(version2) < 0);
        assertTrue(version2.compareTo(version1) > 0);

        version1 = Tool3lgmVersion.parseString("IrrelevantPrefix_1.2.3_dev");
        version2 = Tool3lgmVersion.parseString("1.2.3_dev");
        assertTrue(version1.compareTo(version1) == 0);
        assertTrue(version2.compareTo(version2) == 0);
        assertTrue(version1.compareTo(version2) == 0);
    }

    @Test
    public void equalsTest() {
        Tool3lgmVersion version1 = Tool3lgmVersion.parseString("1.2.3");
        Tool3lgmVersion version2 = Tool3lgmVersion.parseString("1.2.3");
        assertTrue(version1.equals(version1));
        assertTrue(version2.equals(version2));
        assertTrue(version1.equals(version2));
        assertTrue(version2.equals(version1));

        version2 = Tool3lgmVersion.parseString("1.2.4");
        assertFalse(version1.equals(version2));
        assertFalse(version2.equals(version1));

        version2 = Tool3lgmVersion.parseString("1.2.3_dev");
        assertFalse(version1.equals(version2));
        assertFalse(version2.equals(version1));

        version1 = Tool3lgmVersion.parseString("IrrelevantPrefix_1.2.3_dev");
        version2 = Tool3lgmVersion.parseString("1.2.3_dev");
        assertTrue(version1.equals(version1));
        assertTrue(version2.equals(version2));
        assertTrue(version1.equals(version2));
        assertTrue(version2.equals(version1));
    }

    @Test
    public void toStringTest() {
        Tool3lgmVersion version = Tool3lgmVersion.parseString("1.2.3");
        assertEquals(version.toString(), "1.2.3");
        version = Tool3lgmVersion.parseString("IrrelevantPrefix 1.2.3");
        assertEquals(version.toString(), "1.2.3");
        version = Tool3lgmVersion.parseString("IrrelevantPrefix 1.2.3 (dev)");
        assertEquals(version.toString(), "1.2.3 (dev)");
        version = Tool3lgmVersion.parseString("12.345.6789_devvvvvvv");
        assertEquals(version.toString(), "12.345.6789_devvvvvvv");
    }

}
