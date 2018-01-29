/*
 * Created on 20.07.2004
 */
package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.FORWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.NOTCONNECTED;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.isStartOrEndClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.PathsDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.IsPartOfEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

/**
 * Stellt Funktionen bereit, mit denen konkrete Pfade zu definierten Metapfaden ermittelt werden können.
 *
 * @author Thomas Rudert
 * @author AXS (5.10.2007)
 */
public final class PathFinder {

    /**
     * Liefert alle <code>MetaPath</code>es, die zwischen Elementen der Art <code>startClass</code> und <code>endClass</code> definiert sind.
     *
     * @param startClass
     * @param endClass
     * @return
     */
    public static final Collection<MetaPath> getMetaPathes(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass) {
        PathsDefinition pathsDefinition = ModelConstants.getPathsDefinition();
        return pathsDefinition.getMetaPathes(startClass, endClass);
    }

    /**
     * @return
     */
    public static final Set<Class<? extends ModelElement>> getElementClassesInPathes() {
        PathsDefinition pathsDefinition = ModelConstants.getPathsDefinition();
        return pathsDefinition.getElementClassesInPathes();
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
     * @return NOTCONNECTED / FORWARD / BACKWARD / DOUBLE if path.isImmediate otherwise
     *         NOTCONNECTED / DOUBLE
     */
    public static final int isConnected(ModelElement element1, ModelElement element2, final MetaPath metaPath) {
        boolean searchParts = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS);
        boolean searchParents = UserProperties.is(BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS);
        if (!searchParts && !searchParents || element1 == element2 || metaPath.isHierarchyPath()) {
            return isConnected(element1, element2, metaPath, false);
        }
        int retVal = NOTCONNECTED;
        Set<ModelElement> list1 = new HashSet<>();
        Set<ModelElement> list2 = new HashSet<>();
        list1.add(element1);
        list2.add(element2);
        if (searchParts) {
            list1.addAll(element1.getPartElements());
            list2.addAll(element2.getPartElements());
        }
        if (searchParents) {
            list1.addAll(element1.getParentElements());
            list2.addAll(element2.getParentElements());
        }
        Iterator<ModelElement> iterator1 = list1.iterator();
        while (iterator1.hasNext()) {
            element1 = iterator1.next();
            Iterator<ModelElement> iterator2 = list2.iterator();
            while (iterator2.hasNext()) {
                element2 = iterator2.next();
                int con = isConnected(element1, element2, metaPath, false);
                switch (con) {
                case DOUBLE:
                    return DOUBLE;
                case FORWARD:
                    if (retVal == BACKWARD) {
                        return DOUBLE;
                    }
                    return FORWARD;
                case BACKWARD:
                    if (retVal == FORWARD) {
                        return DOUBLE;
                    }
                    return BACKWARD;
                }
            }
        }
        return retVal;
    }

    /**
     * @param element1
     * @param element2
     * @param metaPath
     * @param reverse
     * @return
     */
    private static final int isConnected(final ModelElement element1, final ModelElement element2, final MetaPath metaPath, final boolean reverse) {
        if (!metaPath.getStartClass().isAssignableFrom(element1.getClass()) || !metaPath.getEndClass().isAssignableFrom(element2.getClass())) {
            if (metaPath.getEndClass().isAssignableFrom(element1.getClass()) && metaPath.getStartClass().isAssignableFrom(element2.getClass())) {
                return isConnected(element2, element1, metaPath, !reverse);
            }
            return NOTCONNECTED;
        }

        for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
            // direkt vebunden?
            if (metaPath.isImmediate(pathIndex)) {
                Class<? extends Edge> edgeClass = metaPath.getEdgeClasses(pathIndex)[0];
                if (element1.isConnectedTo(element2, edgeClass) && element1.isConnectedFrom(element2, edgeClass)) {
                    return DOUBLE;
                } else if (element1.isConnectedTo(element2, edgeClass)) {
                    return reverse && metaPath.isDirectional() ? BACKWARD : FORWARD;
                } else if (element1.isConnectedFrom(element2, edgeClass)) {
                    return reverse && metaPath.isDirectional() ? FORWARD : IsPartOfEdge.class.isAssignableFrom(edgeClass) ? NOTCONNECTED : BACKWARD;
                }
            } else {
                switch (isConnected(element1, element2, metaPath, 0, pathIndex)) {
                case DOUBLE:
                    return DOUBLE;
                case FORWARD:
                    return reverse && metaPath.isDirectional() ? BACKWARD : FORWARD;
                case BACKWARD:
                    return reverse && metaPath.isDirectional() ? FORWARD : BACKWARD;
                }
            }
        }
        return NOTCONNECTED;
    }

    /**
     * @param current
     * @param end
     * @param metaPath
     * @param position
     * @param pathIndex
     * @return
     */
    private static final int isConnected(final ModelElement current, final ModelElement end, final MetaPath metaPath, final int position, final int pathIndex) {
        if (position == metaPath.getLength(pathIndex)) {
            if (current.equals(end)) {
                return DOUBLE;
            }
            return NOTCONNECTED;
        }
        int retVal;
        List<ModelElement> elements = current.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[position]);
        for (ModelElement me : elements) {
            if ((retVal = isConnected(me, end, metaPath, position + 1, pathIndex)) != NOTCONNECTED) {
                if (metaPath.getControl() == position) {
                    if (current.isConnectedTo(me) && current.isConnectedFrom(me)) {
                        return DOUBLE;
                    } else if (current.isConnectedTo(me)) {
                        return FORWARD;
                    } else if (current.isConnectedFrom(me)) {
                        return BACKWARD;
                    } else {
                        return NOTCONNECTED;
                    }
                }
                return retVal;
            }
        }
        return NOTCONNECTED;
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
    public static final Set<ModelElement> getDirectConnectedElements(final ModelElement me, final MetaPath metaPath) {
        Set<ModelElement> startElements = new HashSet<>();
        startElements.add(me);
        Set<ModelElement> endElements = new HashSet<>();
        for (int assoIndex = 0; assoIndex < metaPath.getLength(); assoIndex++) {
            for (int pathIndex = 0; pathIndex < metaPath.countPathes(); pathIndex++) {
                endElements.clear();
                for (ModelElement startElem : startElements) {
                    endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[assoIndex]));
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
    public static final Set<ModelElement> getConnectedElements(final ModelElement me, final MetaPath metaPath) {
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
    public static final Set<ModelElement> getConnectedElements(final ModelElement me, final Class<? extends ModelElement> targetElementClass, final MetaPath metaPath) {
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
                    endElements.addAll(startElem.getConnectedElementsByEdge(metaPath.getEdgeClasses(pathIndex)[index]));
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