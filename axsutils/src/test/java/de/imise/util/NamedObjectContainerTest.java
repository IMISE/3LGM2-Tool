package de.imise.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

public class NamedObjectContainerTest {

    @Test
    public void NamedObjectContainerEString() {
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(new Object(), "Foo");
        NamedObjectContainer<Object> noc2 = new NamedObjectContainer<>(new Object(), "Foo");
        NamedObjectContainer<Object> noc3 = new NamedObjectContainer<>(new Object(), "Bar");
        testConstructorWithEqualsIfToStringEqualsTrue(noc1, noc2, noc3, false);
        assertNotEquals(noc1, noc2);
    }

    @Test
    public void NamedObjectContainerEStringbooleanTrue() {
        boolean shouldBeEqualsIfToStringEquals = false;
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(new Object(), "Foo", shouldBeEqualsIfToStringEquals);
        NamedObjectContainer<Object> noc2 = new NamedObjectContainer<>(new Object(), "Foo", shouldBeEqualsIfToStringEquals);
        NamedObjectContainer<Object> noc3 = new NamedObjectContainer<>(new Object(), "Bar", shouldBeEqualsIfToStringEquals);
        testConstructorWithEqualsIfToStringEqualsTrue(noc1, noc2, noc3, false);
        assertNotEquals(noc1, noc2);
    }

    @Test
    public void NamedObjectContainerEStringbooleanFalse() {
        boolean shouldBeEqualsIfToStringEquals = true;
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(new Object(), "Foo", shouldBeEqualsIfToStringEquals);
        NamedObjectContainer<Object> noc2 = new NamedObjectContainer<>(new Object(), "Foo", shouldBeEqualsIfToStringEquals);
        NamedObjectContainer<Object> noc3 = new NamedObjectContainer<>(new Object(), "Bar", shouldBeEqualsIfToStringEquals);
        testConstructorWithEqualsIfToStringEqualsTrue(noc1, noc2, noc3, true);
        assertEquals(noc1, noc2);
    }

    private <E, F, G> void testConstructorWithEqualsIfToStringEqualsTrue(final NamedObjectContainer<E> noc1, final NamedObjectContainer<F> noc2, final NamedObjectContainer<G> noc3, final boolean equalsIfToStringIsEquals) {
        assertNotNull(noc1);
        assertNotNull(noc2);
        assertNotNull(noc3);
        String toString1 = noc1.toString();
        String toString2 = noc2.toString();
        String toString3 = noc3.toString();
        assertNotNull(toString1);
        assertEquals(toString1, toString2);
        assertNotEquals(toString1, toString3);
        assertEquals(noc1, noc1);
        assertNotEquals(noc1, noc3);
        assertEquals(noc1.equalsIfToStringIsEquals, equalsIfToStringIsEquals);
        assertEquals(noc2.equalsIfToStringIsEquals, equalsIfToStringIsEquals);
        assertEquals(noc3.equalsIfToStringIsEquals, equalsIfToStringIsEquals);
    }

    @Test
    public void equalsConstructorEString() {
        //beide Objekte werden mit equalsIfToStringEquals = false initialisisert, sind also nur gleich, wenn das Object und der toString gleich sind
        equalsConstructorEStringBoolean(false);
        //noc1 wird immer mit equalsIfToStringEquals = true und noc2 mit false initialisiert. Sie sind nur gleich, wenn das Object und der toString gleich sind
        equalsConstructorEStringBoolean(true);
    }

