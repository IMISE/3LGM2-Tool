package de.imise.tool3lgm.graphtools.model;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.EqualsAndHashCodeTest;
import de.imise.tool3lgm.graphtools.TestClassGenerator;

/**
 * @author AXS (01.10.2019)
 */
public class ElementSelectionContextTest extends EqualsAndHashCodeTest {

    private final ElementSelectionContext elementSelectionContext1 = new ElementSelectionContext(TestClassGenerator.METAMODEL_1);
    private final ElementSelectionContext elementSelectionContext2Equals = new ElementSelectionContext(TestClassGenerator.METAMODEL_2_EQUALS_1);
    private final ElementSelectionContext elementSelectionContext3NotEquals1 = new ElementSelectionContext(TestClassGenerator.METAMODEL_3_NOT_EQUALS_1);

    @Test
    public void equalsTest() {
        equalsTest(elementSelectionContext1, elementSelectionContext2Equals);
        notEqualsTest(elementSelectionContext1, elementSelectionContext3NotEquals1);
    }

    @Test
    public void hashCodeTest() {
        equalsHashCodeTest(elementSelectionContext1, elementSelectionContext2Equals);
        notEqualsHashCodeTest(elementSelectionContext1, elementSelectionContext3NotEquals1);
    }
}
