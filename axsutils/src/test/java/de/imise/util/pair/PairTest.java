package de.imise.util.pair;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;
import org.testng.util.Strings;

public class PairTest {

    @Test
    public void pairAndGetFirstItemAndGetSeconItem() {
        Object firstObject = new Object();
        Object secondObject = new Object();
        Pair<Object, Object> pair = new Pair<>(firstObject, secondObject);
        assertEquals(pair.getFirstItem(), firstObject);
        assertEquals(pair.getSecondItem(), secondObject);
        pair.setFirstItem(secondObject);
        pair.setSecondItem(firstObject);
        assertEquals(pair.getFirstItem(), secondObject);
        assertEquals(pair.getSecondItem(), firstObject);
    }

    @Test
    public void equals() {
        Object object = new Object();
        equals(object, new Object());
        equals(object, null);
        equals(null, object);
    }

    @SuppressWarnings("unlikely-arg-type")
    public <T> void equals(final T firstObject, final T secondObject) {
        Pair<Object, Object> pair1 = new Pair<>(firstObject, secondObject);
        Pair<Object, Object> pair2 = new Pair<>(firstObject, secondObject);
        assertFalse(pair1.equals(null));
        assertFalse(pair2.equals(null));
        assertFalse(pair1.equals(firstObject));
        assertFalse(pair2.equals(""));
        assertTrue(pair1.equals(pair1));
        assertTrue(pair2.equals(pair2));
        assertTrue(pair1.equals(pair2));
        assertTrue(pair2.equals(pair1));

        pair2 = new Pair<>(secondObject, firstObject);
        assertFalse(pair1.equals(pair2));
        assertFalse(pair2.equals(pair1));

        pair2 = new Pair<>(firstObject, firstObject);
        assertFalse(pair1.equals(pair2));
        assertFalse(pair2.equals(pair1));
    }

    @Test
    public void hashCodeTest() {
        Object object = new Object();
        String toString = "Foo";
        Pair<Object, String> pair1 = new Pair<>(object, toString);
        Pair<Object, String> pair2 = new Pair<>(object, toString);
        int hashCode1 = pair1.hashCode();
        int hashCode2 = pair2.hashCode();
        assertEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(new Object(), toString);
        hashCode2 = pair2.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(object, "Bar");
        hashCode2 = pair2.hashCode();
        assertNotEquals(hashCode1, hashCode2);

        pair1 = new Pair<>(null, null);
        hashCode1 = pair1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(null, null);
        hashCode2 = pair2.hashCode();
        assertEquals(pair1, pair2);
        assertEquals(hashCode1, hashCode2);

        pair1 = new Pair<>(new Object(), null);
        hashCode1 = pair1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(pair1.getFirstItem(), null);
        hashCode2 = pair2.hashCode();
        assertEquals(pair1, pair2);
        assertEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(new Object(), null);
        hashCode2 = pair2.hashCode();
        //assertNotEquals(pair1, pair2); aus irgendeinem Grund geht das hier nicht. Es kommt sowohl bei Not als auch ohne Not ein AssertionFailed!? Bug in assertEquals()?
        assertThat(pair1, not(equalTo(pair2))); //deswegen dasselbe über Hamcrest -> es geht
        assertNotEquals(hashCode1, hashCode2);

        pair1 = new Pair<>(null, toString);
        hashCode1 = pair1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(null, toString);
        hashCode2 = pair2.hashCode();
        assertEquals(pair1, pair2);
        assertEquals(hashCode1, hashCode2);
        pair2 = new Pair<>(null, "Bar");
        hashCode2 = pair2.hashCode();
        //assertNotEquals(pair1, pair2); aus irgendeinem Grund geht das hier nicht. Es kommt sowohl bei Not als auch ohne Not ein AssertionFailed!? Bug in assertEquals()?
        assertThat(pair1, not(equalTo(pair2))); //deswegen dasselbe über Hamcrest -> es geht
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    public void toStringTest() {
        Object firstObject = new Object();
        Object secondObject = new Object();
        Pair<Object, Object> pair = new Pair<>(firstObject, secondObject);
        assertTrue(Strings.isNotNullAndNotEmpty(pair.toString())); //da der Text nicht wirklich gebraucht wird, ist das hier nur für die Coverage
    }
}
