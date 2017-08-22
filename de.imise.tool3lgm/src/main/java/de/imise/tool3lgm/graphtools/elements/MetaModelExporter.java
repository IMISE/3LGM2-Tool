package de.imise.tool3lgm.graphtools.elements;

import static de.imise.tool3lgm.graphtools.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.elements.Edge.getMaxEndToStartCardinality;
import static de.imise.tool3lgm.graphtools.elements.Edge.getMaxStartToEndCardinality;
import static de.imise.tool3lgm.graphtools.elements.Edge.getMinEndToStartCardinality;
import static de.imise.tool3lgm.graphtools.elements.Edge.getMinStartToEndCardinality;
import static de.imise.tool3lgm.graphtools.elements.Edge.getStartClass;
import static de.imise.tool3lgm.graphtools.elements.Edge.isStartClass;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getBackwardMetaAssociationName;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.imise.tool3lgm.metamodel.tlgm_v3_0.TLGMOriginalMetaModel;
import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

public class MetaModelExporter {

    private static final String HIERARCHY_DELIMITER = " > ";

    private static final String INDENTION = "    ";

    public static void main(final String[] args) {
        MetaModel metaModel = new TLGMOriginalMetaModel();
        print(metaModel);
    }

    public static void print(final MetaModel metaModel) {
        List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies = getElementsHierarchies(ModelConstants.ALL_NODES);

        System.out.println("###  ALL NODES (compact)");
        for (NamedObjectContainer<List<Class<?>>> o : elementsHierarchies) {
            System.out.println(o + getDisplayableName(o));
        }
        System.out.println();

        System.out.println("###  ALL NODES (tree)");
        printHierarchyTree(elementsHierarchies);
        System.out.println();

        System.out.println("### All Nodes (full) ######");
        printElementsWithEdges(elementsHierarchies);
        System.out.println();

        List<NamedObjectContainer<List<Class<?>>>> edgesHierarchies = getElementsHierarchies(ModelConstants.ALL_EDGES);

        System.out.println("###  ALL EDGES (compact)");
        for (NamedObjectContainer<List<Class<?>>> o : edgesHierarchies) {
            System.out.println(o + getDisplayableName(o));
        }
        System.out.println();

        System.out.println("###  ALL EDGES (tree)");
        printHierarchyTree(edgesHierarchies);
        System.out.println();

        System.out.println("### All EDGES (full) ######");
        printElementsWithEdges(edgesHierarchies);
        System.out.println();

    }

    private static String getDisplayableName(final NamedObjectContainer<List<Class<?>>> noc) {
        return getDisplayableName(noc, 0);
    }

    private static String getDisplayableName(final NamedObjectContainer<List<Class<?>>> noc, final int classIndex) {
        List<Class<?>> elementClassList = noc.getObject();
        Class<? extends ModelElement> elementClass = elementClassList.get(classIndex).asSubclass(ModelElement.class);
        StringBuilder sb = new StringBuilder();
        String displayableName = ModelConstants.getDisplayableName(elementClass);
        String elementClassName = elementClass.getSimpleName();
        if (!elementClassName.equals(displayableName)) {
            sb.append(" (");
            sb.append(displayableName);
            sb.append(")");
        }
        return sb.toString();
    }

    private static List<NamedObjectContainer<List<Class<?>>>> getElementsHierarchies(final Class<? extends ModelElement>[] classes) {
        List<Class<? extends ModelElement>> allClasses = Lists.newArrayList(classes);
        List<NamedObjectContainer<List<Class<?>>>> elementHierarchies = Lists.newArrayList();
        for (Class<? extends ModelElement> elementClass : allClasses) {
            NamedObjectContainer<List<Class<?>>> noc = getSingleElementHierarchy(elementClass, false);
            elementHierarchies.add(noc);
        }
        Alphabetical.sort(elementHierarchies);
        return elementHierarchies;
    }

