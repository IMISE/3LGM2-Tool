package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.InvalidPathException;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.util.ReflectionUtils;

public abstract class PathsDefinition {

    /**
     * COMMENTME
     */
    private final Multimap<String, MetaPath> metaPathes = HashMultimap.create();

    /**
     * Liste der Elementtypen, für die ein Pfad in init() definiert wurde (wird z.B. für die Comboboxen in der Matrixsicht gebraucht)
     */
    private final Set<Class<? extends ModelElement>> elementClassesInPathes = new HashSet<>();

    public PathsDefinition() {
        if (metaPathes.isEmpty()) {
            try {
                //Alle simplen Pfade für die Kante zu jeder Pfaddefinition hinzufügen
                for (Class<? extends Edge> edgeClass : ModelConstants.ALL_EDGES_SET) {
                    putSimpleMetaPaths(edgeClass);
                }
                init();
            } catch (InvalidPathException exp) {
                exp.printStackTrace();
            }
            initElementTypesInPathes();
        }
    }

    /**
     * Unterklassen können hier weitere Pfade hinzufügen, die über die Kante hinausgehen
     *
     * @throws InvalidPathException
     */
    protected abstract void init() throws InvalidPathException;

    private void initInternal() {
    }

    static int metaPathCount = 1;

    public final void put(final MetaPath metaPath) {
        String pathsKey = calculateKey(metaPath.getStartClass(), metaPath.getEndClass());
        metaPathes.put(pathsKey, metaPath);
    }

    /**
     * Berechnet einen eindeutigen Schlüssel für 2 übergebene Klassen.
     *
     * @param elementClass1
     * @param elementClass2
     * @return
     */
    private static final String calculateKey(final Class<? extends ModelElement> elementClass1, final Class<? extends ModelElement> elementClass2) {
        int hash1 = elementClass1.hashCode();
        int hash2 = elementClass2.hashCode();
        StringBuilder sb = new StringBuilder();
        if (hash1 < hash2) {
            sb.append(elementClass1.hashCode());
            sb.append(elementClass2.hashCode());
        } else {
            sb.append(elementClass2.hashCode());
            sb.append(elementClass1.hashCode());
        }
        return sb.toString();
    }

    /**
     * Bildet das Set aller Klassen, für die Pfade definiert wurden.
     */
    private void initElementTypesInPathes() {
        for (MetaPath path : metaPathes.values()) {
            elementClassesInPathes.add(path.getStartClass());
            elementClassesInPathes.add(path.getEndClass());
        }
    }

    /**
     * @return
     */
    public final Set<Class<? extends ModelElement>> getElementClassesInPathes() {
        initInternal();
        return elementClassesInPathes;
    }

    /**
     * @param resourceString
     * @return
     */
    protected static final String s(final String resourceString) {
        return getResString(resourceString);
    }

    /**
     * Liefert alle <code>MetaPath</code>es, die zwischen Elementen der Art <code>startClass</code> und <code>endClass</code> definiert sind.
     *
     * @param startClass
     * @param endClass
     * @return
     */
    public final Collection<MetaPath> getMetaPathes(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        initInternal();
        return metaPathes.get(calculateKey(startClass, endClass));
    }

    private final void putSimpleMetaPaths(final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
        Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
        Class<? extends ModelElement>[] startClasses = ModelConstants.getInstanciableAssignableClasses(edgeStartClass);
        Class<? extends ModelElement>[] endClasses = ModelConstants.getInstanciableAssignableClasses(edgeEndClass);
        Set<Class<?>> allStartClasses = ReflectionUtils.getClassesWithSuperClasses(startClasses, edgeStartClass.getSuperclass());
        Set<Class<?>> allEndClasses = ReflectionUtils.getClassesWithSuperClasses(endClasses, edgeEndClass.getSuperclass());
        for (Class<?> start : allStartClasses) {
            Class<? extends ModelElement> startClass = start.asSubclass(ModelElement.class);
            for (Class<?> end : allEndClasses) {
                Class<? extends ModelElement> endClass = end.asSubclass(ModelElement.class);
                MetaPath metaPath = null;
                if (ModelConstants.isPartOfEdge(edgeClass)) {
                    metaPath = new MetaPath(startClass, endClass, new Class[][] {
                            {
                                    edgeClass
                            }
                    }, s("zeile") + " " + ModelConstants.getMetaAssociationName(edgeClass, false, Edge.FORWARD) + " " + s("spalte"), true);

                } else {
                    String forwardName = ModelConstants.getMetaAssociationName(edgeClass, false, Edge.FORWARD, true, true);
                    if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
                        metaPath = new MetaPath(startClass, endClass, new Class[][] {
                                {
                                        edgeClass
                                }
                        }, new String[] {
                                ModelConstants.getMetaAssociationName(edgeClass, false, Edge.DOUBLE, true, true, " " + s("text_und") + " "),
                                forwardName,
                                ModelConstants.getMetaAssociationName(edgeClass, false, Edge.BACKWARD, true, true),
                        });
                    } else {
                        metaPath = new MetaPath(startClass, endClass, new Class[][] {
                                {
                                        edgeClass
                                }
                        }, forwardName);
                    }
                }
                put(metaPath);
            }
        }
    }

}