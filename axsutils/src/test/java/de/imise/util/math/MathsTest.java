package de.imise.util.math;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MathsTest {

    @Test
    public void maxTest() {
        int max = Maths.max(1, 2, 3, 2);
        assertEquals(max, 3);
    }

    @Test
    public void minTest() {
        int min = Maths.min(1, 2, 3, 2);
        assertEquals(min, 1);
    }

    @Test
    public void getValueInMinMaxTest() {
        int valueInMinMax;
        valueInMinMax = Maths.getValueInMinMax(-10, 0, 100);
        assertEquals(valueInMinMax, 0);
        valueInMinMax = Maths.getValueInMinMax(10, 0, 100);
        assertEquals(valueInMinMax, 10);
        valueInMinMax = Maths.getValueInMinMax(110, 0, 100);
        assertEquals(valueInMinMax, 100);
    }

}
