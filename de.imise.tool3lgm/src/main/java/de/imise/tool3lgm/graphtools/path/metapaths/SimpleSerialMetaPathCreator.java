package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.ArrayList;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public class SimpleSerialMetaPathCreator extends SimpleMetaPathCreator {

    /**
     * @param metaModelSpecific MetaModel source, in dem die Pfade angelegt
     *            werden
     */
    public SimpleSerialMetaPathCreator(final MetaModelSpecific metaModelSpecific) {
        super(metaModelSpecific);
    }

    /**
     * @return
     */
    public SimpleSerialMetaPathBuilder builder() {
        return new SimpleSerialMetaPathBuilder(this);
    }

    /**
     * @return
     */
    public static SimpleSerialMetaPathBuilder builder(final MetaModelSpecific metaModelSpecific) {
        return new SimpleSerialMetaPathCreator(metaModelSpecific).builder();
    }

    /**
     * @author Ich (13.11.2020)
     */
    public static class SimpleSerialMetaPathBuilder {

        /**
         *
         */
        private final SimpleSerialMetaPathCreator sequenceMetaPathCreator;

        /**
         *
         */
        private final List<SequenceMetaPath> sequenceMetaPaths;

        /**
         * @param sequenceMetaPathCreator
         */
        private SimpleSerialMetaPathBuilder(final SimpleSerialMetaPathCreator sequenceMetaPathCreator) {
            this.sequenceMetaPathCreator = sequenceMetaPathCreator;
            sequenceMetaPaths = new ArrayList<>();
        }

        /**
         * @param startClass
         * @param endClass
         * @param edgeClasses
         */
        @SuppressWarnings("unchecked")
        public void add(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... edgeClasses) {
            SimpleMetaPath simpleMetaPath = sequenceMetaPathCreator.createSimpleMetaPath(startClass, endClass, edgeClasses);
            List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
            for (ElementaryMetaPath elementaryMetaPath : elementaryMetaPaths) {
                sequenceMetaPaths.add(elementaryMetaPath);
            }
        }

        /**
         * @param startClass
         * @param endClass
         * @param edgeClasses
         */
        @SuppressWarnings("unchecked")
        public void addSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... edgeClasses) {
            SimpleMetaPath simpleMetaPath = sequenceMetaPathCreator.createSimpleMetaPath(startClass, endClass, edgeClasses);
            sequenceMetaPaths.add(simpleMetaPath);
        }

        /**
         * @param startClass
         * @param endClass
         * @param baseResKeyOrName
         * @param edgeClasses
         */
        @SuppressWarnings("unchecked")
        public void addSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... edgeClasses) {
            SimpleMetaPath simpleMetaPath = sequenceMetaPathCreator.createSimpleMetaPath(startClass, endClass, baseResKeyOrName, edgeClasses);
            sequenceMetaPaths.add(simpleMetaPath);
        }

        /**
         * @param sequenceMetaPath
         */
        public void add(final ElementaryMetaPath elementaryMetaPath) {
            sequenceMetaPaths.add(elementaryMetaPath);
        }

        /**
         * @param sequenceMetaPath
         */
        public void add(final SimpleMetaPath simpleMetaPath) {
            sequenceMetaPaths.add(simpleMetaPath);
        }

        /**
         * @param sequenceMetaPaths
         */
        public void addAll(final List<SequenceMetaPath> sequenceMetaPaths) {
            sequenceMetaPaths.addAll(sequenceMetaPaths);
        }

        /**
         * @return
         */
        public SimpleSerialMetaPath build() {
            return new SimpleSerialMetaPath(sequenceMetaPaths);
        }

    }

}
