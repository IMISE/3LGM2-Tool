package de.imise.tool3lgm.graphtools.analyse.context;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.EqualsAndHashCodeTest;
import de.imise.tool3lgm.graphtools.TestClassGenerator;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class AbstractAnalysisTest extends EqualsAndHashCodeTest {

    /**
     * @author AXS (01.10.2019)
     */
    private static final class AbstractAnalysisAdapter extends AbstractAnalysis {

        /**
         * @param metaModelSpecific
         */
        protected AbstractAnalysisAdapter(final MetaModelSpecific metaModelSpecific, final String id, final String name, final List<Class<? extends ModelElement>> startClasses) {
            super(metaModelSpecific, id);
            this.name = name;
        }
        @Override
        public List<ElementContainer> getResult(final GraphDocument doc) {
            return null;
        }
    }

    /**
     * @author AXS (01.10.2019)
     */
    private static final class AbstractAnalysisAdapter2 extends AbstractAnalysis {
        /**
         * @param metaModelSpecific
         */
        protected AbstractAnalysisAdapter2(final MetaModelSpecific metaModelSpecific, final String id, final String name, final List<Class<? extends ModelElement>> startClasses) {
            super(metaModelSpecific, id);
            this.name = name;
            this.startClasses = startClasses;
        }
        @Override
        public List<ElementContainer> getResult(final GraphDocument doc) {
            return null;
        }
    }

    private final AbstractAnalysis analysis1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1, "id1", "name1", ImmutableList.of(ModelElement.class));

    /**
     * Same class and MetaModelSpecific and equals id, name and startClasses
     */
    private final AbstractAnalysis analysis2Equals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1, "id1", "name1", ImmutableList.of(ModelElement.class));

    /**
     * Same class and equals id, name and startClasses but other
     * MetaModelSpecific
     */
    private final AbstractAnalysis analysis3NotEquals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_2, "id1", "name1", ImmutableList.of(ModelElement.class));

    /**
     * Same class and MetaModelSpecific and equals name and startClasses but
     * other id
     */
    private final AbstractAnalysis analysis4NotEquals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1, "id2", "name1", ImmutableList.of(ModelElement.class));

    /**
     * Same class and MetaModelSpecific and equals id and startClasses but other
     * name
     */
    private final AbstractAnalysis analysis5NotEquals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1, "id1", "name2", ImmutableList.of(ModelElement.class));

    /**
     * Same class and MetaModelSpecific and equals id and name but other
     * startClasses
     */
    private final AbstractAnalysis analysis6NotEquals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1, "id1", "name2", ImmutableList.of(ModelElement.class, Node.class));

    /**
     * Same MetaModelSpecific and equals id, name and startClasses but other
     * class
     */
    private final AbstractAnalysis analysisXNotEquals1 = new AbstractAnalysisAdapter2(TestClassGenerator.META_MODEL_SPECIFIC_1, "id1", "name1", ImmutableList.of(ModelElement.class));

    @Test
    public void equalsTest() {
        equalsTest(analysis1, analysis1);
        equalsTest(analysis2Equals1, analysis2Equals1);
        equalsTest(analysis3NotEquals1, analysis3NotEquals1);
        equalsTest(analysis4NotEquals1, analysis4NotEquals1);
        equalsTest(analysis5NotEquals1, analysis5NotEquals1);
        equalsTest(analysis6NotEquals1, analysis6NotEquals1);

        equalsTest(analysis1, analysis2Equals1);

        notEqualsTest(analysis1, analysis3NotEquals1);
        notEqualsTest(analysis1, analysis4NotEquals1);
        notEqualsTest(analysis1, analysis5NotEquals1);
        notEqualsTest(analysis1, analysis6NotEquals1);
        notEqualsTest(analysis1, analysisXNotEquals1);
    }

    @Test
    public void hashCodeTest() {
        equalsHashCodeTest(analysis1, analysis2Equals1);
        notEqualsHashCodeTest(analysis1, analysis3NotEquals1);
    }

    @Test
    public void hasEqualsContentTest() {
        assertTrue(analysis1.hasEqualsContent(analysis1, true));
        assertTrue(analysis1.hasEqualsContent(analysis1, false));
        assertTrue(analysis1.hasEqualsContent(analysis5NotEquals1, false));
        assertFalse(analysis1.hasEqualsContent(analysis5NotEquals1, true));
    }

}