    private void equalsConstructorEStringBoolean(final boolean equalsIfToStringIsEqualsNoc1) {
        Object object = new Object();
        String toString = "Foo";
        NamedObjectContainer<Object> noc1;

        //noc1 wird immer mit dem Parameter equalsIfToStringEquals und noc2 immer mit false initialisiert. Sie sind nur gleich, wenn beide Objecte (Object und toString) gleich sind
        noc1 = new NamedObjectContainer<>(object, toString, equalsIfToStringIsEqualsNoc1);
        equals(noc1, object, toString, true);
        equals(noc1, object, "Foo", true);
        equals(noc1, new Object(), toString, false);
        equals(noc1, new Object(), "Foo", false);
        equals(noc1, null, toString, false);
        equals(noc1, null, "Foo", false);
        equals(noc1, object, "Bar", false);
        equals(noc1, new Object(), "Bar", false);
        equals(noc1, null, "Bar", false);
        equals(noc1, object, null, false);
        equals(noc1, new Object(), null, false);
        equals(noc1, null, null, false);

        noc1 = new NamedObjectContainer<>(null, toString, equalsIfToStringIsEqualsNoc1);
        equals(noc1, null, toString, true);
        equals(noc1, null, "Foo", true);
        equals(noc1, null, "Bar", false);
        equals(noc1, new Object(), "Bar", false);
        equals(noc1, null, null, false);
        equals(noc1, new Object(), null, false);

        noc1 = new NamedObjectContainer<>(null, null, equalsIfToStringIsEqualsNoc1);
        equals(noc1, null, toString, false);
        equals(noc1, null, "Foo", false);
        equals(noc1, null, "Bar", false);
        equals(noc1, new Object(), "Bar", false);
        equals(noc1, null, null, true);
        equals(noc1, new Object(), null, false);

        assertEquals(noc1.equals(null), false);
        assertEquals(noc1.equals(new Object()), false);
    }

    @Test
    public void equalsConstructorEStringBooleanTrue() {
        //beide Objekte werden mit equalsIfToStringEquals = true initialisisert, sind also gleich, wenn der toString gleich ist (das Object ist egal)
        Object object = new Object();
        String toString = "Foo";
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(object, toString, true);
        equals(noc1, object, toString, true, true);
        equals(noc1, object, "Foo", true, true);
        equals(noc1, new Object(), toString, true, true);
        equals(noc1, new Object(), "Foo", true, true);
        equals(noc1, null, toString, true, true);
        equals(noc1, null, "Foo", true, true);
        equals(noc1, object, "Bar", true, false);
        equals(noc1, new Object(), "Bar", true, false);
        equals(noc1, null, "Bar", true, false);
        equals(noc1, object, null, true, false);
        equals(noc1, new Object(), null, true, false);
        equals(noc1, null, null, true, false);

        noc1 = new NamedObjectContainer<>(null, toString, true);
        equals(noc1, null, toString, true, true);
        equals(noc1, null, "Foo", true, true);
        equals(noc1, null, "Bar", true, false);
        equals(noc1, new Object(), "Bar", true, false);
        equals(noc1, null, null, true, false);
        equals(noc1, new Object(), null, true, false);

        noc1 = new NamedObjectContainer<>(null, null, true);
        equals(noc1, null, toString, true, false);
        equals(noc1, null, "Foo", true, false);
        equals(noc1, null, "Bar", true, false);
        equals(noc1, new Object(), "Bar", true, false);
        equals(noc1, null, null, true, true);
        equals(noc1, new Object(), null, true, true);
    }

    private final <E, F> void equals(final NamedObjectContainer<E> noc1, final F object2, final String string2, final boolean assertEquals) {
        equals(noc1, object2, string2, null, assertEquals);
    }

    private final <E, F> void equals(final NamedObjectContainer<E> noc1, final F object2, final String string2, final Boolean equalsIfToStringEquals2, final boolean assertEquals) {
        assertEquals(noc1, noc1);
        NamedObjectContainer<F> noc2 = equalsIfToStringEquals2 != null ? new NamedObjectContainer<>(object2, string2, equalsIfToStringEquals2) : new NamedObjectContainer<>(object2, string2);
        assertEquals(noc2, noc2);
        if (assertEquals) {
            assertEquals(noc1, noc2);
            assertEquals(noc2, noc1);
        } else {
            assertNotEquals(noc1, noc2);
            assertNotEquals(noc2, noc1);
        }
    }

    @Test
    public void toStringTest() {
        toStringTest(new Object(), "Foo");
        toStringTest(null, "Foo");
        toStringTest(null, null);
    }

