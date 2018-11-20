/*
 * Created on 20.07.2004
 */
package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ConnectionState.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ConnectionState.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ConnectionState.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartOrEndClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * Stellt Funktionen bereit, mit denen konkrete Pfade zu definierten Metapfaden ermittelt werden können.
 *
 * @author Thomas Rudert
 * @author AXS (5.10.2007)
 */
public final class PathFinderOld {

    /**
     * Liefert alle <code>MetaPath</code>es, die zwischen Elementen der Art <code>startClass</code> und <code>endClass</code> definiert sind.
     *
     * @param startClass
     * @param endClass
     * @return
     */
    public static final Collection<AbstractMetaPath> getMetaPathes(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        MetaPathDefinition pathsDefinition = ModelConstants.getPathsDefinition();
        return pathsDefinition.getMetaPaths(startClass, endClass, true, true);
    }

    /**
     * @return
     */
    public static final Set<Class<? extends ModelElement>> getElementClassesInPathes() {
        MetaPathDefinition pathsDefinition = ModelConstants.getPathsDefinition();
        return pathsDefinition.getStartElementClassesInPaths(true, true);
    }

    //	/**
    //	 * Wenn die übergebene <code>startClass</code> die Start- oder Endklasse der ersten Assoziation dieses
    //	 * Pfades ist und die übergebene <code>endClass</code> in der letzten Assoziation des Pfades vorkommt,
    //	 * dann kommt der übergebene Pfad zurück.<br />
    //	 *
    //	 * Wenn die übergebene <code>startClass</code> eine Endklasse des übergebenen Metapfades ist und die übergebene
    //	 * <code>endClass</code> eine Startklasse des übergebenen Metapfades, dann kommt ein Metapfad zurück, in dem
    //	 * die Assoziationen genau anders herum sind als im übergebenen MetaPfad.<br />
    //	 *
    //	 * Wenn Start- und Endklassen in keiner der Richtungen übereinstimmen, kommt <code>null</code> zurück.
    //	 *
    //	 * @param startClass
    //	 * @param endClass
    //	 * @param metaPath
    //	 * @return
    //	 * /
    //    public static final MetaPath getDirectedMetaPath(Class<? extends ModelElement> startClass, Class<? extends ModelElement> endClass, MetaPath metaPath) {
    //		boolean switchAssociations = false;
    //		for (int ep = 0 ; ep<metaPath.countPathes(); ep++){
    //			Class<? extends Edge>[] edgeClasses = metaPath.getEdgeClasses(ep);
    //			int lastIndex = edgeClasses.length-1;
    //			if (Edge.isStartOrEndClass(edgeClasses[0], startClass) && Edge.isStartOrEndClass(edgeClasses[lastIndex], endClass))
    //				return metaPath;
    //			if (Edge.isStartOrEndClass(edgeClasses[0], endClass) && Edge.isStartOrEndClass(edgeClasses[lastIndex], startClass))
    //				switchAssociations = true;
    //		}
    //		//wenn switchAssociations immer noch false ist und er bis hier gekommen ist, dann passt der MetaPath gar
    //		//nicht zu den Start und Zielklassen -> es kommt null zurück
    //		if (!switchAssociations)
    //			return null;
    //
    //		@SuppressWarnings("unchecked")
    //		Class<? extends Edge>[][] path = new Class[metaPath.countPathes()][metaPath.getEdgeClasses(0).length];
    //		//von allen Assoziationslisten alle Assoziationen umdrehen
    //		for (int ep = 0 ; ep<metaPath.countPathes(); ep++){
    //			Class<? extends Edge>[] edgeClasses = metaPath.getEdgeClasses(ep);
    //			int lastIndex = edgeClasses.length-1;
    //			for (int i=0; i<edgeClasses.length; i++){
    //				path[ep][lastIndex-i] = edgeClasses[i];
    //			}
    //		}
    //		return new MetaPath(startClass, endClass, path);
    //	}

