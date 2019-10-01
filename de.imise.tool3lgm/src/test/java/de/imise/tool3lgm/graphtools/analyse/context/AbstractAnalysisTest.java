package de.imise.tool3lgm.graphtools.analyse.context;

import java.util.List;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.EqualsAndHashCodeTest;
import de.imise.tool3lgm.graphtools.TestClassGenerator;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
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
        protected AbstractAnalysisAdapter(final MetaModelSpecific metaModelSpecific) {
            super(metaModelSpecific);
        }
        @Override
        public List<ElementContainer> getResult(final GraphDocument doc) {
            return null;
        }
    }

    private final AbstractAnalysis analysis1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1);

    /**
     * Same class and MetaModelSpecific
     */
    private final AbstractAnalysis analysis2Equals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_1);

    /**
     * Same class but other MetaModelSpecific
     */
    private final AbstractAnalysis analysis3NotEquals1 = new AbstractAnalysisAdapter(TestClassGenerator.META_MODEL_SPECIFIC_2);

    /**
     * Same MetaModelSpecific but other class
     */
    private final AbstractAnalysis analysis4NotEquals1 = new AbstractAnalysis(TestClassGenerator.META_MODEL_SPECIFIC_1) {
        @Override
        public List<ElementContainer> getResult(final GraphDocument doc) {
            return null;
        }
    };

    @Test
    public void equalsTest() {
        equalsTest(analysis1, analysis2Equals1);
        notEqualsTest(analysis1, analysis3NotEquals1);
        notEqualsTest(analysis1, analysis4NotEquals1);
        notEqualsTest(analysis2Equals1, analysis3NotEquals1);
        notEqualsTest(analysis2Equals1, analysis4NotEquals1);
        notEqualsTest(analysis3NotEquals1, analysis4NotEquals1);
    }

    @Test
    public void hashCodeTest() {
        equalsHashCodeTest(analysis1, analysis2Equals1);
        notEqualsHashCodeTest(analysis1, analysis3NotEquals1);
    }

}
