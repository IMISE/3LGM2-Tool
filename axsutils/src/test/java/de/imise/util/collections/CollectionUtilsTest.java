package de.imise.util.collections;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

public class CollectionUtilsTest {

    //  @Test
    public void _getNoMultiplesListTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void _removeMultiplesTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void addNonMultiplesTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void arrayContainsTestObjectObject() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void arrayContainsTestintint() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void containsInstancesOfTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void containsNameTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void containsOnlyInstancesOfTestCollectionClassboolean() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void containsOnlyInstancesOfTestCollectionbooleanClass() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestCollectionT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestListT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestSetT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestMapKV() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestListMultimapKV() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestSetMultimapKV() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void ensureImmutableTestMultimapKV() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getAllElementsOfTestCollectionTClassT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getAllElementsOfTestCollectionClassTboolean() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getAllInstancesOfTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getBackwardIterableTest() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void getCommonIterableTestIterableextendsT() {
        List<String> strings = new ArrayList<>();
        strings.add("eins");
        strings.add("zwei");
        strings.add("drei");
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        List<Object> empty = new ArrayList<>();
        Iterable<Object> commonIterable = CollectionUtils.getCommonIterable(strings, empty, ints);
        int i = 0;
        for (Object o : commonIterable) {
            if (i == 0) {
                assertEquals(o, strings.get(0));
            } else if (i == 1) {
                assertEquals(o, strings.get(1));
            } else if (i == 2) {
                assertEquals(o, strings.get(2));
            } else if (i == 3) {
                assertEquals(o, ints.get(0));
            } else if (i == 4) {
                assertEquals(o, ints.get(1));
            } else if (i == 5) {
                assertEquals(o, ints.get(2));
            }
            i++;
        }
        assertEquals(i, 6);

    }

    @Test
    public void getCommonIterableTestListIterableT() {
        List<String> strings = new ArrayList<>();
        strings.add("eins");
        strings.add("zwei");
        strings.add("drei");
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        ints.add(3);
        List<Object> empty = new ArrayList<>();
        List<?>[] lists = {
                strings, empty, ints
        };
        Iterable<Object> commonIterable = CollectionUtils.getCommonIterable(lists);
        int i = 0;
        for (Object o : commonIterable) {
            if (i == 0) {
                assertEquals(o, strings.get(0));
            } else if (i == 1) {
                assertEquals(o, strings.get(1));
            } else if (i == 2) {
                assertEquals(o, strings.get(2));
            } else if (i == 3) {
                assertEquals(o, ints.get(0));
            } else if (i == 4) {
                assertEquals(o, ints.get(1));
            } else if (i == 5) {
                assertEquals(o, ints.get(2));
            }
            i++;
        }
        assertEquals(i, 6);
    }

    //  @Test
    public void getNextIndicatedNameTestStringCollection() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getNextIndicatedNameTestStringIterablebooleanboolean() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getNextIndicatedNameTestStringStringIterable() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getNextIndicatedNameTestStringStringintIterable() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void getSimpleClassNamesTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void iterableTestIteratorT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void iterableTestListT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void joinArraysTestTT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void joinArraysTestT() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void joinClassArraysTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void mainTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void maxSizeTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toJoinedStringTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toListStringTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toMatrixArrayTest() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toStringTestlong() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toStringTestdouble() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toStringTestint() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toStringTestObjectStringStringString() {
        throw new RuntimeException("Test not implemented");
    }

    //  @Test
    public void toStringArrayTestObject() {
        throw new RuntimeException("Test not implemented");
    }
}
