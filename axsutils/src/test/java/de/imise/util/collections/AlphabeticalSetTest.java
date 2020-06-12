package de.imise.util.collections;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.TreeSet;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import de.imise.util.Alphabetical;
import de.imise.util.TestData;

@Test
public class AlphabeticalSetTest {

    @Test
    public void SimpleTest() {
        AlphabeticalSet<String> alphaSet = new AlphabeticalSet<>(TestData.namesCol);
        TreeSet<String> treeSet = new TreeSet<>(Alphabetical.getLocalizedComparator());
        treeSet.addAll(TestData.namesCol);
        assertThat(alphaSet, Matchers.contains(treeSet.toArray()));
    }
}
