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

    /**
     * Legt den übergebenen Metapfad zur Sammlung aller Metapfade hinzu. Ist registerPathForSubClasses == <code>true</code>, wird
     * für jede Kombination aus Unterklassen der Start- und Endklasse des übergebenen Pfades ebenfalls dieser Pfad hinzugefügt.
     *
     * @param metaPath
     * @param registerPathForSubClasses wenn true, dann wird der Pfad auch für alle Unterklassen der übergebenen Start- und
     *            Zielklassen angelegt. Wenn <code>false</code>, dann nur für die übergebenen Klassen.
     */
    public final void put(final MetaPath metaPath, final boolean registerPathForSubClasses) {
        if (!registerPathForSubClasses) {
            registerPath(metaPath);
        } else {
            Class<? extends ModelElement> pathStartClass = metaPath.getStartClass();
            Class<? extends ModelElement> pathEndClass = metaPath.getEndClass();
            Class<? extends ModelElement>[] startClasses = ModelConstants.getInstanciableAssignableClasses(pathStartClass);
            Class<? extends ModelElement>[] endClasses = ModelConstants.getInstanciableAssignableClasses(pathEndClass);
            Set<Class<?>> allStartClasses = ReflectionUtils.getClassesWithSuperClasses(startClasses, pathStartClass.getSuperclass());
            Set<Class<?>> allEndClasses = ReflectionUtils.getClassesWithSuperClasses(endClasses, pathEndClass.getSuperclass());
            for (Class<?> start : allStartClasses) {
                Class<? extends ModelElement> startClass = start.asSubclass(ModelElement.class);
                for (Class<?> end : allEndClasses) {
                    Class<? extends ModelElement> endClass = end.asSubclass(ModelElement.class);
                    //wenn die Start- und Endklassen die vom übergebenen Pfad sind, muss man keinen neuen Pfad anlegen
                    MetaPath newMetaPath = pathStartClass == startClass && pathEndClass == endClass ? metaPath : new MetaPath(startClass, endClass, metaPath);
                    registerPath(newMetaPath);
                }
            }
        }
    }

    /**
     * Legt den übergebenen Metapfad zur Sammlung aller Metapfade hinzu. Außerdem wird für jede Kombination aus Unterklassen
     * der Start- und Endklasse des übergebenen Pfades ebenfalls dieser Pfad hinzugefügt.
     *
     * @param metaPath
     */
    public final void put(final MetaPath metaPath) {
        put(metaPath, true);
    }

    private void registerPath(final MetaPath metaPath) {
        Class<? extends ModelElement> startClass = metaPath.getStartClass();
        Class<? extends ModelElement> endClass = metaPath.getEndClass();
        String pathsKey = calculateKey(startClass, endClass);
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
                if (ModelConstants.isHasPartEdge(edgeClass)) {
                    metaPath = new MetaPath(startClass, endClass, new Class[][] {
                            {
                                    edgeClass
                            }
                    }, ModelConstants.getMetaAssociationName(edgeClass, false, Edge.FORWARD), true);

                } else {
                    String forwardName = ModelConstants.getMetaAssociationName(edgeClass, false, Edge.FORWARD);
                    if (ModelConstants.isDoubleMeaningEdge(edgeClass)) {
                        metaPath = new MetaPath(startClass, endClass, new Class[][] {
                                {
                                        edgeClass
                                }
                        }, new String[] {
                                ModelConstants.getMetaAssociationName(edgeClass, false, Edge.DOUBLE, false, false, " " + s("und") + " "),
                                forwardName,
                                ModelConstants.getMetaAssociationName(edgeClass, false, Edge.BACKWARD),
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