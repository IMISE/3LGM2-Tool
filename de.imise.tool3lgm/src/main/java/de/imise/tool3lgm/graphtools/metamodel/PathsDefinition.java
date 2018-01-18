package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;
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
                init();
            } catch (InvalidPathException exp) {
                exp.printStackTrace();
            }
            initElementTypesInPathes();
        }
    }

    protected abstract void init() throws InvalidPathException;

    private void initInternal() {
    }

    static int metaPathCount = 1;

    /**
     * Setzt den übergenen Metapfad in die HashMap aller Metapfade
     *
     * @param metaPaths
     */
    public final void put(final MetaPath[] metaPaths) {
        for (int i = 0; i < metaPaths.length; i++) {
            //            Sys.errn(2, metaPathCount++ + " " + metaPaths[i] + "(" + metaPaths[0].getStartClass().getSimpleName() + " -> " + metaPaths[0].getEndClass().getSimpleName() + " ### " + metaPaths[i].getEdgeClasses()[0].getSimpleName() + ")");
            //            System.err.println(metaPathCount++ + " " + metaPaths[i] + "(" + metaPaths[i].getStartClass().getSimpleName() + " -> " + metaPaths[i].getEndClass().getSimpleName() + " ### " + metaPaths[i].getEdgeClasses()[0].getSimpleName() + ")");
            String pathsKey = calculateKey(metaPaths[i].getStartClass(), metaPaths[i].getEndClass());
            //            System.err.println(metaPathes.get(pathsKey));
            metaPathes.put(pathsKey, metaPaths[i]);
        }
    }

    public final void put(final MetaPath metaPath) {
        String pathsKey = calculateKey(metaPath.getStartClass(), metaPath.getEndClass());
        //            System.err.println(metaPathes.get(pathsKey));
        metaPathes.put(pathsKey, metaPath);
        //            System.err.println(metaPathes.get(pathsKey));
        //            System.err.println();
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

    public static final MetaPath[] createSimpleMetaPaths(final Class<? extends Edge> edgeClass) {
        Class<? extends ModelElement> edgeStartClass = Edge.getStartClass(edgeClass);
        Class<? extends ModelElement> edgeEndClass = Edge.getEndClass(edgeClass);
        Class<? extends ModelElement>[] startClasses = ModelConstants.getInstanciableAssignableClasses(edgeStartClass);
        Class<? extends ModelElement>[] endClasses = ModelConstants.getInstanciableAssignableClasses(edgeEndClass);
        Set<Class<?>> allStartClasses = ReflectionUtils.getClassesWithSuperClasses(startClasses, edgeStartClass.getSuperclass());
        Set<Class<?>> allEndClasses = ReflectionUtils.getClassesWithSuperClasses(endClasses, edgeEndClass.getSuperclass());
        MetaPath[] returnPaths = new MetaPath[allStartClasses.size() * allEndClasses.size()];
        int currentPathIndex = 0;
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
                        }, new Color[] {
                                Color.ORANGE,
                                Color.BLUE,
                                Color.GREEN
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
                returnPaths[currentPathIndex++] = metaPath;
            }
        }
        return returnPaths;
    }

}