    private static void printHierarchyTree(final List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies) {
        List<Class<?>> lastElementHierarchy = Lists.newArrayList();
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
                    System.out.println(currentClass.getSimpleName() + getDisplayableName(noc, i));
                }
            }
            lastElementHierarchy = elementHierarchy;
        }
    }

    private static boolean containsSameClass(final List<Class<?>> elementHierarchy, final Class<?> elementClass, final int indexFromEnd) {
        int indexInElementHierarchy = elementHierarchy.size() - indexFromEnd;
        if (indexInElementHierarchy < 0) {
            return false;
        }
        return elementHierarchy.get(indexInElementHierarchy) == elementClass;
    }

    private static void printElementsWithEdges(final List<NamedObjectContainer<List<Class<?>>>> elementsHierarchies) {
        for (NamedObjectContainer<List<Class<?>>> noc : elementsHierarchies) {
            System.out.println(noc);
            Class<? extends ModelElement> elementClass = noc.getObject().get(0).asSubclass(ModelElement.class);
            if (ModelConstants.isEdgeType(elementClass)) {
                Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                //                String edgeString = getEdgeStringOrg2(edgeClass, ModelElement.class, INDENTION);
                String edgeString = getEdgeString(edgeClass, INDENTION);
                System.out.println(edgeString);
            }
            List<Class<? extends Edge>> edgeClasses = Lists.newArrayList(ModelConstants.getEdgeTypes(elementClass));
            Alphabetical.sort(edgeClasses);
            List<NamedObjectContainer<Class<? extends Edge>>> edgesStrings = new ArrayList<>();
            for (Class<? extends Edge> edgeClass : edgeClasses) {
                if (isDefinedStartOrEndClass(elementClass, edgeClass)) {
                    //                    NamedObjectContainer<Class<? extends Edge>> nocE = new NamedObjectContainer<Class<? extends Edge>>(edgeClass, getEdgeStringOrg2(edgeClass, elementClass, INDENTION + INDENTION));
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
    private static boolean isDefinedStartOrEndClass(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        return getStartClass(edgeClass) == elementClass || getEndClass(edgeClass) == elementClass;
    }

    private static NamedObjectContainer<List<Class<?>>> getSingleElementHierarchy(final Class<? extends ModelElement> elementClass, final boolean appendDisplayableName) {
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
        if (appendDisplayableName) {
            sb.append(" (");
            sb.append(ModelConstants.getDisplayableName(elementClass));
            sb.append(")");
        }
        NamedObjectContainer<List<Class<?>>> noc = new NamedObjectContainer<>(classAndSuperClasses, sb.toString());
        return noc;
    }

    private static final List<Class<?>> getSuperClasses(final Class<?> clazz, final Class<?> superClass) {
        ImmutableList.Builder<Class<?>> classAndSuperClasses = ImmutableList.builder();
        Class<?> tmpClass = clazz;
        classAndSuperClasses.add(tmpClass);
        while (tmpClass != superClass && tmpClass != Object.class) {
            tmpClass = tmpClass.getSuperclass().asSubclass(ModelElement.class);
            classAndSuperClasses.add(tmpClass);
        }
        return classAndSuperClasses.build();
    }

    private static String getEdgeString(final Class<? extends Edge> edgeClass, final String intention) {
        StringBuilder sb = new StringBuilder(intention);
        sb.append(edgeClass.getSimpleName());
        sb.append(": ");
        sb.append(getEdgeString(edgeClass, true));
        sb.append("  < - > ");
        sb.append(getEdgeString(edgeClass, false));
        return sb.toString();
    }

    private static String getEdgeString(final Class<? extends Edge> edgeClass, final boolean forward) {
        StringBuilder sb = new StringBuilder();
        int minEndToStartCardinality = forward ? getMinEndToStartCardinality(edgeClass) : getMinStartToEndCardinality(edgeClass);
        int maxEndToStartCardinality = forward ? getMaxEndToStartCardinality(edgeClass) : getMaxStartToEndCardinality(edgeClass);
        String maxEndToStartCardinalityString = maxEndToStartCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxEndToStartCardinality);
        int minStartToEndCardinality = forward ? getMinStartToEndCardinality(edgeClass) : getMinEndToStartCardinality(edgeClass);
        int maxStartToEndCardinality = forward ? getMaxStartToEndCardinality(edgeClass) : getMaxEndToStartCardinality(edgeClass);
        String maxStartToEndCardinalityString = maxStartToEndCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxStartToEndCardinality);

        String startClassName = forward ? ModelConstants.getDisplayableName(getStartClass(edgeClass)) : ModelConstants.getDisplayableName(getEndClass(edgeClass));
        String endClassName = forward ? ModelConstants.getDisplayableName(getEndClass(edgeClass)) : ModelConstants.getDisplayableName(getStartClass(edgeClass));
        String metaAssociationName = forward ? ModelConstants.getForwardMetaAssociationName(edgeClass) : getBackwardMetaAssociationName(edgeClass);

        sb.append(startClassName);
        sb.append(" ");
        sb.append(metaAssociationName);
        sb.append(" [").append(minStartToEndCardinality).append(", ").append(maxStartToEndCardinalityString).append("] ");
        sb.append(endClassName);

        return sb.toString();
    }

    private static String getEdgeStringOrg2(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> readingDirectionStartClass, final String intention) {
        String edgeClassName = edgeClass.getSimpleName();
        StringBuilder sb = new StringBuilder();
        boolean forward = isStartClass(edgeClass, readingDirectionStartClass);
        int minEndToStartCardinality = forward ? getMinEndToStartCardinality(edgeClass) : getMinStartToEndCardinality(edgeClass);
        int maxEndToStartCardinality = forward ? getMaxEndToStartCardinality(edgeClass) : getMaxStartToEndCardinality(edgeClass);
        String maxEndToStartCardinalityString = maxEndToStartCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxEndToStartCardinality);
        int minStartToEndCardinality = forward ? getMinStartToEndCardinality(edgeClass) : getMinEndToStartCardinality(edgeClass);
        int maxStartToEndCardinality = forward ? getMaxStartToEndCardinality(edgeClass) : getMaxEndToStartCardinality(edgeClass);
        String maxStartToEndCardinalityString = maxStartToEndCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxStartToEndCardinality);
        String fulldMetaAssociationName = forward ? ModelConstants.getFullForwardMetaAssociationName(edgeClass) : ModelConstants.getFullBackwardMetaAssociationName(edgeClass);
        sb.append(intention).append(edgeClassName).append(": [").append(minEndToStartCardinality).append(", ").append(maxEndToStartCardinalityString).append("] ");
        sb.append(fulldMetaAssociationName).append(" [").append(minStartToEndCardinality).append(", ").append(maxStartToEndCardinalityString).append("]");
        return sb.toString();
    }

    private static String getEdgeStringOrg(final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> readingDirectionStartClass, final String intention) {
        String edgeClassName = edgeClass.getSimpleName();
        int minEndToStartCardinality = getMinEndToStartCardinality(edgeClass);
        int maxEndToStartCardinality = getMaxEndToStartCardinality(edgeClass);
        String maxEndToStartCardinalityString = maxEndToStartCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxEndToStartCardinality);
        int minStartToEndCardinality = getMinStartToEndCardinality(edgeClass);
        int maxStartToEndCardinality = getMaxStartToEndCardinality(edgeClass);
        String maxStartToEndCardinalityString = maxStartToEndCardinality == Integer.MAX_VALUE ? "N" : Integer.toString(maxStartToEndCardinality);
        String fullForwardMetaAssociationName = ModelConstants.getFullForwardMetaAssociationName(edgeClass);
        StringBuilder sb = new StringBuilder();
        sb.append(intention).append(edgeClassName).append(": [").append(minEndToStartCardinality).append(", ").append(maxEndToStartCardinalityString).append("] ");
        sb.append(fullForwardMetaAssociationName).append(" [").append(minStartToEndCardinality).append(", ").append(maxStartToEndCardinalityString).append("]");

        return sb.toString();
    }

}
