package de.imise.tool3lgm.graphtools.metamodel;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.EqualsAndHashCodeTest;
import de.imise.tool3lgm.graphtools.TestClassGenerator;

public class MetaModelSpecificAdapterTest extends EqualsAndHashCodeTest {

    private final MetaModelSpecificAdapter adapter1 = new MetaModelSpecificAdapter(TestClassGenerator.TestMetaModelDefiniton1.class);
    private final MetaModelSpecificAdapter adapter2Equals1 = new MetaModelSpecificAdapter(TestClassGenerator.TestMetaModelDefiniton1.class);
    private final MetaModelSpecificAdapter adapter3NotEquals1 = new MetaModelSpecificAdapter(TestClassGenerator.TestMetaModelDefiniton2.class);

    @Test
    public void equalsTest() {
        //EqualsVerifier.forClass(MetaModelSpecificAdapter.class).verify(); //geht nicht ohne Fehler
        equalsTest(adapter1, adapter2Equals1);
        notEqualsTest(adapter1, adapter3NotEquals1);
    }

    @Test
    public void hashCodeTest() {
        equalsHashCodeTest(adapter1, adapter2Equals1);
        notEqualsHashCodeTest(adapter1, adapter3NotEquals1);
    }

}
