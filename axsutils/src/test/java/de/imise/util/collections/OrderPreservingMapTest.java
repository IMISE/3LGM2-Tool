package de.imise.util.collections;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderPreservingMapTest {

    @Test
    public void OrderPreservingMapGeneralTest() {
        OrderPreservingMap<String, String> map = new OrderPreservingMap<>();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.size() == 0);

        map.put("en", "english");
        Assert.assertTrue(!map.isEmpty());
        Assert.assertTrue(map.size() == 1);
        Assert.assertEquals(map.get("en"), "english");

        map.put("en", "English");
        Assert.assertTrue(!map.isEmpty());
        Assert.assertTrue(map.size() == 1);
        Assert.assertEquals(map.get("en"), "English");

        map.put("de", "German");
        Assert.assertTrue(!map.isEmpty());
        Assert.assertTrue(map.size() == 2);
        Assert.assertEquals(map.get("en"), "English");
        Assert.assertEquals(map.get("de"), "German");

        List<String> valuesList = map.valuesList();
        Assert.assertTrue(!valuesList.isEmpty());
        Assert.assertTrue(valuesList.size() == 2);
        Assert.assertEquals(valuesList.get(0), "English");
        Assert.assertEquals(valuesList.get(1), "German");

        map.put("es", "Spain");
        map.put("en", "American");
        valuesList = map.valuesList();
        Assert.assertTrue(valuesList.size() == 3);
        Assert.assertEquals(valuesList.get(0), "German");
        Assert.assertEquals(valuesList.get(1), "Spain");
        Assert.assertEquals(valuesList.get(2), "American");

        ListSet<String> keys = map.keyListSet();
        Assert.assertTrue(keys.size() == 3);
        Assert.assertEquals(keys.get(0), "de");
        Assert.assertEquals(keys.get(1), "es");
        Assert.assertEquals(keys.get(2), "en");

        map.clear();
        Assert.assertTrue(map.isEmpty());

    }

}
