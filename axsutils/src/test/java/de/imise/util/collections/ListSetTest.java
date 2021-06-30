package de.imise.util.collections;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ListSetTest {

    @Test
    public void addTestintE() {
        ListSet<String> listSet = new ListSet<>();
        assertTrue(listSet.isEmpty());
        assertTrue(listSet.size() == 0);
        assertTrue(listSet.indexOf("") == -1);

        //add with index
        listSet.add(0, "Hallo");
        assertTrue(!listSet.isEmpty());
        assertTrue(listSet.size() == 1);
        assertTrue(listSet.indexOf("") == -1);
        assertTrue(listSet.indexOf("Hallo") == 0);
        listSet.add(1, "Leute");
        assertTrue(listSet.size() == 2);
        assertTrue(listSet.indexOf("") == -1);
        assertTrue(listSet.indexOf("Hallo") == 0);
        assertTrue(listSet.indexOf("Leute") == 1);
        listSet.add(2, "was");
        listSet.add(3, "geht");
        assertTrue(listSet.size() == 4);
        assertTrue(listSet.indexOf("Hallo") == 0);
        assertTrue(listSet.indexOf("Leute") == 1);
        assertTrue(listSet.indexOf("was") == 2);
        assertTrue(listSet.indexOf("geht") == 3);
        listSet.add(1, "ihr");
        assertTrue(listSet.size() == 5);
        assertTrue(listSet.indexOf("Hallo") == 0);
        assertTrue(listSet.indexOf("ihr") == 1);
        assertTrue(listSet.indexOf("Leute") == 2);
        assertTrue(listSet.indexOf("was") == 3);
        assertTrue(listSet.indexOf("geht") == 4);
        listSet.add(1, "geht");
        assertTrue(listSet.size() == 5);
        assertTrue(listSet.indexOf("Hallo") == 0);
        assertTrue(listSet.indexOf("geht") == 1);
        assertTrue(listSet.indexOf("ihr") == 2);
        assertTrue(listSet.indexOf("Leute") == 3);
        assertTrue(listSet.indexOf("was") == 4);

        //add without index
        listSet.add("ab");
        assertTrue(listSet.size() == 6);
        assertTrue(listSet.indexOf("Hallo") == 0);
        assertTrue(listSet.indexOf("geht") == 1);
        assertTrue(listSet.indexOf("ihr") == 2);
        assertTrue(listSet.indexOf("Leute") == 3);
        assertTrue(listSet.indexOf("was") == 4);
        assertTrue(listSet.indexOf("ab") == 5);

        //set with index
        listSet.set(0, "Tach");
        assertTrue(listSet.size() == 6);
        assertTrue(listSet.indexOf("Tach") == 0);
        assertTrue(listSet.indexOf("geht") == 1);
        assertTrue(listSet.indexOf("ihr") == 2);
        assertTrue(listSet.indexOf("Leute") == 3);
        assertTrue(listSet.indexOf("was") == 4);
        assertTrue(listSet.indexOf("ab") == 5);

        listSet.set(1, "geht");
        assertTrue(listSet.size() == 6);
        assertTrue(listSet.indexOf("Tach") == 0);
        assertTrue(listSet.indexOf("geht") == 1);
        assertTrue(listSet.indexOf("ihr") == 2);
        assertTrue(listSet.indexOf("Leute") == 3);
        assertTrue(listSet.indexOf("was") == 4);
        assertTrue(listSet.indexOf("ab") == 5);

        listSet.set(4, "geht"); //removes "geht" and resets the old 4 (noew 3) by "geht"
        assertTrue(listSet.size() == 5);
        assertTrue(listSet.indexOf("Tach") == 0);
        assertTrue(listSet.indexOf("ihr") == 1);
        assertTrue(listSet.indexOf("Leute") == 2);
        assertTrue(listSet.indexOf("geht") == 3);
        assertTrue(listSet.indexOf("ab") == 4);
    }

}