    /**
     * @param element1
     * @param element2
     * @param metaPath
     * @param doc
     * @return FORWARD / BACKWARD / DOUBLE if path.isImmediate otherwise <code>null</code>
     */
    @SuppressWarnings("incomplete-switch") //ANY ist egal, weil das hier nie passiert
    public static final ConnectionState isConnected(final ModelElement element1, final ModelElement element2, final MetaPathOld metaPath) {
        boolean searchParts = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS);
        boolean searchParents = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS);
        if (!searchParts && !searchParents || element1 == element2 || metaPath.isRecursiveSubordinationPath()) { //statt isRecursiveSubordinationPath() wurde hier mal auf HasPartEdge getestet. Was genau das macht ist mir (AXS) nicht (mehr) klar. Deswegen habe ich es jetzt von der Bedeutung so gleich wie mäglich gemacht
            return isConnectedInternal(element1, element2, metaPath);
        }
        ConnectionState retVal = null;
        Set<ModelElement> set1 = new HashSet<>();
        Set<ModelElement> set2 = new HashSet<>();
        set1.add(element1);
        set2.add(element2);
        if (searchParts) {
            set1.addAll(element1.getPartElements());
            set2.addAll(element2.getPartElements());
        }
        if (searchParents) {
            set1.addAll(element1.getParentElements());
            set2.addAll(element2.getParentElements());
        }
        for (ModelElement me1 : set1) {
            for (ModelElement me2 : set2) {
                ConnectionState con = isConnectedInternal(me1, me2, metaPath);
                if (con != null) {
                    switch (con) {
                    case DOUBLE:
                        return DOUBLE;
                    case FORWARD:
                        if (retVal == BACKWARD) {
                            return DOUBLE;
                        }
                        retVal = FORWARD;
                        break;
                    case BACKWARD:
                        if (retVal == FORWARD) {
                            return DOUBLE;
                        }
                        retVal = BACKWARD;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    /**
     * @param element1
     * @param element2
     * @param metaPath
     * @return
     */
    private static final ConnectionState isConnectedInternal(final ModelElement element1, final ModelElement element2, final MetaPathOld metaPath) {
        if (!metaPath.getStartClass().isAssignableFrom(element1.getClass()) || !metaPath.getEndClass().isAssignableFrom(element2.getClass())) {
            if (metaPath.getEndClass().isAssignableFrom(element1.getClass()) && metaPath.getStartClass().isAssignableFrom(element2.getClass())) {
                return isConnectedInternal(element2, element1, metaPath);
            }
            return null;
        }

        for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
            // direkt vebunden?
            if (metaPath.isImmediate(pathIndex)) {
                Class<? extends Edge> edgeClass = metaPath.getEdgeClasses(pathIndex)[0];
                boolean connectedTo = element1.isConnectedTo(element2, edgeClass);
                boolean connectedFrom = element1.isConnectedFrom(element2, edgeClass);
                if (connectedTo && connectedFrom) {
                    return DOUBLE;
                } else if (connectedTo) {
                    return FORWARD;
                } else if (connectedFrom) {
                    return HasPartEdge.class.isAssignableFrom(edgeClass) ? null : BACKWARD;
                }
            } else {
                return isConnected(element1, element2, metaPath, 0, pathIndex);
            }
        }
        return null;
    }

    /**
     * @param current
     * @param end
     * @param metaPath
     * @param position
     * @param pathIndex
     * @return
     */
    private static final ConnectionState isConnected(final ModelElement current, final ModelElement end, final MetaPathOld metaPath, final int position, final int pathIndex) {
        if (position == metaPath.getLength(pathIndex)) {
            if (current.equals(end)) {
                return DOUBLE;
            }
            return null;
        }
        ConnectionState retVal;
        List<ModelElement> elements = current.getConnectedElements(metaPath.getEdgeClasses(pathIndex)[position]);
        for (ModelElement me : elements) {
            if ((retVal = isConnected(me, end, metaPath, position + 1, pathIndex)) != null) {
                if (metaPath.getPathDirectionSourceEdgeIndex() == position) {
                    if (current.isConnectedTo(me) && current.isConnectedFrom(me)) {
                        return DOUBLE;
                    } else if (current.isConnectedTo(me)) {
                        return FORWARD;
                    } else if (current.isConnectedFrom(me)) {
                        return BACKWARD;
                    } else {
                        return null;
                    }
                }
                return retVal;
            }
        }
        return null;
    }

    //	/**
    //	 * Liefert alle Elemente, die im angegebenen Modell mit dem übergebenen Element über den angegebenen Pfad verbunden sind.
    //	 *
    //	 * @param me
    //	 * @param metaPath
    //	 * @param gdcoll
    //	 * @return
    //	 * /
    //	public static final Set<ModelElement> getDirectConnectedElements(ModelElement me, MetaPath metaPath, GDCollection gdcoll){
    //		Set<ModelElement> startElements = new HashSet<ModelElement>();
    //		startElements.add(me);
    //		Set<ModelElement> endElements = new HashSet<ModelElement>();
    //		for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++){
    //			for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++){
    //				endElements.clear();
    //				for (ModelElement startElem : startElements)
    //					endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[assoIndex]));
    //			}
    //			startElements = endElements;
    //		}
    //		return endElements;
    //	}

    /**
     * Liefert alle Elemente, die im angegebenen Modell mit dem übergebenen Element über den angegebenen Pfad verbunden sind.
     *
     * @param me
     * @param metaPath
     * @return
     */
    public static final Set<ModelElement> getDirectConnectedElements(final ModelElement me, final MetaPathOld metaPath) {
        Set<ModelElement> startElements = new HashSet<>();
        startElements.add(me);
        Set<ModelElement> endElements = new HashSet<>();
        int pathLength = metaPath.getLength();
        for (int assoIndex = 0; assoIndex < pathLength; assoIndex++) {
            for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
                endElements.clear();
                Class<? extends Edge>[] subPathEdgeClasses = metaPath.getEdgeClasses(pathIndex);
                Class<? extends Edge> subPathEdgeClass = subPathEdgeClasses[assoIndex];
                for (ModelElement startElem : startElements) {
                    Class<? extends ModelElement> pathStepEndClass = Edge.getOther(subPathEdgeClass, startElem.getClass());
                    //bei der letzten Kanten muss man gucken, ob die verbundene Klasse der Kante oder das Endelement des Pfades spezieller ist und die speziellere Klasse als Endklasse nehmen
                    if (assoIndex == pathLength - 1) {
                        Class<? extends ModelElement> metaPathEndClass = metaPath.getEndClass();
                        if (pathStepEndClass.isAssignableFrom(metaPathEndClass)) {
                            pathStepEndClass = metaPathEndClass;
                        }
                    }
                    endElements.addAll(startElem.getConnectedElements(pathStepEndClass, subPathEdgeClass));
                }
            }
            startElements.clear();
            startElements.addAll(endElements);
        }
        return endElements;
    }

    //	/**
    //	 * Liefert alle Elemente die mit dem übergebeben Element oder seinen Elternelementen über den
    //	 * angegebenen Pfad verbunden Elemente zurück.
    //	 *
    //	 * @param me
    //	 * @param targetElementClass
    //	 * 		Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des
    //	 * 		Metapfades übereinstimmen, sondern kann eine spezielle Unterklasse sein.
    //	 * @param metaPath
    //	 * @param gdcoll
    //	 * @return
    //	 * /
    //	public static final HashSet<ModelElement> getConnectedElements(ModelElement me, Class<? extends ModelElement>targetElementClass, MetaPath metaPath, GDCollection gdcoll){
    //		HashSet<ModelElement> startElements = me.getParentElements();
    //		startElements.add(me);
    //		HashSet<ModelElement> endElements = null;
    //		boolean pathStartClass = isStartOrEndClass(metaPath.getEdgeClasses(0)[0], me.getClass());
    //		for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++){
    //			for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++){
    //				endElements = new HashSet<ModelElement>();
    //				int index = pathStartClass?assoIndex:metaPath.getLength()-1-assoIndex;
    //				for (ModelElement startElem : startElements)
    //					endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[index]));
    //			}
    //			startElements = endElements;
    //		}
    //		if (endElements==null)
    //			return null;
    //		HashSet<ModelElement> reallyEndElements = new HashSet<ModelElement>(endElements.size());
    //		for (ModelElement elem : endElements)
    //			if (targetElementClass.isAssignableFrom(elem.getClass()))
    //				reallyEndElements.add(elem);
    //		return reallyEndElements;
    //	}

    /**
     * Liefert alle Elemente die mit dem übergebeben Element oder seinen Elternelementen über den angegebenen Pfad verbunden Elemente zurück.
     *
     * @param me
     * @param metaPath
     * @return
     */
    public static final Set<ModelElement> getConnectedElements(final ModelElement me, final MetaPathOld metaPath) {
        return getConnectedElements(me, metaPath.getEndClass(), metaPath);
    }

    /**
     * Liefert alle Elemente die mit dem übergebeben Element oder seinen Elternelementen über den angegebenen Pfad verbunden Elemente zurück.
     *
     * @param me
     * @param targetElementClass Klasse, von der die Zielelemente sein sollen. Diese muss nicht mit der letzten Elementklasse des Metapfades
     *            übereinstimmen, sondern kann eine spezielle Unterklasse sein.
     * @param metaPath
     * @return
     */
    public static final Set<ModelElement> getConnectedElements(final ModelElement me, final Class<? extends ModelElement> targetElementClass, final MetaPathOld metaPath) {
        Set<ModelElement> startElements = me.getParentElements();
        startElements.add(me);
        Set<ModelElement> endElements = null;
        //das übergebene Element ist Startklasse des Pfades?
        boolean pathStartClass = isStartOrEndClass(metaPath.getEdgeClasses(0)[0], me.getClass());
        //für alle Assoziationen jedes inneren MetaPfades des übergebenen Gesamtmetapfades
        for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++) {
            //für jeden inneren MetaPfad
            for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
                endElements = new HashSet<>();
                //wenn das übergebene Element von der Startklasse des Pfades ist, laufe die einzelnen
                //Assoziationen des Metapfades von vorne durch, sonst von hinten
                int index = pathStartClass ? assoIndex : metaPath.getLength() - 1 - assoIndex;
                //zu allen Endelementen alle über die aktuelle Assoziation verbunenen Elemente hinzufügen
                for (ModelElement startElem : startElements) {
                    endElements.addAll(startElem.getConnectedElements(metaPath.getEdgeClasses(pathIndex)[index]));
                }
            }
            //Startelemente auf die Endelemente setzen
            startElements.clear();
            startElements.addAll(endElements);
        }
        //wenn es keine Endelemente gibt
        if (endElements == null) {
            return null;
        }
        Set<ModelElement> reallyEndElements = new HashSet<>(endElements.size());
        for (ModelElement elem : endElements) {
            if (targetElementClass.isAssignableFrom(elem.getClass())) {
                reallyEndElements.add(elem);
            }
        }
        return reallyEndElements;
    }

}