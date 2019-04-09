/*
 * Created on 23.04.2004
 */
package de.imise.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import de.imise.util.pair.Pair;

public class PairTest {
	
	@Test
	public void getFirstItemTest() {
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
