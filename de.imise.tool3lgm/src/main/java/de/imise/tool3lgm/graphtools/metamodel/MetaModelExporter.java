package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.gui.Tool3lgmMetaModelContextChooser;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

public class MetaModelExporter {

    private static final String HIERARCHY_DELIMITER = " > ";

    private static final String INDENTION = "    ";

    private final MetaModel metaModel;

    private final ElementsNameBuilder elementsNameBuilder;

    public static void main(final String[] args) {
        UserProperties.init();
        Tool3lgmModelType choosedModelType = new Tool3lgmMetaModelContextChooser().chooseModelType();
        new MetaModelExporter(choosedModelType.getMetaModel());
    }

    public MetaModelExporter(final MetaModel metaModel) {
        this.metaModel = metaModel;
        elementsNameBuilder = metaModel.getElementsNameBuilder();
        try {
            printMetaModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void printMetaModel() {
        List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies = getElementsHierarchies(metaModel.allNodesSet);

        System.out.println("###  ALL NODES (compact)");
        for (NamedObjectContainer<List<Class<?>>> o : elementsHierarchies) {
            System.out.println(o);
        }
        System.out.println();

        System.out.println("###  ALL NODES (tree)");
        printHierarchyTree(elementsHierarchies, false);
        System.out.println();

        System.out.println("### All Nodes (full) ######");
        printElementsWithEdges(metaModel, elementsHierarchies);
        System.out.println();

        List<NamedObjectContainer<List<Class<?>>>> edgesHierarchies = getElementsHierarchies(metaModel.allEdgesSet);

        System.out.println("###  ALL EDGES (compact)");
        for (NamedObjectContainer<List<Class<?>>> o : edgesHierarchies) {
            System.out.println(o);
        }
        System.out.println();

        System.out.println("###  ALL EDGES (tree)");
        printHierarchyTree(edgesHierarchies, false);
        System.out.println();

        System.out.println("### All EDGES (full) ######");
        printElementsWithEdges(metaModel, edgesHierarchies);
        System.out.println();

        System.out.println("RESOURCES");
        System.out.println();

        System.out.println("###  ALL NODES (tree)");
        printHierarchyTree(elementsHierarchies, true);
        System.out.println();

        System.out.println("###  ALL EDGES (tree with resources)");
        printHierarchyTree(edgesHierarchies, true);
        System.out.println();

    }

    private String getDisplayableName(final NamedObjectContainer<List<Class<?>>> noc, final int classIndex) {
        List<Class<?>> elementClassList = noc.getObject();
        Class<? extends ModelElement> elementClass = elementClassList.get(classIndex).asSubclass(ModelElement.class);
        StringBuilder sb = new StringBuilder();
        String displayableName = elementsNameBuilder.getDisplayableName(elementClass);
        String displayablePluralName = elementsNameBuilder.getDisplayablePluralName(elementClass);
        boolean isEdge = Edge.class.isAssignableFrom(elementClass);
        boolean isAssociationClass = isEdge && metaModel.hasEdgeType(elementClass, Edge.class); //Assoziationsklassen sollen auch als Knoten zählen
        String elementClassName = elementClass.getSimpleName();
        sb.append("(");
        sb.append(displayableName);
        sb.append(" | ");
        sb.append(displayablePluralName);
        if (isEdge) {
            String edgeForwardName = isEdge ? elementsNameBuilder.getForwardMetaAssociationName(elementClass.asSubclass(Edge.class)) : null;
            String edgeBackwardName = isEdge ? elementsNameBuilder.getBackwardMetaAssociationName(elementClass.asSubclass(Edge.class)) : null;
            sb.append(" | ");
            sb.append(edgeForwardName);
            sb.append(" | ");
            sb.append(edgeBackwardName);
        }
        sb.append(")");
        if ((!isEdge || isAssociationClass) && (displayableName.equals(elementClassName) || displayablePluralName.equals(elementClassName))) {
            sb.append("   ######################  MISSING RESOURCE ??? ###################");
        }
        return sb.toString();
    }

    private <T extends ModelElement> List<NamedObjectContainer<List<Class<?>>>> getElementsHierarchies(final Collection<Class<? extends T>> classes) {
        List<NamedObjectContainer<List<Class<?>>>> elementHierarchies = new ArrayList<>();
        for (Class<? extends T> elementClass : classes) {
            NamedObjectContainer<List<Class<?>>> noc = getSingleElementHierarchy(elementClass);
            elementHierarchies.add(noc);
        }
        Alphabetical.sort(elementHierarchies);
        return elementHierarchies;
    }

    private void printHierarchyTree(final List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies, final boolean appendResources) {
        List<Class<?>> lastElementHierarchy = new ArrayList<>();
        for (NamedObjectContainer<List<Class<?>>> noc : elementsHierarchies) {
            List<Class<?>> elementHierarchy = noc.getObject();
            for (int i = elementHierarchy.size() - 1; i >= 0; i--) {
                Class<? extends ModelElement> currentClass = elementHierarchy.get(i).asSubclass(ModelElement.class);
                if (!containsSameClass(lastElementHierarchy, currentClass, elementHierarchy.size() - i)) {
                    for (int h = i; h < elementHierarchy.size() - 1; h++) {
                        System.out.print(INDENTION);
                    }
                    if (Modifier.isAbstract(currentClass.getModifiers())) {
                        System.out.print("abstract ");
                    }
                    StringBuilder sb = new StringBuilder(currentClass.getSimpleName());
                    if (appendResources) {
                        sb.append("\t");
                        sb.append(getDisplayableName(noc, i));
                    }
                    System.out.println(sb);
                }
            }
            lastElementHierarchy = elementHierarchy;
        }
    }

    private boolean containsSameClass(final List<Class<?>> elementHierarchy, final Class<?> elementClass, final int indexFromEnd) {
        int indexInElementHierarchy = elementHierarchy.size() - indexFromEnd;
        if (indexInElementHierarchy < 0) {
            return false;
        }
        return elementHierarchy.get(indexInElementHierarchy) == elementClass;
    }

    private void printElementsWithEdges(final MetaModel metaModel, final List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies) {
        for (NamedObjectContainer<List<Class<?>>> noc : elementsHierarchies) {
            System.out.println(noc);
            Class<? extends ModelElement> elementClass = noc.getObject().get(0).asSubclass(ModelElement.class);
            if (MetaModel.isEdgeType(elementClass)) {
                Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                //                String edgeString = getEdgeStringOrg2(edgeClass, ModelElement.class, INDENTION);
                String edgeString = getEdgeString(edgeClass, INDENTION);
                System.out.println(edgeString);
            }
            List<Class<? extends Edge>> edgeClasses = Arrays.asList(metaModel.getEdgeTypes(elementClass));
            Alphabetical.sort(edgeClasses);
            List<NamedObjectContainer<Class<? extends Edge>>> edgesStrings = new ArrayList<>();
            for (Class<? extends Edge> edgeClass : edgeClasses) {
                if (isDefinedStartOrEndClass(elementClass, edgeClass)) {
                    //                    NamedObjectContainer<Class<? extends Edge>> nocE = new NamedObjectContainer<>(edgeClass, getEdgeStringOrg2(edgeClass, elementClass, INDENTION + INDENTION));
                    NamedObjectContainer<Class<? extends Edge>> nocE = new NamedObjectContainer<>(edgeClass, getEdgeString(edgeClass, INDENTION + INDENTION));
                    edgesStrings.add(nocE);
                }
            }
            Alphabetical.sort(edgesStrings);
            for (NamedObjectContainer<Class<? extends Edge>> nocE : edgesStrings) {
                System.out.println(nocE);
            }
            System.out.println();
        }
    }

    /**
     * Liefert true, wenn die Start- oder Endklasse der übergebenen Kantenklasse genau die übergebene Elementklasse ist. Anders als bei
     * den statischen Funktionen aus Edge für die Start- und Endklasse werden hier keine Ober- oder Unerklassen der übergebenen Elementklasse
     * zugelassen.
     *
     * @param elementClass
     * @param edgeClass
     * @return
     */
    private boolean isDefinedStartOrEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return getStartClass(edgeClass) == elementClass || getEndClass(edgeClass) == elementClass;
    }

    private NamedObjectContainer<List<Class<?>>> getSingleElementHierarchy(final Class<? extends ModelElement> elementClass) {
        List<Class<?>> classAndSuperClasses = getSuperClasses(elementClass, ModelElement.class);
        StringBuilder sb = new StringBuilder();
        for (int i = classAndSuperClasses.size() - 1; i >= 0; i--) {
            Class<?> clazz = classAndSuperClasses.get(i);
            if (Modifier.isAbstract(clazz.getModifiers())) {
                sb.append("abstract ");
            }
            sb.append(clazz.getSimpleName());
            if (i > 0) {
                sb.append(HIERARCHY_DELIMITER);
            }
        }
        NamedObjectContainer<List<Class<?>>> noc = new NamedObjectContainer<>(classAndSuperClasses, sb.toString());
        return noc;
    }

    private final List<Class<?>> getSuperClasses(final Class<?> clazz, final Class<?> superClass) {
        ImmutableList.Builder<Class<?>> classAndSuperClasses = ImmutableList.builder();
        Class<?> tmpClass = clazz;
        classAndSuperClasses.add(tmpClass);
        while (tmpClass != superClass && tmpClass != Object.class) {
            tmpClass = tmpClass.getSuperclass().asSubclass(ModelElement.class);
            classAndSuperClasses.add(tmpClass);
        }
        return classAndSuperClasses.build();
    }

    private String getEdgeString(final Class<? extends Edge> edgeClass, final String intention) {
        StringBuilder sb = new StringBuilder(intention);
        sb.append(edgeClass.getSimpleName());
        sb.append(": ");
        sb.append(getEdgeString(edgeClass, true));
        sb.append("  < - > ");
        sb.append(getEdgeString(edgeClass, false));
        return sb.toString();
    }

    private String getEdgeString(final Class<? extends Edge> edgeClass, final boolean forward) {
        StringBuilder sb = new StringBuilder();
        int minBackwardCardinality = forward ? MetaModel.getMinBackwardCardinality(edgeClass) : MetaModel.getMinForwardCardinality(edgeClass);
        int maxBackwardCardinality = forward ? MetaModel.getMaxBackwardCardinality(edgeClass) : MetaModel.getMaxForwardCardinality(edgeClass);
        String maxBackwardCardinalityString = maxBackwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxBackwardCardinality);
        int minForwardCardinality = forward ? MetaModel.getMinForwardCardinality(edgeClass) : MetaModel.getMinBackwardCardinality(edgeClass);
        int maxForwardCardinality = forward ? MetaModel.getMaxForwardCardinality(edgeClass) : MetaModel.getMaxBackwardCardinality(edgeClass);
        String maxForwardCardinalityString = maxForwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxForwardCardinality);