    private void toStringTest(final Object object, final String toString) {
        NamedObjectContainer<Object> noc = new NamedObjectContainer<>(object, toString);
        String s = toString == null ? null : "" + toString; //neues String Object mit selbem Inhalt anlegen
        assertEquals(noc.toString, s);
        s = String.valueOf(s); //ist toString null, dann gibt toString() "null" als String zurück, ansonsten den String selbst
        assertEquals(noc.toString(), s);
        noc = new NamedObjectContainer<>(object, null); //toString mit null testen
        assertNull(noc.toString); //Variable selbst ist null
        assertEquals(noc.toString(), "null"); //toString() gibt aber "null" als String zurück
    }

    @Test
    public void getObject() {
        Object object = new Object();
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(object, "Foo");
        NamedObjectContainer<Object> noc2 = new NamedObjectContainer<>(object, "Foo");
        NamedObjectContainer<Object> noc3 = new NamedObjectContainer<>(new Object(), "Bar");
        getObject(noc1, noc2, noc3);
        noc1 = new NamedObjectContainer<>(null, "Foo");
        noc2 = new NamedObjectContainer<>(null, "Foo");
        noc3 = new NamedObjectContainer<>(new Object(), "Bar");
        getObject(noc1, noc2, noc3);
    }

    private <E, F, G> void getObject(final NamedObjectContainer<E> noc1, final NamedObjectContainer<F> noc2, final NamedObjectContainer<G> noc3) {
        Object object1 = noc1.getObject();
        Object object2 = noc2.getObject();
        Object object3 = noc3.getObject();
        assertSame(object1, object2);
        assertNotEquals(object1, object3);
    }

    @Test
    public void of() {
        Object object = new Object();
        String toString = "Foo";
        NamedObjectContainer<Object> ofNoc = NamedObjectContainer.of(object, toString);
        NamedObjectContainer<Object> noc = new NamedObjectContainer<>(object, toString);
        assertEquals(ofNoc, noc);
        assertEquals(noc, ofNoc);
        assertNotSame(ofNoc, noc);
        noc = new NamedObjectContainer<>(new Object(), toString);
        assertNotEquals(ofNoc, noc);
        assertNotEquals(noc, ofNoc);
        noc = new NamedObjectContainer<>(null, toString);
        assertNotEquals(ofNoc, noc);
        assertNotEquals(noc, ofNoc);
    }

    @Test
    public void hashCodeTest() {
        Object object = new Object();
        String toString = "Foo";
        NamedObjectContainer<Object> noc1 = new NamedObjectContainer<>(object, toString);
        NamedObjectContainer<Object> noc2 = new NamedObjectContainer<>(object, toString);
        int hashCode1 = noc1.hashCode();
        int hashCode2 = noc2.hashCode();
        assertEquals(hashCode1, hashCode2);
        noc2 = NamedObjectContainer.of(new Object(), toString);
        hashCode2 = noc2.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        noc2 = NamedObjectContainer.of(object, "Bar");
        hashCode2 = noc2.hashCode();
        assertNotEquals(hashCode1, hashCode2);

        noc1 = new NamedObjectContainer<>(null, null);
        hashCode1 = noc1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        noc2 = new NamedObjectContainer<>(null, null);
        hashCode2 = noc2.hashCode();
        assertEquals(noc1, noc2);
        assertEquals(hashCode1, hashCode2);

        noc1 = new NamedObjectContainer<>(new Object(), null);
        hashCode1 = noc1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        noc2 = new NamedObjectContainer<>(noc1.getObject(), null);
        hashCode2 = noc2.hashCode();
        assertEquals(noc1, noc2);
        assertEquals(hashCode1, hashCode2);
        noc2 = new NamedObjectContainer<>(new Object(), null);
        hashCode2 = noc2.hashCode();
        assertNotEquals(noc1, noc2);
        assertNotEquals(hashCode1, hashCode2);

        noc1 = new NamedObjectContainer<>(null, toString);
        hashCode1 = noc1.hashCode();
        assertNotEquals(hashCode1, hashCode2);
        noc2 = new NamedObjectContainer<>(null, toString);
        hashCode2 = noc2.hashCode();
        assertEquals(noc1, noc2);
        assertEquals(hashCode1, hashCode2);
        noc2 = new NamedObjectContainer<>(null, "Bar");
        hashCode2 = noc2.hashCode();
        assertNotEquals(noc1, noc2);
        assertNotEquals(hashCode1, hashCode2);

    }

}
