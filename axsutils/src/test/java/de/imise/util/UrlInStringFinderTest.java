package de.imise.util;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.imise.util.UrlInStringFinder.UrlFinderResult;

public class UrlInStringFinderTest {

    @Test
    public void getResultTest() {
        UrlInStringFinder finder = new UrlInStringFinder();
        String s1 = "www.3lgm2.de";
        UrlFinderResult result1 = finder.getResult(s1);
        //Sys.err1(result1);
        Assert.assertNull(result1.file);
        Assert.assertEquals(result1.url, "www.3lgm2.de");
        Assert.assertEquals(result1.startIndexInOriginal, 0);
        Assert.assertEquals(result1.lengthInOriginal, s1.length());

        String s2 = " \"www.3lgm2.de\";";
        UrlFinderResult result2 = finder.getResult(s2);
        //Sys.err1(result2);
        Assert.assertNull(result2.file);
        Assert.assertEquals(result2.url, "www.3lgm2.de");
        Assert.assertEquals(result2.startIndexInOriginal, 2);
        Assert.assertEquals(result2.lengthInOriginal, result2.url.length());

    }

    @Test
    public void getResultsTest() {
        UrlInStringFinder finder = new UrlInStringFinder();
        String s1 = "Weitere Infos unter www.3lgm2.de oder unter https://www.wikipedia.de.";
        List<UrlFinderResult> results1 = finder.getResults(s1);
        //Sys.err1(results1);
        UrlFinderResult result1 = results1.get(0);
        Assert.assertNull(result1.file);
        Assert.assertEquals(result1.url, "www.3lgm2.de");
        Assert.assertEquals(result1.startIndexInOriginal, 0);
        Assert.assertEquals(result1.lengthInOriginal, "www.3lgm2.de".length());

        UrlFinderResult result2 = results1.get(1);
        Assert.assertNull(result2.file);
        Assert.assertEquals(result2.url, "https://www.wikipedia.de");
        Assert.assertEquals(result2.startIndexInOriginal, 0);
        Assert.assertEquals(result2.lengthInOriginal, "https://www.wikipedia.de".length());

    }

    @Test
    public void parseFileTest() {
    }

    @Test
    public void parseUrlTest() {
    }

    @Test
    public void trimTest() {
    }
}