        String startClassName = forward ? elementsNameBuilder.getDisplayableName(getStartClass(edgeClass)) : elementsNameBuilder.getDisplayableName(getEndClass(edgeClass));
        String endClassName = forward ? elementsNameBuilder.getDisplayableName(getEndClass(edgeClass)) : elementsNameBuilder.getDisplayableName(getStartClass(edgeClass));
        String metaAssociationName = forward ? elementsNameBuilder.getForwardMetaAssociationName(edgeClass) : elementsNameBuilder.getBackwardMetaAssociationName(edgeClass);

        sb.append(startClassName);
        sb.append(" ");
        sb.append(metaAssociationName);
        sb.append(" [").append(minForwardCardinality).append(", ").append(maxForwardCardinalityString).append("] ");
        sb.append(endClassName);

        return sb.toString();
    }

    @SuppressWarnings("unused")
    private String getEdgeStringOrg2(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> readingDirectionStartClass, final String intention) {
        String edgeClassName = edgeClass.getSimpleName();
        StringBuilder sb = new StringBuilder();
        boolean forward = MetaModel.isStartClass(edgeClass, readingDirectionStartClass);
        int minBackwardCardinality = forward ? MetaModel.getMinBackwardCardinality(edgeClass) : MetaModel.getMinForwardCardinality(edgeClass);
        int maxBackwardCardinality = forward ? MetaModel.getMaxBackwardCardinality(edgeClass) : MetaModel.getMaxForwardCardinality(edgeClass);
        String maxBackwardCardinalityString = maxBackwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxBackwardCardinality);
        int minForwardCardinality = forward ? MetaModel.getMinForwardCardinality(edgeClass) : MetaModel.getMinBackwardCardinality(edgeClass);
        int maxForwardCardinality = forward ? MetaModel.getMaxForwardCardinality(edgeClass) : MetaModel.getMaxBackwardCardinality(edgeClass);
        String maxForwardCardinalityString = maxForwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxForwardCardinality);
        String fulldMetaAssociationName = forward ? elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass) : elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass);
        sb.append(intention).append(edgeClassName).append(": [").append(minBackwardCardinality).append(", ").append(maxBackwardCardinalityString).append("] ");
        sb.append(fulldMetaAssociationName).append(" [").append(minForwardCardinality).append(", ").append(maxForwardCardinalityString).append("]");
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private String getEdgeStringOrg(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> readingDirectionStartClass, final String intention) {
        String edgeClassName = edgeClass.getSimpleName();
        int minBackwardCardinality = MetaModel.getMinBackwardCardinality(edgeClass);
        int maxBackwardCardinality = MetaModel.getMaxBackwardCardinality(edgeClass);
        String maxBackwardCardinalityString = maxBackwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxBackwardCardinality);
        int minForwardCardinality = MetaModel.getMinForwardCardinality(edgeClass);
        int maxForwardCardinality = MetaModel.getMaxForwardCardinality(edgeClass);
        String maxForwardCardinalityString = maxForwardCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxForwardCardinality);
        String fullForwardMetaAssociationName = elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
        StringBuilder sb = new StringBuilder();
        sb.append(intention).append(edgeClassName).append(": [").append(minBackwardCardinality).append(", ").append(maxBackwardCardinalityString).append("] ");
        sb.append(fullForwardMetaAssociationName).append(" [").append(minForwardCardinality).append(", ").append(maxForwardCardinalityString).append("]");

        return sb.toString();
    }

}
