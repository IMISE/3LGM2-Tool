package de.imise.tool3lgm.graphtools;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @author AXS (01.10.2019)
 */
public class EqualsAndHashCodeTest {

    /**
     * @param object1
     * @param object2Equals1
     */
    public void equalsTest(final Object object1, final Object object2Equals1) {
        assertEquals(object1, object1);
        assertEquals(object2Equals1, object2Equals1);
        assertEquals(object1, object2Equals1);
        assertEquals(object2Equals1, object1);
    }

    /**
     * @param object1
     * @param object2NotEquals1
     */
    public void notEqualsTest(final Object object1, final Object object2NotEquals1) {
        assertEquals(object1, object1);
        assertEquals(object2NotEquals1, object2NotEquals1);
        assertNotEquals(object1, object2NotEquals1);
    }

    /**
     * @param object1
     * @param object2
     */
    public void equalsHashCodeTest(final Object object1, final Object object2Equals1) {
        assertEquals(object1.hashCode(), object1.hashCode());
        assertEquals(object2Equals1.hashCode(), object2Equals1.hashCode());
        assertEquals(object1.hashCode(), object2Equals1.hashCode());
    }

    /**
     * @param object1
     * @param object2
     */
    public void notEqualsHashCodeTest(final Object object1, final Object object2NotEquals1) {
        assertEquals(object1.hashCode(), object1.hashCode());
        assertEquals(object2NotEquals1.hashCode(), object2NotEquals1.hashCode());
        assertNotEquals(object1.hashCode(), object2NotEquals1.hashCode());
    }

}
