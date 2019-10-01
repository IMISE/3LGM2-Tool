package de.imise.tool3lgm.graphtools;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (01.10.2019)
 */
public class TestClassGenerator {

    public static final class TestMetaModelDefiniton1 extends MetaModelDefinition {

        @Override
        public Class<? extends ModelElement>[] getAllDomainLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends Edge>[] getAllEdges() {
            return null;
        }

        @Override
        public Set<Class<? extends ModelElement>> getImportableNodes() {
            return null;
        }

        @Override
        public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
            return null;
        }

    }

    public static final class TestMetaModelDefiniton2 extends MetaModelDefinition {

        @Override
        public Class<? extends ModelElement>[] getAllDomainLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
            return null;
        }

        @Override
        public Class<? extends Edge>[] getAllEdges() {
            return null;
        }

        @Override
        public Set<Class<? extends ModelElement>> getImportableNodes() {
            return null;
        }

        @Override
        public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
            return null;
        }

    }

    public static final MetaModelSpecific META_MODEL_SPECIFIC_1 = new MetaModelSpecific() {

        @Override
        public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
            return TestMetaModelDefiniton1.class;
        }
    };

    public static final MetaModelSpecific META_MODEL_SPECIFIC_2 = new MetaModelSpecific() {

        @Override
        public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
            return TestMetaModelDefiniton2.class;
        }
    };

    /**
     * Adapter, der alle abstrakten Funktionen mit leeren Arrays und Sets überschreibt.
     *
     * @author AXS (6 Jun 2019)
     */
    @SuppressWarnings("unchecked")
    public static class DefaultMetaModelDefinitionAdapter1 extends MetaModelDefinition {

        @Override
        public Class<? extends ModelElement>[] getAllDomainLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends Edge>[] getAllEdges() {
            return new Class[0];
        }

        @Override
        public Set<Class<? extends ModelElement>> getImportableNodes() {
            return ImmutableSet.of();
        }

        @Override
        public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
            return ImmutableSet.of();
        }

    }

    /**
     * Adapter, der alle abstrakten Funktionen mit leeren Arrays und Sets überschreibt.
     *
     * @author AXS (6 Jun 2019)
     */
    @SuppressWarnings("unchecked")
    public static class DefaultMetaModelDefinitionAdapter2 extends MetaModelDefinition {

        @Override
        public Class<? extends ModelElement>[] getAllDomainLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterDomainLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllLogicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllInterLogicalPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends ModelElement>[] getAllPhysicalLayerNodes() {
            return new Class[0];
        }

        @Override
        public Class<? extends Edge>[] getAllEdges() {
            return new Class[0];
        }

        @Override
        public Set<Class<? extends ModelElement>> getImportableNodes() {
            return ImmutableSet.of();
        }

        @Override
        public Set<Class<? extends ModelElement>> getGenerateNameClasses() {
            return ImmutableSet.of();
        }

    }

    //    public static final MetaModelDefinition META_MODEL_DEFINITION_1 = new DefaultMetaModelDefinitionAdapter1();
    //
    //    public static final MetaModelDefinition META_MODEL_DEFINITION_2_EQUALS_CLASS_1 = new DefaultMetaModelDefinitionAdapter1();
    //
    //    public static final MetaModelDefinition META_MODEL_DEFINITION_3_NOT_EQUALS_CLASS_1 = new DefaultMetaModelDefinitionAdapter2();
    //
    public static final MetaModelContext META_MODEL_CONTEXT_1 = new MetaModelContext(DefaultMetaModelDefinitionAdapter1.class);

    public static final MetaModelContext META_MODEL_CONTEXT_2_EQUALS_1 = new MetaModelContext(DefaultMetaModelDefinitionAdapter1.class);

    public static final MetaModelContext META_MODEL_CONTEXT_3_NOT_EQUALS_1 = new MetaModelContext(DefaultMetaModelDefinitionAdapter2.class);

    public static MetaModel METAMODEL_1;
    public static MetaModel METAMODEL_2_EQUALS_1;
    public static MetaModel METAMODEL_3_NOT_EQUALS_1;

    static {
        try {
            METAMODEL_1 = new MetaModel(META_MODEL_CONTEXT_1);
            METAMODEL_2_EQUALS_1 = new MetaModel(META_MODEL_CONTEXT_2_EQUALS_1);
            METAMODEL_3_NOT_EQUALS_1 = new MetaModel(META_MODEL_CONTEXT_3_NOT_EQUALS_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
